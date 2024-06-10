package com.istef.swing;


import com.istef.imageblur2024.exceptios.MyException;
import com.istef.imageblur2024.filters.ImgFilterBuilder;
import com.istef.imageblur2024.filters.ImgFilter;
import com.istef.imageblur2024.io.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * Swing App demo of the filters created in the ImageBlur2024 CLI project
 */
public class ImageFilterAppSwing extends JFrame {
    private JPanel paneImage;
    private JPanel paneButtons;
    private JLabel lblImage;
    private JLabel lblSpinner;

    private JPanel paneFilterOptions;
    private JButton btnAdd;
    private JPanel paneAddedFilters;
    private JLabel lblAddedFilters;

    private final String SPINNER = "/images/Spinner.gif";

    private enum InputType {FILE, URL, SEARCH}

    private BufferedImage inPic = null;
    private String inPicPath = null;
    private String inPicType = null;
    private BufferedImage outPic = null;

    private final ImgFilterBuilder[] enumFilters;
    private final String[] strFilters;

    private Map<Method, JTextField> selectedFilterOptions;
    private ImgFilter selectedFilter;
    private List<ImgFilter> addedFilters;

    public void run() {
        EventQueue.invokeLater(() -> {
            try {
                new ImageFilterAppSwing().setVisible(true);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public ImageFilterAppSwing() {
        enumFilters = ImgFilterBuilder.values();
        strFilters = new String[enumFilters.length];
        int i = 0;
        for (ImgFilterBuilder filter : enumFilters) {
            strFilters[i++] = filter.description();
        }

        configImagePane();
        configButtons();
        configFilters();
        configFrame();
    }

    private void configFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle("Image Filtering App");

        JPanel paneContent = new JPanel(null);
        paneContent.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(paneContent);

        paneContent.add(paneButtons);
        paneContent.add(paneImage);
    }

    private void configImagePane() {
        paneImage = new JPanel(null);
        paneImage.setBorder(new EtchedBorder());
        paneImage.setBounds(260, 10, 590, 550);
        lblImage = new JLabel();
        lblImage.setBounds(0, 0, 590, 548);
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblSpinner = new JLabel();
        lblSpinner.setBounds(0, 0, 590, 548);
        lblSpinner.setHorizontalAlignment(SwingConstants.CENTER);
        paneImage.add(lblImage);
        paneImage.add(lblSpinner);
    }

    private void configButtons() {
        JButton btnFile = new JButton("Choose File");
        btnFile.setBounds(20, 10, 200, 36);
        btnFile.addActionListener(e -> btnInputAction(InputType.FILE));

        JButton btnUrl = new JButton("Enter Image Link");
        btnUrl.setBounds(20, 50, 200, 36);
        btnUrl.addActionListener(e -> btnInputAction(InputType.URL));

        JButton btnSearch = new JButton("Google search");
        btnSearch.setBounds(20, 90, 200, 36);
        btnSearch.addActionListener(e -> btnInputAction(InputType.SEARCH));

        btnAdd = new JButton("+");
        btnAdd.setBounds(218, 183, 22, 22);
        btnAdd.addActionListener(e -> addFilter());

        JButton btnRun = new JButton("Apply Filters");
        btnRun.setBounds(20, 470, 200, 36);
        btnRun.addActionListener(e -> applyFilters());

        JButton btnSave = new JButton("Save Image");
        btnSave.setBounds(20, 510, 200, 36);
        btnSave.addActionListener(e -> saveImage());

        paneButtons = new JPanel(null);
        paneButtons.setBounds(5, 10, 252, 550);

        paneButtons.add(btnFile);
        paneButtons.add(btnUrl);
        paneButtons.add(btnSearch);
        paneButtons.add(btnAdd);
        paneButtons.add(btnRun);
        paneButtons.add(btnSave);
    }

    private void configFilters() {
        addedFilters = new ArrayList<>();
        paneFilterOptions = new JPanel();
        paneFilterOptions.setBorder(BorderFactory
                .createCompoundBorder(new EtchedBorder(), new EmptyBorder(2, 10, 4, 5)));
        paneFilterOptions.setLocation(24, 174);

        JComboBox<String> cbxFilters = new JComboBox<>(strFilters);
        cbxFilters.setBounds(20, 130, 204, 50);
        cbxFilters.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                for (int index = 0; index < strFilters.length; index++) {
                    if (strFilters[index].equals(e.getItem().toString())) {
                        selectedFilter = enumFilters[index].filter();
                        configFilterOptions(enumFilters[index].filter());
                        break;
                    }
                }
            }
        });
        selectedFilter = enumFilters[0].filter();
        configFilterOptions(enumFilters[0].filter());

        paneAddedFilters = new JPanel();
        paneAddedFilters.setBorder(BorderFactory
                .createCompoundBorder(new EtchedBorder(), new EmptyBorder(2, 10, 4, 5)));
        paneAddedFilters.setLocation(24, 410);
        paneAddedFilters.setSize(192, 50);
        lblAddedFilters = new JLabel("Selected Filters");
        lblAddedFilters.setFont(new Font("Verdana", Font.PLAIN, 11));
        lblAddedFilters.setBounds(36, 391, 100, 20);
        lblAddedFilters.setForeground(Color.DARK_GRAY);
        paneAddedFilters.setVisible(false);
        lblAddedFilters.setVisible(false);

        paneButtons.add(cbxFilters);
        paneButtons.add(paneFilterOptions);
        paneButtons.add(paneAddedFilters);
        paneButtons.add(lblAddedFilters);
        paneButtons.setComponentZOrder(lblAddedFilters, 0);
    }

