package com.asgarov.references_sorter.sorter;

import com.asgarov.references_sorter.constants.Constants;
import com.asgarov.references_sorter.util.FileUtil;
import com.asgarov.references_sorter.util.ReferenceParagraphComparator;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.asgarov.references_sorter.constants.Constants.*;
import static com.asgarov.references_sorter.util.TextUtil.getReferences;
import static com.asgarov.references_sorter.util.XWPFUtil.addNewLine;
import static com.asgarov.references_sorter.util.XWPFUtil.addParagraph;
import static java.util.stream.Collectors.toList;

public class MicrosoftWordReferencesSorter {

    private static final Logger logger = LogManager.getLogger(MicrosoftWordReferencesSorter.class);

    /**
     * Main logic of the program:
     * - reads the file
     * - resorts references
     * - write to a new file in the same folder with original name postfixed with {@link Constants#UPDATED_POSTFIX}
     *
     * @param pathToFile - original file to read
     */
    @SneakyThrows
    public static void sortReferences(String pathToFile) {
        String outputFileName = FileUtil.updateFileName(pathToFile, UPDATED_POSTFIX);

        try (XWPFDocument xwpfDocument = new XWPFDocument(Files.newInputStream(Paths.get(pathToFile)));
             var document = new XWPFWordExtractor(xwpfDocument);
             var output = new FileOutputStream(outputFileName)) {

            String references = getReferences(document.getText());

            prefixReferenceNumbersWith(document, references, PROCESSING_PREFIX);
            replaceReferencesBackToNumbers(document);
            deleteUnusedReferences(document);

            // we have to add new references BEFORE deleting old ones due to the fact
            // that new references are created by copying data from old ones -> and the old ones throw exception after deletion
            int newReferencesStartIndex = addNewSortedReferences(document);

            int referencesIndex = getParagraphIndexThatContains(document, REFERENCES);
            deleteOldReferences(document, referencesIndex, newReferencesStartIndex - 1);

            //write to file
            xwpfDocument.write(output);
            logger.info("Saved file under " + outputFileName);
        }
    }

    private static void deleteOldReferences(XWPFWordExtractor document, int referencesIndex, int endOfOldParagraphs) {
        for (int i = endOfOldParagraphs; i > referencesIndex; i--) {
            document.getDocument().removeBodyElement(i);
        }
        logger.info("Deleted old references");
    }

    private static int addNewSortedReferences(XWPFWordExtractor document) {
        int newReferencesIndex = document.getDocument().getParagraphs().size();

        var paragraphsAdded = new AtomicInteger(0);
        getReferenceParagraphs(document)
                .stream()
                .sorted(ReferenceParagraphComparator::compare)
                .forEach(paragraph -> {
                    paragraphsAdded.getAndIncrement();
                    addParagraph(document, paragraph);
                    addNewLine(document);
                });

        logger.info("Added " + paragraphsAdded.get() + " sorted references");

        return newReferencesIndex;
    }

    private static void deleteUnusedReferences(XWPFWordExtractor document) {
        List<XWPFParagraph> containingParagraphs = document.getDocument().getParagraphs()
                .stream()
                .filter(p -> contains(p, PROCESSING_PREFIX))
                .collect(toList());

        logger.info("Deleting " + containingParagraphs.size() + " unused references");

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

    /**
     * By first changing reference numbers to something else and then enumerating back in turn
     * we naturally sort them as we enumarate them one after another
     * The references will be in correct order in the whole document and only references section still needs to be sorted
     *
     * @param document to update references in
     */
    private static void replaceReferencesBackToNumbers(XWPFWordExtractor document) {
        logger.info("Replacing references back to numbers");
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

    private static void prefixReferenceNumbersWith(XWPFWordExtractor document,
                                                   String references,
                                                   String prefix) {
        logger.info("Prefixing referenceNumbers with " + prefix);
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
        String updatedReferenceNumber = referenceNumber.replace("[", "[" + prefix);

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
                    break;
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
}
