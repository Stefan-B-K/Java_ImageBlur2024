package com.istef.imageblur2024.filters;


/**
 * Available filters for use in ImageBlur_2024 project
 */
public enum ImgFilterBuilder {
    BLUR_GAUSSIAN(FilterGaussian.class, "Gaussian Blur", "FilterGaussian(int radius)"),
    BLUR_MEDIAN(FilterMedian.class, "Median Blur", "FilterMedian(int radius)"),
    MEAN_ALPHA(FilterMeanAlpha.class, "Mean Alpha", "FilterMeanAlpha(int radius)"),
    CROP(FilterCrop.class, "Crop", "FilterCrop(int x, int y, int width, int height)"),
    COLOR_RGB(FilterColorRGB.class, "Color (RGB)", "FilterColorRGB(String rgbColor)");

    private final Class<?> type;
    private final String description;
    private final String constructorString;

    ImgFilterBuilder(Class<?> type, String description, String constructorString) {
        this.type = type;
        this.description = description;
        this.constructorString = constructorString;
    }

    public Class<?> type() {
        return type;
    }

    public String description() {
        return description;
    }

    public String constructorString() {
        return constructorString;
    }

    /**
     * Cast to concrete type to configure the filter
     */
    public ImgFilter filter() {
        switch (this) {
            case BLUR_GAUSSIAN:
                return new FilterGaussian();
            case BLUR_MEDIAN:
                return new FilterMedian();
            case MEAN_ALPHA:
                return new FilterMeanAlpha();
            case CROP:
                return new FilterCrop();
            case COLOR_RGB:
                return new FilterColorRGB();
            default:
                return new FilterGaussian();
        }
    }

}


