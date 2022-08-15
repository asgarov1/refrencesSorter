package com.asgarov.references_sorter.util;

public class TextUtil {
    private static final String REFERENCES_REGEX = "References\\s*";

    public static String getMainText(String fileText) {
        return fileText.split(REFERENCES_REGEX)[0];
    }

    public static String getReferences(String fileText) {
        String[] fileSplit = fileText.split(REFERENCES_REGEX);
        if (fileSplit.length < 2) {
            throw new IllegalStateException("Can't find references: File doesn't contain a heading with value \"References\"");
        }
        return fileSplit[1];
    }
}
