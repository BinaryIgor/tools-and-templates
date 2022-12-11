package com.igor101.system.monitor.test;

import java.security.SecureRandom;
import java.util.Random;

public class Tests {

    public static final Random RANDOM = new SecureRandom();

    public static int randomInt(int from, int to) {
        return from + RANDOM.nextInt(to);
    }

}
