package com.sap.cap.cds.intellij.usersettings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.project.Project;
import com.sap.cap.cds.intellij.settings.JsonSettingsManager;
import com.sap.cap.cds.intellij.util.JsonUtil;
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
        if (json.isEmpty()) {
            return;
        }

        try {
            JsonElement root = JsonParser.parseString(json);
            if (!root.isJsonObject()) {
                logger.warn("Invalid JSON structure for user settings: Root is not an object.");
                return;
            }
            JsonObject settingsRoot = root.getAsJsonObject();
            if (settingsRoot.has("settings") && settingsRoot.get("settings").isJsonObject()) {
                settingsRoot = settingsRoot.getAsJsonObject("settings");
            }

            Map<String, Object> fileSettings = JsonUtil.flatten(settingsRoot);
            settings.putAll(fileSettings);
            logger.debug("Loaded and merged user settings from JSON");
        } catch (JsonSyntaxException e) {
            logger.error("Failed to parse user settings JSON '%s'".formatted(json), e);
        }
    }

    @Override
    public void saveSettingsToFile(@NotNull Map<String, Object> settings) {
        if (settings.isEmpty()) {
            writeJson("{}");
            return;
        }
        writeJson(JsonUtil.nest(settings, "settings").toString());
    }
}
