package org.doraemon.treasure.gear.util;

import java.util.Random;

public class Randomness {
    private static final Random random = new Random();

    public static String uniquify(String prefix) {
        return prefix + "_" + random.nextInt(10240);
    }

    public static long nextLong() {
        return random.nextLong();
    }

    public static boolean nextBoolean() {
        return random.nextBoolean();
    }

    public static int nextInt() {
        return random.nextInt();
    }

    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }
}
