package com.sap.cap.cds.intellij.usersettings;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.client.SettingsHelper;
import com.sap.cap.cds.intellij.settings.JsonSettingsManager;
import com.sap.cap.cds.intellij.settings.JsonSettingsService;
import com.sap.cap.cds.intellij.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    public void updateSettings(Map<String, Object> newUiState) {
        Map<String, Object> currentSettings = getSettings();
        currentSettings.clear();
        currentSettings.putAll(CdsUserSettings.getInstance(project).getDefaults());
        currentSettings.putAll(newUiState);

        Map<String, Object> nonDefaultSettings = new HashMap<>();
        Map<String, Object> defaults = CdsUserSettings.getInstance(project).getDefaults();
        for (Map.Entry<String, Object> entry : currentSettings.entrySet()) {
            if (!Objects.equals(entry.getValue(), defaults.get(entry.getKey()))) {
                nonDefaultSettings.put(entry.getKey(), entry.getValue());
            }
        }
        jsonManager.saveSettingsToFile(nonDefaultSettings);
    }

    @Override
    public Map<String, Object> getSettings() {
        return CdsUserSettings.getInstance(project).getSettings();
    }

    public Object getSettingsStructured() {
        String nestedJsonString = JsonUtil.nest(getSettings(), null).toString();
        return SettingsHelper.parseJson(nestedJsonString, false);
    }
}
