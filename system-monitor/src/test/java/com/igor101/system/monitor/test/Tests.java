package com.igor101.system.monitor.test;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tests {

    public static final Random RANDOM = new SecureRandom();
    private static final String[] RANDOM_STRING_CHARACTERS = "abcdfghijklmnopqrstuvwxyz0123456789".split("");

    public static int randomInt(int from, int to) {
        return from + RANDOM.nextInt(to);
    }

    public static <T> T randomElement(T[] elements) {
        var idx = randomInt(0, elements.length);
        return elements[idx];
    }

    public static String randomString(int from, int to) {
        return Stream.generate(() -> randomElement(RANDOM_STRING_CHARACTERS))
                .limit(randomInt(from, to))
                .collect(Collectors.joining());
    }

    public static String randomString() {
        return randomString(1, 50);
    }

}
