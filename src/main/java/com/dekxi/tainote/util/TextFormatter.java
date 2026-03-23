package com.dekxi.tainote.util;

public class TextFormatter {
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                titleCase.append(c);
                nextTitleCase = true;
            } else {
                if (nextTitleCase) {
                    titleCase.append(Character.toTitleCase(c));
                    nextTitleCase = false;
                } else {
                    titleCase.append(Character.toLowerCase(c));
                }
            }
        }
        return titleCase.toString();
    }
}
