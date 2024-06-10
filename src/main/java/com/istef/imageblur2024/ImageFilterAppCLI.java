package com.istef.imageblur2024;

import com.istef.imageblur2024.exceptios.MyException;
import com.istef.imageblur2024.filters.*;
import com.istef.imageblur2024.io.*;

import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.*;


public class ImageFilterAppCLI {
    private List<String> availableFilters;
    private String inputFilters;
    private String inputImageType;
    private List<ImgFilter> filters;
    private final String FILE_OUT_PATH = "src/main/resources/images";

    public void run() {
        try {
            showInstructions();
            BufferedImage inputImage = composeInputFile();
            filters = composeFilters();
            processImage(inputImage, FILE_OUT_PATH);
        } catch (MyException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Show list of available filters and the expected input format.
     */
    private void showInstructions() throws MyException {
        String delimiter = makeDelimiter('=', 70);
        String projectName = Paths.get(System.getProperty("user.dir")).getFileName().toString();
        int gapLength = (delimiter.length() - projectName.length()) / 2;
        String gap = " ".repeat(gapLength);

        System.out.println(delimiter);
        System.out.println(gap + projectName + gap);
        System.out.println(delimiter);
        System.out.println("Available filters:");
        availableFilters = listAvailableFilters();
        System.out.println(delimiter);
        System.out.println("Space-delimited input format for applying filters to an image:");
        System.out.println("\t<imagePath> <filterName> <paramValue> <paramValue> ...");
        System.out.println("Where <imagePath> can be an absolute path to a local file,\n an URL or a word to search for in google.");
        System.out.println("Example input: C:/downloads/MyImage.png ");
        System.out.println("\tC:/downloads/MyImage.png FilterCrop 30 40 400 600 filterGaussian 22");
        System.out.println(delimiter);
    }


    /**
     * Read the input line from CLI and load the image
     *
     * @return the image loaded from the provided imagePath
     */
    private @Nullable BufferedImage composeInputFile() throws MyException {
        Scanner sc = new Scanner(System.in);
        String inputLine = sc.nextLine();
        sc.close();

        String[] filePathAndFilters = inputLine.trim().split("\\s+", 2);
        String imagePath = filePathAndFilters[0];
        inputFilters = filePathAndFilters[1];
        boolean isUrl = imagePath.startsWith("http://") || imagePath.startsWith("https://");
        boolean isSearchItem = !imagePath.contains("/");
        ImgLoader imgLoader = isUrl
                ? new ImgUrlLoader()
                : (isSearchItem ? new ImgSerpLoader() : new ImgFileReader());
        BufferedImage src = imgLoader.load(imagePath);
        inputImageType = imgLoader.getInputFileType();
        return src;
    }

    /**
     * Compose a list of filters, instantiated with the provided input parameters
     *
     * @return List of ImgFilter instances
     */
    private List<ImgFilter> composeFilters() throws MyException {
        List<ImgFilter> filters = new ArrayList<>();
        String[] filtersArray = inputFilters.split("\\s+");

        int inputIndex = 0;
        while (inputIndex < filtersArray.length) {
            String filterName = filtersArray[inputIndex++].toLowerCase();
            if (!availableFilters.contains(filterName)) {
                throw new MyException("Invalid filter name!");
            }
            for (ImgFilterBuilder filter : ImgFilterBuilder.values()) {
                String constructorString = filter.constructorString();
                String className = constructorName(constructorString);
                if (!className.equals(filterName)) continue;

                Constructor<?> constructor = publicConstructor(filter.type());
                int paramsCount = constructor.getParameterCount();
                Object[] params = null;
                if (paramsCount > 0) {
                    params = new Object[paramsCount];
                    Class<?>[] paramTypes = constructor.getParameterTypes();
                    for (int i = 0; i < paramsCount; i++) {
                        params[i] = parseToType(paramTypes[i], filtersArray[inputIndex++]);
                    }
                }

                try {
                    ImgFilter imgFilter = (ImgFilter) (paramsCount > 0
                            ? constructor.newInstance(params)
                            : constructor.newInstance());
                    filters.add(imgFilter);
                } catch (ReflectiveOperationException e) {
                    if (e.getCause() instanceof MyException) throw (MyException) e.getCause();
                    throw new MyException("Failed to make an instance of " + filter.type().getName());
                }
            }
        }
        return filters;
    }

    /**
     * Apply the filters in the given input order and save the image
     *
     * @param inputImage   the input image
     * @param outputFolder the path to the folder, where the processed image is to be saved
     */
    private void processImage(BufferedImage inputImage, String outputFolder) throws MyException {
        ImgFilter[] filterArr = filters.toArray(new ImgFilter[0]);
        BufferedImage out = multiFilter(inputImage, filterArr);
        ImgFileWriter imgFileWriter = new ImgFileWriter();

        String lastChar = outputFolder.trim().substring(outputFolder.trim().length() - 1);
        if (!lastChar.equals("/")) outputFolder += '/';
        String fileOut = outputFolder + "Untitled." + inputImageType;
        if (!imgFileWriter.persist(out, fileOut))
            throw new MyException("Failed to save the image!");
    }


    //region ============= Helper methods =============">

    private List<String> listAvailableFilters() throws MyException {
        List<String> availableFilters = new ArrayList<>();
        for (ImgFilterBuilder filter : ImgFilterBuilder.values()) {
            String constructorString = filter.constructorString();
            if (!constructorString.isEmpty()) {
                availableFilters.add(constructorName(constructorString));
                System.out.println('\t' + constructorString);
            }
        }
        if (availableFilters.isEmpty()) throw new MyException("No filters available!");
        return availableFilters;
    }

    private String makeDelimiter(char symbol, int length) {
        return String.valueOf(symbol).repeat(length);
    }

    private Constructor<?> publicConstructor(Class<?> clazz) throws MyException {
        Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
        for (Constructor<?> c : declaredConstructors) {
            if (c.getModifiers() == Modifier.PUBLIC) return c;
        }
        throw new MyException("Could not find public constructor of " + clazz.getName());
    }

    private String constructorName(String constructorString) {
        return constructorString.split("\\(", 2)[0].trim().toLowerCase();
    }

    private Object parseToType(Class<?> type, String value) throws MyException {
        try {
            if (type.equals(int.class) || type.equals(Integer.class)) {
                return Integer.parseInt(value);
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                return Float.parseFloat(value);
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                return Double.parseDouble(value);
            } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                return Boolean.parseBoolean(value);
            }
        } catch (NumberFormatException e) {
            throw new MyException("Wrong parameter type: " + type.getName() + ' ' + value);
        }

        return value;
    }

    private BufferedImage multiFilter(BufferedImage src, ImgFilter... filters) {
        BufferedImage out = null;

        for (int i = 0; i < filters.length; i++) {
            if (i == 0) out = filters[i].filter(src);
            else out = filters[i].filter(out);
        }
        return out;
    }

    //endregion

}
