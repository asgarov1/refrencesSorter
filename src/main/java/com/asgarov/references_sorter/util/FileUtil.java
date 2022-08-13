package com.asgarov.references_sorter.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileUtil {
    public static String readFile(String fileName) {
        try {
            return Files.lines(Paths.get(fileName)).collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
