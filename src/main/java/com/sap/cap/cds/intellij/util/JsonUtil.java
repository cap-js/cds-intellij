package com.sap.cap.cds.intellij.util;

import org.json.*;

public class JsonUtil {

    public static Object getPropertyAtPath(String json, String[] path) {
        JSONObject jsonObject = new JSONObject(json);
        for (int i = 0; i < path.length - 1; i++) {
            String key = path[i];
            jsonObject = jsonObject.getJSONObject(key);
        }
        return jsonObject.get(path[path.length - 1]);
    }
}
