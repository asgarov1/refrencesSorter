package com.asgarov.references_sorter.constants;

public class Constants {

    public static final String PROCESSING_PREFIX = "PROCESSING_";

    public static final String REFERENCE_WORD_REGEX = "\\[" + PROCESSING_PREFIX + "\\d+]";
    public static final String REFERENCE_REGEX = "\\[\\d+][^\\r\\n]*";

    public static final String SPACE_BETWEEN_REFERENCES_REGEX = "\\s+(?=\\[\\d+])";
    public static final String REFERENCES = "References";

    public static final String UPDATED_POSTFIX = "_updated";
}
