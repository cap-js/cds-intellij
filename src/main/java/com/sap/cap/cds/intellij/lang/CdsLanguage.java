package com.sap.cap.cds.intellij.lang;

public class CdsLanguage extends com.intellij.lang.Language {

    public static final CdsLanguage INSTANCE = new CdsLanguage();
    public static final String LABEL = "cds";
    public static final String SAMPLE_SRC = """
            /**
            * | k | el |
            * |--|--|
            * |1 | 'a'|
            * | 2| 'b'|
            *
            *    1. comment 1
            * 2. comment 2
            */
            
            
            entity MySampleEntity {
            @pre key k : Integer;
            el : String @post;
            }
            
            entity F {}
            
            entity G {}
            """;

    private CdsLanguage() {
        super(LABEL);
    }
}