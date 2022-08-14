package com.asgarov.references_sorter.sorter;

import com.asgarov.references_sorter.domain.DocumentWrapper;
import com.asgarov.references_sorter.util.FileUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.asgarov.references_sorter.constants.Constants.*;
import static com.asgarov.references_sorter.util.FileUtil.updateFileName;
import static com.asgarov.references_sorter.util.TextUtil.getMainText;
import static com.asgarov.references_sorter.util.TextUtil.getReferences;
import static java.lang.System.lineSeparator;

/**
 * This class can only sort references in .txt files
 *  - use {@link MicrosoftWordSorter} to sort references in Microsoft Word documents
 */
public class TxtSorter {

    public static void sort(String pathToFile) {
        String fileText = FileUtil.readFile(pathToFile);
        DocumentWrapper document = new DocumentWrapper(getMainText(fileText), getReferences(fileText));

        prefixReferencesNumbersWith(document, PROCESSING_PREFIX);
        replaceReferencesBackToNumbers(document);
        sortReferences(document);

        FileUtil.writeToFile(updateFileName(pathToFile, "_updated"), document);
    }

    private static void sortReferences(DocumentWrapper document) {
        final Pattern pattern = Pattern.compile(REFERENCE_REGEX, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(document.getReferences());

        String sortedReferences = matcher.results()
                .map(MatchResult::group)
                .sorted(Comparator.comparingInt(TxtSorter::getReferenceNum))
                .collect(Collectors.joining(lineSeparator() + lineSeparator()));

        document.setReferences(sortedReferences);
    }

    private static int getReferenceNum(String line) {
        int endIndex = line.indexOf("]");
        String referenceNumber = line.trim().substring(1, endIndex);
        return Integer.parseInt(referenceNumber);
    }

    private static void replaceReferencesBackToNumbers(DocumentWrapper document) {
        final Pattern pattern = Pattern.compile(REFERENCE_WORD_REGEX, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(document.getBodyText());

        int refCount = 1;
        while (matcher.find()) {
            String matchedReference = matcher.group(0);

            document.replaceInAll(matchedReference, lineSeparator() + lineSeparator() + "[" + refCount++ + "]");

            // update matches
            matcher = pattern.matcher(document.getBodyText());
        }

        document.setReferences(document.getReferences() + lineSeparator() + lineSeparator());
    }

    private static void prefixReferencesNumbersWith(DocumentWrapper document, String prefix) {
        String updatedReferences = Arrays.stream(document.getReferences().split(lineSeparator()))
                .filter(Predicate.not(String::isEmpty))
                .map(line -> updateNumber(line, document, prefix))
                .collect(Collectors.joining());

        document.setReferences(updatedReferences);
    }

    private static String updateNumber(String line, DocumentWrapper document, String prefix) {
        if (!line.trim().startsWith("[")) {
            return line;
        }

        int endIndex = line.indexOf("]");
        String referenceNumber = line.substring(0, endIndex);
        String updatedReferenceNumber = referenceNumber.replace(
                "[",
                lineSeparator() + lineSeparator() + "[" + prefix
        );

        document.setBodyText(document.getBodyText().replace(referenceNumber, updatedReferenceNumber));
        return line.replace(referenceNumber, updatedReferenceNumber);
    }
}
