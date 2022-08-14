package com.asgarov.references_sorter.util;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.Objects;

public class XWPFUtil {
    public static void duplicateInto(XWPFRun oldRun, XWPFParagraph newParagraph) {
        XWPFRun newRun = newParagraph.createRun();
        newRun.setBold(oldRun.isBold());
        newRun.setCapitalized(oldRun.isCapitalized());
        newRun.setText(oldRun.getText(0));
        newRun.setColor(oldRun.getColor());
        newRun.setCharacterSpacing(oldRun.getCharacterSpacing());
        newRun.setDoubleStrikethrough(oldRun.isDoubleStrikeThrough());
        newRun.setEmbossed(oldRun.isEmbossed());
        newRun.setFontFamily(oldRun.getFontFamily());
        newRun.setImprinted(oldRun.isImprinted());
        newRun.setItalic(oldRun.isItalic());
        newRun.setFontSize(Objects.requireNonNullElse(oldRun.getFontSizeAsDouble(), 0d));
        newRun.setKerning(oldRun.getKerning());
        newRun.setLang(oldRun.getLang());
        newRun.setShadow(oldRun.isShadowed());
        newRun.setSmallCaps(oldRun.isSmallCaps());
        newRun.setStyle(oldRun.getStyle());
        newRun.setTextHighlightColor(oldRun.getTextHightlightColor().toString());
        newRun.setTextScale(oldRun.getTextScale());
        newRun.setTextPosition(oldRun.getTextPosition());
        newRun.setVanish(oldRun.isVanish());
        newRun.setVerticalAlignment(oldRun.getVerticalAlignment().toString());
    }

    public static void addNewLine(XWPFWordExtractor document) {
        XWPFParagraph newParagraph = document.getDocument().createParagraph();
        XWPFRun newRun = newParagraph.createRun();
        newRun.addBreak();
    }

    public static void addParagraph(XWPFWordExtractor document, XWPFParagraph oldParagraph) {
        XWPFParagraph newParagraph = document.getDocument().createParagraph();
        oldParagraph.getRuns().forEach(run -> duplicateInto(run, newParagraph));
    }
}
