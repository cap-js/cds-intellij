package com.sap.cap.cds.intellij.usersettings;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class CdsUserSettingsComponent {

    private final Project project;
    private final JPanel mainPanel;
    private final Map<String, JComponent> controls = new HashMap<>();
    private final CdsUserSettingsService service;

    public CdsUserSettingsComponent(Project project) {
        this.project = project;
        this.service = project.getService(CdsUserSettingsService.class);
        this.mainPanel = createPanel();
        loadCurrentSettings();
    }

    private JPanel createPanel() {
        FormBuilder builder = FormBuilder.createFormBuilder();
        Map<String, Object> allSettings = CdsUserSettings.getInstance(project).getAllSettings();
        Map<String, List<String>> categoryGroups = groupSettingsByCategory(allSettings);

        for (Map.Entry<String, List<String>> categoryEntry : categoryGroups.entrySet()) {
            String category = categoryEntry.getKey();
            List<String> settingKeys = categoryEntry.getValue();

            builder.addSeparator(5);
            builder.addLabeledComponent(new JLabel(category), new JPanel());

            for (String settingKey : settingKeys) {
                Object defaultValue = allSettings.get(settingKey);
                JComponent control = createControlForSetting(settingKey, defaultValue);
                controls.put(settingKey, control);

                String label = formatLabel(settingKey);
                if (control instanceof JBCheckBox) {
                    ((JBCheckBox) control).setText(label);
                    builder.addComponent(control);
                } else {
                    builder.addLabeledComponent(label + ":", control);
                }
            }
        }

        builder.addComponentFillVertically(new JPanel(), 0);
        return builder.getPanel();
    }

    private Map<String, List<String>> groupSettingsByCategory(Map<String, Object> allSettings) {
        Map<String, List<String>> groups = new LinkedHashMap<>();

        for (String settingKey : allSettings.keySet()) {
            String category = extractCategory(settingKey);
            groups.computeIfAbsent(category, k -> new ArrayList<>()).add(settingKey);
        }

        return groups;
    }

    private String extractCategory(String settingKey) {
        String[] parts = settingKey.split("\\.");
        if (parts.length >= 2) {
            return capitalizeWords(parts[1]);
        }
        return "General";
    }

    private String capitalizeWords(String text) {
        return java.util.Arrays.stream(text.split("(?=[A-Z])"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    private String formatLabel(String settingKey) {
        String[] parts = settingKey.split("\\.");
        String lastPart = parts[parts.length - 1];
        return capitalizeWords(lastPart);
    }

    private JComponent createControlForSetting(String settingKey, Object defaultValue) {
        if (defaultValue instanceof Boolean) {
            return new JBCheckBox();
        }

        // Check if this setting has enum values in the schema
        String[] enumValues = getEnumValuesFromSchema(settingKey);
        if (enumValues != null) {
            JComboBox<String> combo = new JComboBox<>(enumValues);
            return combo;
        }

        return new JBTextField();
    }

    private String[] getEnumValuesFromSchema(String settingKey) {
        try {
            File schemaFile = new File(project.getBasePath(), "lsp/schemas/user-settings.json");
            if (schemaFile.exists()) {
                String content = Files.readString(schemaFile.toPath());
                JSONObject schema = new JSONObject(content);
                JSONObject properties = schema.getJSONObject("properties");

                if (properties.has(settingKey)) {
                    JSONObject setting = properties.getJSONObject(settingKey);
                    if (setting.has("enum")) {
                        return setting.getJSONArray("enum").toList()
                                .stream()
                                .map(String::valueOf)
                                .toArray(String[]::new);
                    }
                }
            }
        } catch (Exception e) {
            // Fall back to null if schema reading fails
        }
        return null;
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public boolean isModified() {
        Map<String, Object> currentSettings = loadSettingsFromFile();
        Map<String, Object> defaults = CdsUserSettings.getInstance(project).getAllSettings();

        for (Map.Entry<String, JComponent> entry : controls.entrySet()) {
            String key = entry.getKey();
            Object currentValue = currentSettings.getOrDefault(key, defaults.get(key));
            Object controlValue = getControlValue(entry.getValue());

            if (!java.util.Objects.equals(currentValue, controlValue)) {
                return true;
            }
        }
        return false;
    }

    public void apply() {
        Map<String, Object> settings = new HashMap<>();

        for (Map.Entry<String, JComponent> entry : controls.entrySet()) {
            settings.put(entry.getKey(), getControlValue(entry.getValue()));
        }

        service.jsonManager.saveSettingsToFile(settings);
    }

    public void reset() {
        loadCurrentSettings();
    }

    private void loadCurrentSettings() {
        Map<String, Object> currentSettings = loadSettingsFromFile();
        Map<String, Object> defaults = CdsUserSettings.getInstance(project).getAllSettings();

        for (Map.Entry<String, JComponent> entry : controls.entrySet()) {
            String key = entry.getKey();
            Object value = currentSettings.getOrDefault(key, defaults.get(key));
            setControlValue(entry.getValue(), value);
        }
    }

    private Map<String, Object> loadSettingsFromFile() {
        try {
            File settingsFile = new File(project.getBasePath(), ".cds-lsp/.settings.json");
            if (settingsFile.exists()) {
                String content = Files.readString(settingsFile.toPath());
                JSONObject json = new JSONObject(content);
                Map<String, Object> settings = new HashMap<>();
                for (String key : json.keySet()) {
                    settings.put(key, json.get(key));
                }
                return settings;
            }
        } catch (Exception e) {
            // Fall back to defaults
        }
        return new HashMap<>();
    }

    private Object getControlValue(JComponent control) {
        if (control instanceof JBCheckBox) {
            return ((JBCheckBox) control).isSelected();
        } else if (control instanceof JBTextField) {
            String text = ((JBTextField) control).getText();
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                return text;
            }
        } else if (control instanceof JComboBox) {
            return ((JComboBox<?>) control).getSelectedItem();
        }
        return null;
    }

    private void setControlValue(JComponent control, Object value) {
        if (control instanceof JBCheckBox && value instanceof Boolean) {
            ((JBCheckBox) control).setSelected((Boolean) value);
        } else if (control instanceof JBTextField) {
            ((JBTextField) control).setText(String.valueOf(value));
        } else if (control instanceof JComboBox && value != null) {
            ((JComboBox<Object>) control).setSelectedItem(value);
        }
    }
}
