package com.asgarov.references_sorter;

import com.asgarov.references_sorter.util.FileUtil;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Sorter {

    private static final String REFERENCES_REGEX = "References\\s*";

    public static void sort(String pathToFile) {
        String fileText = FileUtil.readFile(pathToFile);

        String references = getReferences(fileText);
        StringWrapper bodyText = new StringWrapper(getMainText(fileText));

        String updatedReferences = replaceReferencesNumbers(references, bodyText);

        System.out.println(bodyText.getData());
    }

    private static String replaceReferencesNumbers(String references, StringWrapper bodyText) {
        return Arrays.stream(references.split(System.lineSeparator()))
                .filter(Predicate.not(String::isEmpty))
                .map(line -> updateNumber(line, bodyText))
                .collect(Collectors.joining());
    }

    private static String updateNumber(String line, StringWrapper bodyText) {
        if (!line.trim().startsWith("[")) {
            return line;
        }

        int endIndex = line.indexOf("]");
        String referenceNumber = line.substring(0, endIndex);
        String updatedReferenceNumber = referenceNumber.replace("[", "[abc");

        bodyText.setData(bodyText.getData().replace(referenceNumber, updatedReferenceNumber));
        return line.replace(referenceNumber, updatedReferenceNumber);
    }

    private static String getMainText(String fileText) {
        return fileText.split(REFERENCES_REGEX)[0];
    }

    private static String getReferences(String fileText) {
        return fileText.split(REFERENCES_REGEX)[1];
    }

    public static void main(String[] args) {
        Sorter.sort("src/main/resources/assignment.txt");
    }
}
