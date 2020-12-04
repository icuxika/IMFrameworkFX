package com.jfoenix.util;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class JFXUtil {

    /**
     * This method is used to run a specified Runnable in the FX Application thread,
     * it returns before the task finished execution
     *
     * @param doRun This is the sepcifed task to be excuted by the FX Application thread
     * @return Nothing
     */
    public static void runInFX(Runnable doRun) {
        if (Platform.isFxApplicationThread()) {
            doRun.run();
            return;
        }
        Platform.runLater(doRun);
    }

    /**
     * This method is used to run a specified Runnable in the FX Application thread,
     * it waits for the task to finish before returning to the main thread.
     *
     * @param doRun This is the sepcifed task to be excuted by the FX Application thread
     * @return Nothing
     */
    public static void runInFXAndWait(Runnable doRun) {
        if (Platform.isFxApplicationThread()) {
            doRun.run();
            return;
        }
        final CountDownLatch doneLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                doRun.run();
            } finally {
                doneLatch.countDown();
            }
        });
        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static <T> T[] concat(T[] a, T[] b, Function<Integer, T[]> supplier) {
        final int aLen = a.length;
        final int bLen = b.length;
        T[] array = supplier.apply(aLen + bLen);
        System.arraycopy(a, 0, array, 0, aLen);
        System.arraycopy(b, 0, array, aLen, bLen);
        return array;
    }
}
