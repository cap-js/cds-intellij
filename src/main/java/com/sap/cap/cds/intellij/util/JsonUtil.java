package com.sap.cap.cds.intellij.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class JsonUtil {

    private static final Gson GSON_FOR_SETTINGS = new Gson();

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

    public static String toSortedString(JSONObject jsonObject) {
        String string = jsonObject.toString(JsonUtil.JSON_INDENT);
        if (string.trim().matches("\\{\\s*[^,]*}")) {
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
        try {
            return new JSONObject(json1).similar(new JSONObject(json2));
        } catch (JSONException e) {
            return false;
        }
    }

    public static JsonObject nest(@NotNull Map<String, Object> flatMap, @Nullable String topLevelKey) {
        JsonObject root = new JsonObject();
        JsonObject target = (topLevelKey != null && !topLevelKey.isEmpty()) ? new JsonObject() : root;
        if (topLevelKey != null && !topLevelKey.isEmpty()) {
            root.add(topLevelKey, target);
        }

        for (Map.Entry<String, Object> entry : flatMap.entrySet()) {
            String[] parts = entry.getKey().split("\\.");
            JsonObject current = target;
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                if (!current.has(part) || !current.get(part).isJsonObject()) {
                    current.add(part, new JsonObject());
                }
                current = current.getAsJsonObject(part);
            }
            current.add(parts[parts.length - 1], GSON_FOR_SETTINGS.toJsonTree(entry.getValue()));
        }
        return root;
    }

    public static @NotNull Map<String, Object> flatten(@NotNull JsonObject jsonObject) {
        Map<String, Object> flatMap = new HashMap<>();
        flattenRecursively(jsonObject, "", flatMap);
        return flatMap;
    }

    private static void flattenRecursively(@NotNull JsonObject json, @NotNull String prefix, @NotNull Map<String, Object> result) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String newKey = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            JsonElement value = entry.getValue();

            if (value.isJsonObject()) {
                flattenRecursively(value.getAsJsonObject(), newKey, result);
            } else if (value.isJsonPrimitive()) {
                JsonPrimitive primitive = value.getAsJsonPrimitive();
                if (primitive.isBoolean()) {
                    result.put(newKey, primitive.getAsBoolean());
                } else if (primitive.isString()) {
                    result.put(newKey, primitive.getAsString());
                } else if (primitive.isNumber()) {
                    String numStr = primitive.getAsString();
                    if (numStr.contains(".")) {
                        result.put(newKey, primitive.getAsDouble());
                    } else {
                        result.put(newKey, primitive.getAsLong());
                    }
                }
            } else if (value.isJsonArray()) {
                result.put(newKey, value.toString());
            }
        }
    }

    public static final int JSON_INDENT = 2;
}
