package com.istef.imageblur2024.filters;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.security.InvalidParameterException;

/**
 * A filter which crops an image to a given rectangle.
 */
public class FilterCrop implements ImgFilter {

    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height = 0;


    /**
     * Construct a Crop filter with zero rectangle size.
     */
    protected FilterCrop() {
    }

    /**
     * Construct a Crop filter.
     * @param x         the left edge of the crop rectangle
     * @param y         the top edge of the crop rectangle
     * @param width     the width of the crop rectangle
     * @param height    the height of the crop rectangle
     */
    public FilterCrop(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    /**
     * Set the left edge of the crop rectangle.
     * @param x the left edge of the crop rectangle.
     *          The X axis origin is the top left corner of the image
     *          with direction to the right
     */
    public void setX(int x) {
        this.x = Math.max(x, 0);
    }

    /**
     * Set the top edge of the crop rectangle.
     * @param y the top edge of the crop rectangle.
     *          The Y axis origin is the top left corner of the image
     *          with downwards direction
     */
    public void setY(int y) {
        this.y = Math.max(y, 0);
    }

    /**
     * Set the width of the crop rectangle.
     * @param width the width of the crop rectangle
     */
    public void setWidth(int width) {
        this.width = Math.max(width, 0);
    }

    /**
     * Set the height of the crop rectangle.
     * @param height the height of the crop rectangle
     */
    public void setHeight(int height) {
        this.height = Math.max(height, 0);
    }


    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int w = src.getWidth();
        int h = src.getHeight();

        try {
            validateInput(w, h);
        } catch (InvalidParameterException e) {
            return src;
        }

        ColorModel dstCM = src.getColorModel();
        dst = new BufferedImage(dstCM,
                dstCM.createCompatibleWritableRaster(width, height),
                dstCM.isAlphaPremultiplied(),
                null);

        Graphics2D g = dst.createGraphics();
        g.drawRenderedImage(src, AffineTransform.getTranslateInstance(-x, -y));
        g.dispose();

        return dst;
    }

    private void validateInput(int imgWidth, int imgHeight) throws InvalidParameterException {
        if ((x > imgWidth) || (y > imgHeight)) {
            throw new InvalidParameterException("Top left corner (and the entire crop rectangle) out of the image bounds!");
        }
        if (width == 0 || height == 0) {
            throw new InvalidParameterException("Invalid Crop rectangle side value!");
        }
    }

}
