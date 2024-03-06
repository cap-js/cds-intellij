package com.sap.cap.cds.intellij;

public class Language extends com.intellij.lang.Language {

    public static final Language INSTANCE = new Language();

    public static final String LABEL = "cds";

    private Language() {
        super(LABEL);
    }
}