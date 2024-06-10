package com.istef.imageblur2024.filters;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;


/**
 * A convenience class which implements the common method for parsing the pixels
 * and generating the kernel values for further processing
 */
public abstract class AbstractFilterSquareKernel implements ImgFilter {
    private int radius;
    private int kernelSize;

    /**
     * A convenience method for filter processing the kernel matrix
     * and generating the output pixel value
     * @param inPixelValue   a BufferedImage object
     * @param kernelValues   the left edge of the pixel block
     * @see #filter(BufferedImage, BufferedImage)
     */
    abstract protected int outputPixelRGB(int inPixelValue, int[] kernelValues);

    protected AbstractFilterSquareKernel() {}

    /**
     * Set the radius of the kernel, and hence the amount of blur.
     * The bigger the radius, the longer this filter will take.
     * @param radius    the radius of the blur in pixels.
     * @min-value       0
     */
    public void setRadius(int radius) {
        this.radius = radius;
        this.kernelSize = 2 * radius + 1;
    }

    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();

        if (dst == null)
            dst = createCompatibleDestImage(src);

        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        src.getRGB(0, 0, width, height, inPixels, 0, width);
        
        try {
            ForkJoinPool pool = new ForkJoinPool(2);
            int threads = 8;
            int chunkLength = inPixels.length / threads;

            Future<?> future = pool.submit(new PixelForkTask(0, inPixels.length, chunkLength, (left, right) -> {
                for (int pixelIndex = left; pixelIndex < right; pixelIndex++) {
                    int[] kernelValues = new int[kernelSize * kernelSize];
                    int i = 0;
                    int row = pixelIndex / width;
                    int col = pixelIndex % width;
                    for (int r = row - radius; r <= row + radius; r++) {
                        if (r < 0 || r >= height) continue;
                        for (int c = col - radius; c <= col + radius; c++) {
                            if (c < 0 || c >= width) continue;
                            kernelValues[i++] = inPixels[r * width + c];
                        }
                    }
                    int[] nonZeroValues = Arrays.copyOf(kernelValues, i);
                    outPixels[pixelIndex] = outputPixelRGB(inPixels[pixelIndex], nonZeroValues);
                }
            }));

            future.get();

        } catch (InterruptedException | ExecutionException e) {
            System.err.println(e.getCause().getMessage());
            System.err.println(e.getMessage());
        } catch (Error e) {
            System.err.println(e.getMessage());
        }

        dst.setRGB(0, 0, width, height, outPixels, 0, width);
        return dst;
    }
}

