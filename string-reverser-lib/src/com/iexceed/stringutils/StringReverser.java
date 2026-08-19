package com.iexceed.stringutils;

public class StringReverser {
    public static String reverse(String input) {
        if (input == null) return null;
        return new StringBuilder(input).reverse().toString();
    }
}
