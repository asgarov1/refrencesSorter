package com.asgarov.references_sorter.util;

public class TextUtil {
    private static final String REFERENCES_REGEX = "References\\s*";

    public static String getMainText(String fileText) {
        return fileText.split(REFERENCES_REGEX)[0];
    }

    public static String getReferences(String fileText) {
        return fileText.split(REFERENCES_REGEX)[1];
    }
}
