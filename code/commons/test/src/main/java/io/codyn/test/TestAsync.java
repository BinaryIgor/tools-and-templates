package io.codyn.test;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestAsync {

    public static CountDownLatch newLatch(int count) {
        return new CountDownLatch(count);
    }

    public static CountDownLatch newLatch() {
        return newLatch(1);
    }

    public static void waitForCountDown(int seconds, CountDownLatch latch) {
        try {
            if (!latch.await(seconds, TimeUnit.SECONDS)) {
                throw new RuntimeException(String.format("Wait time of %d seconds exceeded", seconds));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void waitForCountDown(CountDownLatch latch) {
        waitForCountDown(3, latch);
    }
}