    private void configFilterOptions(ImgFilter selectedFilter) {
        selectedFilterOptions = new HashMap<>();
        Method[] methods = optionMethods(selectedFilter);

        paneFilterOptions.removeAll();
        paneFilterOptions.setLayout(new GridLayout(methods.length, 0, 0, -2));
        int panelHeight = Math.max(methods.length * 32, 40);
        paneFilterOptions.setSize(192, panelHeight);
        btnAdd.setLocation(btnAdd.getX(), 163 + panelHeight / 2);

        for (Method m : methods) {
            JPanel filterOption = filterOption(m);
            paneFilterOptions.add(filterOption);
        }
        paneFilterOptions.revalidate();
    }

    private Method[] optionMethods(ImgFilter imgFilter) {
        Method[] declared = imgFilter.getClass().getMethods();
        Method[] methods = new Method[declared.length];
        int i = 0;
        for (Method m : declared) {
            boolean isPublic = (m.getModifiers() & Modifier.PUBLIC) != 0;
            String name = m.getName();
            if (isPublic && name.startsWith("set")) methods[i++] = m;
        }
        return Arrays.copyOf(methods, i);
    }

    private JPanel filterOption(Method m) {
        String showName = m.getName().substring(3);
        JLabel lblOption = new JLabel(showName);
        lblOption.setFont(new Font("Verdana", Font.PLAIN, 12));
        lblOption.setBounds(0, 0, 120, 30);

        JPanel paneInput = new JPanel(null);
        paneInput.setBorder(new EmptyBorder(0, 0, 0, 0));
        paneInput.setBounds(120, 0, 50, 30);
        JTextField txtOption = new JTextField();
        txtOption.setHorizontalAlignment(SwingConstants.RIGHT);
        txtOption.setBounds(0, 0, 50, 30);
        paneInput.add(txtOption);

        selectedFilterOptions.put(m, txtOption);

        JPanel paneOption = new JPanel(null);
        paneOption.setBorder(new EmptyBorder(4, 4, 4, 4));
        paneOption.add(lblOption);
        paneOption.add(paneInput);
        return paneOption;
    }

