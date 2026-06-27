package com.enriquebarragan.urlshortener.util;

public class Base62Encoder {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    public static String encode(long number) {
        if (number == 0) {
            return String.valueOf(ALPHABET.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        long n = number;

        while (n > 0) {
            int remainder = (int) (n % BASE);
            sb.append(ALPHABET.charAt(remainder));
            n /= BASE;
        }

        return sb.reverse().toString();
    }

    public static long decode(String code) {
        long result = 0;

        for (char c : code.toCharArray()) {
            result = result * BASE + ALPHABET.indexOf(c);
        }

        return result;
    }
}