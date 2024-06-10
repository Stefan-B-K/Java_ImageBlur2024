package com.istef.imageblur2024.filters;

import java.awt.image.BufferedImage;


public class FilterMeanAlpha extends AbstractFilterSquareKernel {

    /**
     * Construct a Mean-Alpha Blur filter with blur radius of 5 pixels.
     */
    protected FilterMeanAlpha() {this.setRadius(5);}

    /**
     * Construct a Mean-Alpha Blur filter.
     * @param radius blur radius in pixels
     * @min-value 0
     * @max-value 50
     * The radius of the kernel defines the amount of blur.
     * The bigger the radius, the longer this filter will take.
     */
    public FilterMeanAlpha(int radius) {
        setRadius(radius);
    }


    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (!src.getColorModel().hasAlpha()) return src;
        return super.filter(src, dst);
    }

    @Override
    protected int outputPixelRGB(int inPixelValue, int[] kernelValues) {
        return meanAlpha(inPixelValue, kernelValues);
    }

    private int meanAlpha(int inPixelValue, int[] kernelValues) {
        int sumAlpha = 0;

        int[] alphas = new int[kernelValues.length];

        for (int i = 0; i < kernelValues.length; i++) {
            alphas[i] = (kernelValues[i] >> 24) & 0xff;
            sumAlpha += alphas[i];
        }

        int meanAlpha = ((sumAlpha / kernelValues.length) << 24) | 0x00ffffff;

        return inPixelValue & meanAlpha;
    }

}

