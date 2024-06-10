package com.istef.imageblur2024.filters;

import java.util.concurrent.RecursiveAction;
import java.util.function.BiConsumer;

/**
 * Recursive Task for use with a ForkJoin pool of threads
 * to speed up the parsing of input image for filtering
 */
class PixelForkTask extends RecursiveAction {

    private final int left;
    private final int right;
    private final BiConsumer<Integer, Integer> callback;
    private final int chunkLength;


    public PixelForkTask(int left, int right, int chunkLength, BiConsumer<Integer, Integer> callback) {
        this.left = left;
        this.right = right;
        this.callback = callback;
        this.chunkLength = chunkLength;
    }

    @Override
    protected void compute() {
        if (right - left < chunkLength) {
            callback.accept(left, right);
        } else {
            int mid = (left + right) / 2;
            invokeAll(new PixelForkTask(left, mid, chunkLength, callback),
                    new PixelForkTask(mid, right, chunkLength, callback));
        }
    }

}
