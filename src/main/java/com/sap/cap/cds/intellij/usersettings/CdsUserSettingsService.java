package com.sap.cap.cds.intellij.usersettings;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.sap.cap.cds.intellij.settings.JsonSettingsManager;
import com.sap.cap.cds.intellij.settings.JsonSettingsService;

import java.util.Map;

import static com.sap.cap.cds.intellij.usersettings.CdsUserSettings.USER_SETTINGS_JSON;
import static com.sap.cap.cds.intellij.util.LoggerScope.USER_SETTINGS;

@Service(Service.Level.PROJECT)
public final class CdsUserSettingsService extends JsonSettingsService<Map<String, Object>> {

    public static final String LABEL = "User Settings";
    public final JsonSettingsManager<Map<String, Object>> jsonManager;

    public CdsUserSettingsService(Project project) {
        super(project, USER_SETTINGS_JSON, USER_SETTINGS);
        this.jsonManager = createJsonManager(project);
    }

    @Override
    protected JsonSettingsManager<Map<String, Object>> createJsonManager(Project project) {
        return new CdsUserSettingsJsonManager(project);
    }

    @Override
    protected Map<String, Object> getSettings() {
        return CdsUserSettings.getInstance(project).getSettings();
    }
}
