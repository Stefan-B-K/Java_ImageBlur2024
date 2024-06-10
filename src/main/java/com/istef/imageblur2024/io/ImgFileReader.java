package com.istef.imageblur2024.io;

import com.istef.imageblur2024.exceptios.MyException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImgFileReader implements ImgLoader {
    private String inputFileType = "";

    public String getInputFileType() {
        return inputFileType;
    }

    @Override
    public BufferedImage load(String filePath) throws MyException {
        try {
            File file = new File(filePath);
            inputFileType = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            return ImageIO.read(new File(filePath));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            throw new MyException("Could not load image file from the given path: " + filePath);
        }
    }
}
