package com.asgarov.references_sorter.sorter;

import com.asgarov.references_sorter.util.FileUtil;
import com.asgarov.references_sorter.util.TextUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.asgarov.references_sorter.constants.Constants.REFERENCE_WORD_REGEX;
import static java.util.stream.Collectors.toList;

public class MicrosoftWordSorter {

    public static final String PROCESSING_PREFIX = "PROCESSING_";
    public static final String SPACE_BETWEEN_REFERENCES_REGEX = "\\s+(?=\\[\\d+])";
    public static final String REFERENCES = "References";

    public static void sort(String pathToFile) {
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(pathToFile)));
             var document = new XWPFWordExtractor(doc);
             var output = new FileOutputStream(FileUtil.updateFileName(pathToFile, "_updated"))) {

            String references = TextUtil.getReferences(document.getText());
            prefixReferencesNumbersWith(document, references, PROCESSING_PREFIX);
            replaceReferencesBackToNumbers(document);
            deleteUnusedReferences(document);

            List<XWPFParagraph> referenceParagraphs = getReferenceParagraphs(document).stream().distinct().collect(toList());
            List<Integer> referenceParagraphsPositions = referenceParagraphs.stream().map(document.getDocument()::getPosOfParagraph).collect(toList());

            List<XWPFParagraph> sortedReferenceParagraphs = referenceParagraphs.stream().sorted(MicrosoftWordSorter::sort).collect(toList());



            System.out.println(document.getDocument().getParagraphs().size());

            for (int i = 0; i < sortedReferenceParagraphs.size(); i++) {
                XWPFParagraph paragraph = sortedReferenceParagraphs.get(i);
                Integer indexToReplace = referenceParagraphsPositions.get(i);
                document.getDocument().setParagraph(paragraph, indexToReplace);
            }

            System.out.println(document.getDocument().getParagraphs().size());

            doc.write(output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int sort(XWPFParagraph paragraphA, XWPFParagraph paragraphB) {
        if (!containsValidReference(paragraphA)) {
            return -1;
        }
        if (!containsValidReference(paragraphB)) {
            return 1;
        }

        return getReferenceNumber(paragraphA) - getReferenceNumber(paragraphB);
    }

    private static boolean containsValidReference(XWPFParagraph paragraph) {
        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.getText(0);
            if (runText != null && runText.contains("[") && !runText.contains(PROCESSING_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    private static int getReferenceNumber(XWPFParagraph paragraph) {
        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.getText(0);
            if (runText != null && runText.contains("[") && !runText.contains(PROCESSING_PREFIX)) {
                int startIndex = runText.indexOf("[") + 1;
                int endIndex = runText.indexOf("]");
                return Integer.parseInt(runText.substring(startIndex, endIndex));
            }
        }
        throw new IllegalStateException();
    }

    private static void deleteUnusedReferences(XWPFWordExtractor document) {
        List<XWPFParagraph> containingParagraphs = document.getDocument().getParagraphs()
                .stream()
                .filter(p -> contains(p, PROCESSING_PREFIX))
                .collect(toList());

        containingParagraphs.stream()
                .map(document.getDocument()::getPosOfParagraph)
                .forEach(document.getDocument()::removeBodyElement);
    }

    private static boolean contains(XWPFParagraph paragraph, String prefix) {
        return paragraph.getRuns().stream()
                .map(run -> run.getText(0))
                .filter(Objects::nonNull)
                .anyMatch(text -> text.contains(prefix));
    }

    private static void replaceReferencesBackToNumbers(XWPFWordExtractor document) {
        final Pattern pattern = Pattern.compile(REFERENCE_WORD_REGEX, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(document.getText());

        int refCount = 1;
        while (matcher.find()) {
            String matchedReference = matcher.group(0);

            if (StringUtils.countMatches(document.getText(), matchedReference) > 1) {
                replaceInDocument(document, matchedReference, "[" + refCount++ + "]");
                matcher = pattern.matcher(document.getText());
            }
        }
    }

    private static void prefixReferencesNumbersWith(XWPFWordExtractor document, String references, String prefix) {
        Arrays.stream(references.split(SPACE_BETWEEN_REFERENCES_REGEX))
                .filter(Predicate.not(String::isEmpty))
                .forEach(line -> updateNumber(line, document, prefix));
    }

    private static void updateNumber(String line, XWPFWordExtractor document, String prefix) {
        if (!line.trim().startsWith("[")) {
            return;
        }

        int endIndex = line.indexOf("]");
        String referenceNumber = line.substring(0, endIndex + 1);
        String updatedReferenceNumber = referenceNumber.replace(
                "[",
                "[" + prefix
        );

        replaceInDocument(document, referenceNumber, updatedReferenceNumber);
    }

    private static List<XWPFParagraph> getReferenceParagraphs(XWPFWordExtractor document) {
        List<XWPFParagraph> xwpfParagraphs = new ArrayList<>();

        int referencesParagraphIndex = getParagraphIndexThatContains(document, REFERENCES);
        List<XWPFParagraph> paragraphs = document.getDocument().getParagraphs();
        for (int i = referencesParagraphIndex; i < paragraphs.size(); i++) {
            for (XWPFRun run : paragraphs.get(i).getRuns()) {
                String runText = run.getText(0);
                if (runText != null && runText.contains("[") && !runText.contains(PROCESSING_PREFIX)) {
                    xwpfParagraphs.add(paragraphs.get(i));
                }
            }
        }
        return xwpfParagraphs;
    }

    private static int getParagraphIndexThatContains(XWPFWordExtractor document, String word) {
        for (XWPFParagraph paragraph : document.getDocument().getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                String runText = run.getText(0);
                if (runText == null) {
                    continue;
                }
                if (runText.contains(word)) {
                    return document.getDocument().getParagraphs().indexOf(paragraph);
                }
            }
        }
        return -1;
    }

    private static void replaceInDocument(XWPFWordExtractor document, String toReplace, String replacement) {
        for (XWPFParagraph xwpfParagraph : document.getDocument().getParagraphs()) {
            for (XWPFRun xwpfRun : xwpfParagraph.getRuns()) {
                String docText = xwpfRun.getText(0);
                if (docText != null) {
                    xwpfRun.setText(docText.replace(toReplace, replacement), 0);
                }
            }
        }
    }

    private static Optional<XWPFParagraph> getContainingParagraph(XWPFWordExtractor document, String matchedReference) {
        for (XWPFParagraph xwpfParagraph : document.getDocument().getParagraphs()) {
            for (XWPFRun xwpfRun : xwpfParagraph.getRuns()) {
                String docText = xwpfRun.getText(0);
                if (docText != null && docText.contains(matchedReference)) {
                    return Optional.of(xwpfParagraph);
                }
            }
        }
        return Optional.empty();
    }
}
