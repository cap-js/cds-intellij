package com.sap.cap.cds.intellij.codestyle;

import com.intellij.openapi.project.Project;
import com.sap.cap.cds.intellij.settings.JsonSettingsManager;
import com.sap.cap.cds.intellij.util.LoggerScope;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

public class CdsPrettierJsonManager extends JsonSettingsManager<CdsCodeStyleSettings> {

    public CdsPrettierJsonManager(Project project) {
        super(project, ".cdsprettier.json", LoggerScope.CODE_STYLE);
    }

    @Override
    public void loadSettingsFromFile(@NotNull CdsCodeStyleSettings settings) {
        logger.debug("Loading settings from file");
        String json = readJson();
        if (!json.isEmpty()) {
            try {
                settings.loadFrom(json);
            } catch (JSONException e) {
                logger.error("Failed to parse JSON '%s'".formatted(json), e);
            }
        }
    }

    @Override
    public void saveSettingsToFile(@NotNull CdsCodeStyleSettings settings) {
        if (!isJsonFilePresent() && settings.isDefault()) {
            logger.debug("Settings are default, skipping save");
            return;
        }
        if (!jsonCached.isEmpty() && settings.equals(jsonCached)) {
            logger.debug("Settings are equal, skipping save");
            return;
        }
        String json = settings.getLoadedOrNonDefaultSettings();
        writeJson(json);
    }
}
