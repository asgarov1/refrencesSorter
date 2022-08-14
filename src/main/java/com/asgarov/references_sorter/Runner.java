package com.asgarov.references_sorter;

import com.asgarov.references_sorter.sorter.MicrosoftWordSorter;

public class Runner {
    public static void main(String[] args) {
//        Sorter.sort("src/main/resources/assignment.txt");
        MicrosoftWordSorter.sort("src/main/resources/assignment.docx");
    }
}
