package com.istef.imageblur2024.filters;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * Definition of a filter compatibility for use in the ImageBlur2024 project
 */
public interface ImgFilter {

    /**
     * The method signature used in the JH Labs filters.
     * @param src the input (original) image
     * @param dst the image processed by the implemented filter
     * @return    the image processed by the implemented filter
     */
    BufferedImage filter(BufferedImage src, BufferedImage dst);

    /**
     * Apply filter to the image.
     *
     * @param src the input (original) image
     * @return    the image processed by the implemented filter
     */
    default BufferedImage filter(BufferedImage src) {
        return filter(src, null);
    }

    default BufferedImage createCompatibleDestImage(BufferedImage src) {
        ColorModel dstCM = src.getColorModel();
        return new BufferedImage(dstCM,
                dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()),
                dstCM.isAlphaPremultiplied(),
                null);
    }
}
