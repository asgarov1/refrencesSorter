package com.asgarov.references_sorter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentWrapper {
    private String bodyText;
    private String references;

    public void replaceInAll(String toReplace, String replacement) {
        setBodyText(getBodyText().replace(toReplace, replacement));
        setReferences(getReferences().replace(toReplace, replacement));
    }
}
