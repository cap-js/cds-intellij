package com.sap.cap.cds.intellij.lang;

public class CdsLanguage extends com.intellij.lang.Language {

    public static final CdsLanguage INSTANCE = new CdsLanguage();
    public static final String LABEL = "cds";

    private CdsLanguage() {
        super(LABEL);
    }
}