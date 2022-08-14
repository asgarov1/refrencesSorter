package com.asgarov.references_sorter.util;

import com.asgarov.references_sorter.domain.DocumentWrapper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;

public class FileUtil {
    public static String readFile(String fileName) {
        try {
            return Files.lines(Paths.get(fileName)).collect(Collectors.joining(lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeToFile(String pathToFile, DocumentWrapper document) {
        try (var writer = new BufferedWriter(new FileWriter(pathToFile))) {
            writer.write(document.getBodyText());
            writer.write("References" + lineSeparator());
            writer.write(document.getReferences());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String updateFileName(String pathToFile, String postfix) {
        int extensionIndex = pathToFile.lastIndexOf(".");
        StringBuilder stringBuilder = new StringBuilder(pathToFile);
        return stringBuilder.replace(extensionIndex, extensionIndex + 1, postfix + ".").toString();
    }
}
