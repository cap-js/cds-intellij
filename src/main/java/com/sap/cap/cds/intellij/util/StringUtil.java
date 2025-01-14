package com.sap.cap.cds.intellij.util;

import org.json.JSONObject;

import java.util.stream.Stream;

public class StringUtil {

    public static final int JSON_INDENT = 2;

    public static String randomString(int len) {
        String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt((int) (Math.random() * CHARS.length())));
        }
        return sb.toString();
    }

    // JSONObject.toString() does not guarantee the order of keys
    public static String toSortedString(JSONObject jsonObject) {
        String string = jsonObject.toString(JSON_INDENT);
        if (string.trim().matches("\\{\\s*}")) {
            return "{}"; // avoid unnecessary blank lines between braces
        }
        return "{\n" +
                Stream.of(string.split(",?\n"))
                        .filter(line -> !line.trim().matches("[{}]+"))
                        .sorted()
                        .reduce((a, b) -> a + ",\n" + b)
                        .orElse("")
                        .replaceFirst("\\s+$", "")
                + "\n}";

    }

}
