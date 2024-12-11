package com.sap.cap.cds.intellij.lang;

public class CdsLanguage extends com.intellij.lang.Language {

    public static final CdsLanguage INSTANCE = new CdsLanguage();
    public static final String LABEL = "cds";
    public static final String SAMPLE_SRC = """
            entity MySampleEntity {
            key k : Integer;
            el : String;
            }
            """;

    private CdsLanguage() {
        super(LABEL);
    }
}