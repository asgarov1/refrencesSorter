package com.asgarov.references_sorter.util;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import static com.asgarov.references_sorter.constants.Constants.PROCESSING_PREFIX;

public class ReferenceParagraphComparator {
    public static int compare(XWPFParagraph paragraphA, XWPFParagraph paragraphB) {
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
}
