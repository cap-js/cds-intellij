package com.sap.cap.cds.intellij.usersettings;

import com.intellij.openapi.project.Project;
import com.sap.cap.cds.intellij.settings.JsonSettingsManager;
import com.sap.cap.cds.intellij.util.LoggerScope;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CdsUserSettingsJsonManager extends JsonSettingsManager<Map<String, Object>> {

    public CdsUserSettingsJsonManager(Project project) {
        super(project, ".cds-lsp/.settings.json", LoggerScope.USER_SETTINGS);
    }

    @Override
    public void loadSettingsFromFile(@NotNull Map<String, Object> settings) {
        logger.debug("Loading user settings from file");
        String json = readJson();
        if (!json.isEmpty()) {
            try {
                // TODO: Parse JSON and update settings map
                logger.debug("Loaded user settings from JSON");
            } catch (Exception e) {
                logger.error("Failed to parse user settings JSON '%s'".formatted(json), e);
            }
        }
    }

    @Override
    public void saveSettingsToFile(@NotNull Map<String, Object> settings) {
        if (settings.isEmpty()) {
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
}
