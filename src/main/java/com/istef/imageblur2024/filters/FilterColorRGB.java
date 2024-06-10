package com.istef.imageblur2024.filters;

import com.istef.imageblur2024.exceptios.MyException;

import java.awt.image.*;

/**
 * A filter which removes two RGB colors from each pixel, except the selected color.
 */
public class FilterColorRGB implements ImgFilter {

    private String rgbColor;

    protected FilterColorRGB() {
    }

    /**
     * Construct a ColorRGB filter.
     *
     * @param rgbColor the color that should remain in each pixel.
     *                 Colors allowed: Red, Green and Blue
     */
    public FilterColorRGB(String rgbColor) throws MyException {
        this.setRgbColor(rgbColor);
    }

    /**
     * Set the left edge of the crop rectangle.
     *
     * @param rgbColor the color that should remain in each pixel.
     *                 Colors allowed: Red, Green and Blue
     */
    public void setRgbColor(String rgbColor) throws MyException {
        String upperCase = rgbColor.trim().toUpperCase();
        if (upperCase.equals("RED") || upperCase.equals("GREEN") || upperCase.equals("BLUE")) {
            this.rgbColor = upperCase;
        } else {
            throw new MyException("Wrong input color! Allowed colors: Red, Green and Blue.");
        }
    }

    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();

        if (dst == null)
            dst = createCompatibleDestImage(src);

        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        src.getRGB(0, 0, width, height, inPixels, 0, width);

        for (int i = 0; i < inPixels.length; i++) {
            int red = 0xff00ffff;
            int green = 0xffff00ff;
            int blue = 0xffffff00;
            switch (rgbColor) {
                case "RED":
                    outPixels[i] = inPixels[i] & blue & green;
                    break;
                case "GREEN":
                    outPixels[i] = inPixels[i] & red & blue;
                    break;
                case "BLUE":
                    outPixels[i] = inPixels[i] & red & green;
            }
        }

        dst.setRGB(0, 0, width, height, outPixels, 0, width);
        return dst;
    }

}

