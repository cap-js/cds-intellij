package com.sap.cap.cds.intellij.util;

public class StringUtil {

    public static final int JSON_INDENT = 2;

    // TODO: get rid of this, use Files.createTempDirectory() in the one usage
    public static String randomString(int len) {
        String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt((int) (Math.random() * CHARS.length())));
        }
        return sb.toString();
    }

}
