package com.istef.imageblur2024.io;

import java.awt.image.BufferedImage;

public interface ImgWriter {
    default boolean persist(BufferedImage out) {
        return false;
    }

    default boolean persist(BufferedImage out, String outputFilePath) {
        return false;
    }
}
