package com.asgarov.references_sorter.domain;

public class DocumentWrapper {
    private String bodyText;
    private String references;

    public DocumentWrapper(String bodyText, String references) {
        this.bodyText = bodyText;
        this.references = references;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public void replaceInAll(String toReplace, String replacement) {
        setBodyText(getBodyText().replace(toReplace, replacement));
        setReferences(getReferences().replace(toReplace, replacement));
    }
}
