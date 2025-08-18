package com.sap.cap.cds.intellij.usersettings;

import com.google.gson.JsonElement;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.client.SettingsHelper;
import com.sap.cap.cds.intellij.settings.JsonSettingsManager;
import com.sap.cap.cds.intellij.settings.JsonSettingsService;
import org.json.JSONObject;

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

    public Object getSettingsStructured() {
        JSONObject nestedJson = toStructured(getSettings());
        String string = nestedJson.toString();
        JsonElement jsonElement = SettingsHelper.parseJson(string, false);
        return jsonElement;
    }

    private JSONObject toStructured(Map<String, Object> flatMap) {
        JSONObject root = new JSONObject();

        for (Map.Entry<String, Object> entry : flatMap.entrySet()) {
            setNestedValue(root, entry.getKey(), entry.getValue());
        }

        return root;
    }

    public void setNestedValue(JSONObject root, String key, Object value) {
        String[] parts = key.split("\\.");
        JSONObject current = root;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (!current.has(part)) {
                current.put(part, new JSONObject());
            }
            current = current.getJSONObject(part);
        }

        current.put(parts[parts.length - 1], value);
    }

    @Override
    public Map<String, Object> getSettings() {
        return CdsUserSettings.getInstance(project).getSettings();
    }
}
