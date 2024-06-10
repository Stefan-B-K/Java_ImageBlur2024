package com.istef.imageblur2024.io;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImgFileWriter implements ImgWriter {

    @Override
    public boolean persist(BufferedImage out, String outputFilePath) {
        File file = new File(outputFilePath);
        String fileName = file.getName();
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
        try {
            ImageIO.write(out, fileExt, file);
            return true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