    private void btnInputAction(InputType inputType) {
        ImgLoader imgLoader = null;
        String errorMessage = "";
        switch (inputType) {
            case FILE:
                inPicPath = fileDialog();
                imgLoader = new ImgFileReader();
                errorMessage = "Could not read the file!";
                break;
            case URL:
                inPicPath = JOptionPane.showInputDialog("Enter/Paste image URL");
                imgLoader = new ImgUrlLoader();
                errorMessage = "Image Not Found!";
                break;
            case SEARCH:
                inPicPath = JOptionPane.showInputDialog("Search Google for image");
                imgLoader = new ImgSerpLoader();
                errorMessage = "Could not find appropriate image!";
        }
        if (inPicPath == null) return;

        lblImage.setText("");
        lblImage.setIcon(null);
        lblSpinner.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(SPINNER))));

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ImgLoader finalImgLoader = imgLoader;
        String finalErrorMessage = errorMessage;
        executorService.submit(() -> {
            try {
                inPic = finalImgLoader.load(inPicPath);
            } catch (MyException e) {
                System.err.println(e.getMessage());
                lblSpinner.setIcon(null);
                lblImage.setText(finalErrorMessage);
                return;
            }

            inPicType = finalImgLoader.getInputFileType();

            ImageIcon scaledIcon = fitImage(inPic);
            if (scaledIcon == null) {
                lblSpinner.setIcon(null);
                lblImage.setText(finalErrorMessage);
                return;
            }
            lblSpinner.setIcon(null);
            lblImage.setIcon(scaledIcon);
        });
        executorService.shutdown();
    }

    private String fileDialog() {
        FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String dir = dialog.getDirectory();
        String file = dialog.getFile();
        dialog.dispose();
        if (dir == null || file == null) return null;
        return dir + file;
    }

    private ImageIcon fitImage(BufferedImage inPic) {
        if (inPic == null) return null;

        int width = inPic.getWidth();
        int height = inPic.getHeight();
        double aspRatio = (double) width / height;

        int parentWidth = paneImage.getWidth();
        int parentHeight = paneImage.getHeight();
        double parentAspRatio = (double) parentWidth / parentHeight;

        if (parentAspRatio > aspRatio) {
            height = parentHeight;
            width = (int) (height * aspRatio);
        } else {
            width = parentWidth;
            height = (int) (width / aspRatio);
        }
        Image scaledPic = inPic.getScaledInstance(width, height, Image.SCALE_FAST);

        return new ImageIcon(scaledPic);
    }

    private void addFilter() {
        if (emptyInputs() || selectedFilter == null) return;

        selectedFilterOptions.forEach(this::setParameters);

        addedFilters.add(selectedFilter);
        int filterCount = addedFilters.size();

        paneAddedFilters.setVisible(true);
        lblAddedFilters.setVisible(true);

        String filterName = selectedFilter.getClass().getSimpleName();
        JLabel filter = new JLabel(filterName);
        filter.setFont(new Font("Verdana", Font.PLAIN, 12));

        if (addedFilters.size() > 1) {
            int yPane = paneAddedFilters.getLocation().y;
            paneAddedFilters.setLocation(24, yPane - (filterCount > 2 ? 32 : 14));
            paneAddedFilters.setSize(192, filterCount * 32);
            int yLbl = lblAddedFilters.getLocation().y;
            lblAddedFilters.setLocation(36, yLbl - (filterCount > 2 ? 32 : 14));
        }
        paneAddedFilters.setLayout(new GridLayout(filterCount, 0, 0, -2));
        paneAddedFilters.add(filter);
        paneAddedFilters.revalidate();
    }

    private void setParameters(Method method, JTextField txtField) {
        Class<?> type = method.getParameters()[0].getType();
        String value = txtField.getText();
        try {
            if (type.equals(int.class) || type.equals(Integer.class)) {
                method.invoke(selectedFilter, Integer.parseInt(value));
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                method.invoke(selectedFilter, Float.parseFloat(value));
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                method.invoke(selectedFilter, Double.parseDouble(value));
            } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                method.invoke(selectedFilter, Boolean.parseBoolean(value));
            } else {
                method.invoke(selectedFilter, value);
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void applyFilters() {

        lblImage.setText("");
        lblSpinner.setIcon(new ImageIcon(SPINNER));
        paneImage.setComponentZOrder(lblSpinner, 0);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            ImgFilter[] filterArr = addedFilters.toArray(new ImgFilter[0]);
            outPic = multiFilter(inPic, filterArr);

            ImageIcon scaledIcon = fitImage(outPic);
            if (scaledIcon == null) {
                lblSpinner.setIcon(null);
                lblImage.setText("Error processing the image!");
                return;
            }
            lblSpinner.setIcon(null);
            lblImage.setIcon(scaledIcon);
        });
    }

    private BufferedImage multiFilter(BufferedImage src, ImgFilter... filters) {
        BufferedImage out = null;

        for (int i = 0; i < filters.length; i++) {
            if (i == 0) out = filters[i].filter(src);
            else out = filters[i].filter(out);
        }
        return out;
    }

    private boolean emptyInputs() {
        for (Component paneOption : paneFilterOptions.getComponents()) {
            for (Component paneInput : ((JPanel) paneOption).getComponents()) {
                if (paneInput instanceof JPanel) {
                    JTextField txtField = (JTextField) ((JPanel) paneInput).getComponent(0);
                    txtField.addFocusListener(new FocusListener() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            JPanel paneInput = (JPanel) txtField.getParent();
                            paneInput.setBorder(new EmptyBorder(0, 0, 0, 0));
                        }

                        @Override
                        public void focusLost(FocusEvent e) {
                        }
                    });
                    if (txtField.getText().trim().isEmpty()) {
                        ((JPanel) paneInput).setBorder(new LineBorder(Color.red, 1));
                        return true;
                    }
                }
            }

        }
        return false;
    }

    private void saveImage() {
        if (outPic == null) return;

        boolean inFilePathIsUrl = inPicPath.startsWith("http://") || inPicPath.startsWith("https://");
        JFileChooser fileChooser = inFilePathIsUrl ? new JFileChooser() : new JFileChooser(inPicPath);
        fileChooser.setSelectedFile(new File("Untitled." + inPicType));
        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        String outputFilePath = fileChooser.getSelectedFile().getAbsolutePath();

        ImgFileWriter imgFileWriter = new ImgFileWriter();
        imgFileWriter.persist(outPic, outputFilePath);
    }

}