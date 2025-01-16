package com.sap.cap.cds.intellij.util;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.stream.Stream;

public class JsonUtil {

    public static Object getPropertyAtPath(String json, String[] path) {
        JSONObject jsonObject = toJSONObject(json);
        for (int i = 0; i < path.length - 1; i++) {
            String key = path[i];
            jsonObject = jsonObject.getJSONObject(key);
        }
        return jsonObject.get(path[path.length - 1]);
    }

    public static JSONObject toJSONObject(@NotNull String json) {
        return new JSONObject(json.isEmpty() ? "{}" : json);
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

    public static boolean isJsonEqual(@NotNull String json1, @NotNull String json2) {
        if (json1.isEmpty() && json2.isEmpty()) {
            return true;
        }
        if (json1.isEmpty() ^ json2.isEmpty()) {
            return false;
        }
        return new JSONObject(json1).similar(new JSONObject(json2));
    }

}
