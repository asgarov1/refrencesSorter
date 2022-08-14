package com.asgarov.references_sorter.constants;

public class Constants {

    public static final String PROCESSING_PREFIX = "PROCESSING_";

    public static final String REFERENCE_WORD_REGEX = "\\[" + PROCESSING_PREFIX + "\\d+]";
    public static final String REFERENCE_INCLUDING_PROCESSING_PREFIX_REGEX = "\\[" + PROCESSING_PREFIX + "\\d+].*";
    public static final String REFERENCE_REGEX = "\\[\\d+][^\\r\\n]*";
}
