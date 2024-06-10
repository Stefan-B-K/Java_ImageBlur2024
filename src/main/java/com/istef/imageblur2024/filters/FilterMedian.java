package com.istef.imageblur2024.filters;

import java.util.*;


public class FilterMedian extends AbstractFilterSquareKernel {

    /**
     * Construct a Median Blur filter with blur radius of 2 pixels.
     */
    protected FilterMedian() {this.setRadius(2);}

    /**
     * Construct a Median Blur filter.
     * @param radius blur radius in pixels
     * @min-value 0
     * @max-value 20
     * The radius of the kernel defines the amount of blur.
     * The bigger the radius, the longer this filter will take.
     */
    public FilterMedian(int radius) {
        setRadius(radius);
    }


    @Override
    protected int outputPixelRGB(int inPixelValue, int[] kernelValues) {
        return median(kernelValues);
    }

    private int median(int[] kernelValues) {
        Arrays.sort(kernelValues);
        return kernelValues[kernelValues.length / 2];
    }

}

