package com.asgarov.references_sorter;

import com.asgarov.references_sorter.sorter.MicrosoftWordReferencesSorter;

public class Runner {
    public static void main(String[] args) {
        MicrosoftWordReferencesSorter.sortReferences("src/main/resources/assignment.docx");
    }
}
