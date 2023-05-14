package io.codyn.test;

import io.codyn.types.Pair;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

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

    public static void awaitCondition(Condition condition, Supplier<String> message) {
        awaitCondition(condition, 5000, message);
    }

    public static void awaitCondition(Condition condition) {
        awaitCondition(condition, () -> "Condition not satisfied");
    }

    public static void awaitCondition(Condition condition, long millis, Supplier<String> message) {
        var roundsAndWait = computeRoundsAndWait(millis);
        var rounds = roundsAndWait.first();
        var wait = roundsAndWait.second();

        var satisfied = false;

        for (int i = 0; i < rounds; i++) {
            try {
                Thread.sleep(wait);
                satisfied = condition.compute();
                if (satisfied) {
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Assertions.assertTrue(satisfied, message);
    }

    private static Pair<Integer, Long> computeRoundsAndWait(long millis) {
        if (millis <= 100) {
            return new Pair<>(1, 100L);
        }

        if (millis <= 1000) {
            return new Pair<>(3, millis / 3);
        }

        var rounds = 10;
        var wait = millis / rounds;

        return new Pair<>(rounds, wait);
    }

    public interface Condition {
        boolean compute() throws Exception;
    }
}
