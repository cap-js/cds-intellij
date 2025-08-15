package com.sap.cap.cds.intellij.usersettings;

import com.intellij.openapi.project.Project;
import com.sap.cap.cds.intellij.settings.JsonSettingsManager;
import com.sap.cap.cds.intellij.util.LoggerScope;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.sap.cap.cds.intellij.usersettings.CdsUserSettings.USER_SETTINGS_JSON;

public class CdsUserSettingsJsonManager extends JsonSettingsManager<Map<String, Object>> {

    public CdsUserSettingsJsonManager(Project project) {
        super(project, USER_SETTINGS_JSON, LoggerScope.USER_SETTINGS);
    }

    @Override
    public void loadSettingsFromFile(@NotNull Map<String, Object> settings) {
        logger.debug("Loading user settings from file");
        String json = readJson();
        if (!json.isEmpty()) {
            try {
                parseJsonIntoSettings(json, settings);
                logger.debug("Loaded user settings from JSON");
            } catch (Exception e) {
                logger.error("Failed to parse user settings JSON '%s'".formatted(json), e);
            }
        }
    }

    @Override
    public void saveSettingsToFile(@NotNull Map<String, Object> settings) {
        if (settings.isEmpty()) {
            // TODO write empty
            logger.debug("Settings are empty, skipping save");
            return;
        }
        writeJson(toJson(settings));
    }

    private String toJson(Map<String, Object> settings) {
        StringBuilder json = new StringBuilder("{\n");
        settings.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    json.append("  \"").append(entry.getKey()).append("\": ");
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        json.append("\"").append(value).append("\"");
                    } else {
                        json.append(value);
                    }
                    json.append(",\n");
                });
        if (json.length() > 2) {
            json.setLength(json.length() - 2);
        }
        json.append("\n}");
        return json.toString();
    }

    private void parseJsonIntoSettings(String json, Map<String, Object> settings) {
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        String content = json.substring(1, json.length() - 1).trim();
        if (content.isEmpty()) {
            return;
        }

        String[] pairs = content.split(",(?=\\s*\")");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length != 2) continue;

            String key = keyValue[0].trim().replaceAll("^\"|\"$", "");
            String valueStr = keyValue[1].trim();

            Object value;
            if (valueStr.equals("true") || valueStr.equals("false")) {
                value = Boolean.parseBoolean(valueStr);
            } else if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
                value = valueStr.substring(1, valueStr.length() - 1);
            } else {
                try {
                    value = Integer.parseInt(valueStr);
                } catch (NumberFormatException e) {
                    try {
                        value = Double.parseDouble(valueStr);
                    } catch (NumberFormatException ex) {
                        value = valueStr;
                    }
                }
            }
            settings.put(key, value);
        }
    }
}
