package com.asgarov.references_sorter.util;

import com.asgarov.references_sorter.domain.DocumentWrapper;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;

public class FileUtil {

    @SneakyThrows
    public static String readFile(String fileName) {
        try(Stream<String> lines = Files.lines(Paths.get(fileName))) {
            return lines.collect(Collectors.joining(lineSeparator()));
        }
    }

    @SneakyThrows
    public static void writeToFile(String pathToFile, DocumentWrapper document) {
        try (var writer = new BufferedWriter(new FileWriter(pathToFile))) {
            writer.write(document.getBodyText());
            writer.write("References" + lineSeparator());
            writer.write(document.getReferences());
        }
    }

    public static String updateFileName(String pathToFile, String postfix) {
        int extensionIndex = pathToFile.lastIndexOf(".");
        StringBuilder stringBuilder = new StringBuilder(pathToFile);
        return stringBuilder.replace(extensionIndex, extensionIndex + 1, postfix + ".").toString();
    }
}
