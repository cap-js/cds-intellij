package com.sap.cap.cds.intellij.util;

import org.json.JSONObject;

import java.util.stream.Stream;

public class JsonUtil {

    public static Object getPropertyAtPath(String json, String[] path) {
        JSONObject jsonObject = new JSONObject(json);
        for (int i = 0; i < path.length - 1; i++) {
            String key = path[i];
            jsonObject = jsonObject.getJSONObject(key);
        }
        return jsonObject.get(path[path.length - 1]);
    }

    // JSONObject.toString() does not guarantee the order of keys
    public static String toSortedString(JSONObject jsonObject) {
        String string = jsonObject.toString(StringUtil.JSON_INDENT);
        if (string.trim().matches("\\{\\s*[^,]*}")) {
            // 0 to 1 entries
            return string;
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

    public static boolean isJsonEqual(String json1, String json2) {
        if ("".equals(json1) ^ "".equals(json2)) {
            return false;
        }
        return new JSONObject(json1).similar(new JSONObject(json2));
    }

}
