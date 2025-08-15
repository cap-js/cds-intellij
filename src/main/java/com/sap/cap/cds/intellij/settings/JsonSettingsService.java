package com.sap.cap.cds.intellij.settings;

import com.intellij.openapi.project.Project;
import com.sap.cap.cds.intellij.util.Logger;
import com.sap.cap.cds.intellij.util.LoggerScope;

import static com.sap.cap.cds.intellij.util.Logger.logger;

public abstract class JsonSettingsService<T> {

    protected final Project project;
    protected final Logger logger;
    protected final JsonSettingsManager<T> jsonManager;

    protected JsonSettingsService(Project project, String fileName, LoggerScope loggerScope) {
        this.project = project;
        this.logger = logger(project, loggerScope);
        this.jsonManager = createJsonManager(project);
    }

    protected abstract JsonSettingsManager<T> createJsonManager(Project project);
    protected abstract T getSettings();

    public boolean isSettingsFilePresent() {
        return jsonManager.isJsonFilePresent();
    }

    public boolean isSettingsFileChanged() {
        return jsonManager.isSettingsFileChanged();
    }

    public void updateSettingsFile() {
        jsonManager.saveSettingsToFile(getSettings());
    }

    public void updateProjectSettingsFromFile() {
        if (isSettingsFilePresent()) {
            logger.debug("Loading settings from file");
            jsonManager.loadSettingsFromFile(getSettings());
        } else {
            logger.debug("No settings file found");
        }
    }
}
