package com.github.pesennik.util;

public class HashUtils {
    private static volatile long lastHashSeed = System.currentTimeMillis();

    public HashUtils() {
        super();
    }

    public static synchronized String generateRandomUid() {
        long seed = System.currentTimeMillis();
        if (seed == lastHashSeed) {
            seed++;
        }
        lastHashSeed = seed;
        return DigestUtils.md5DigestAsHex(("" + seed).getBytes());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static boolean isValidHash(String hash) {
        return hash.length() == 32;
    }
}
