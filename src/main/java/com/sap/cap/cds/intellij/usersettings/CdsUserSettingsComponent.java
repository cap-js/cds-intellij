package com.sap.cap.cds.intellij.usersettings;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.sap.cap.cds.intellij.lsp4ij.CdsLanguageServer;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class CdsUserSettingsComponent {

    private static final String TOOLTIP_HTML_END = "</body></html>";
    private static final String TOOLTIP_HTML_START = "<html><body style='width: 350px'>";
    private static final String TOOLTIP_KEY_END = "</i></font>";
    private static final String TOOLTIP_KEY_START = "<br><br><font color='gray'><i>";

    private final Project project;
    private final JPanel mainPanel;
    private final Map<String, JComponent> controls = new HashMap<>();
    private final CdsUserSettingsService service;

    public CdsUserSettingsComponent(Project project) {
        this.project = project;
        this.service = project.getService(CdsUserSettingsService.class);
        this.mainPanel = createPanel();
        if (isValidProject()) {
            loadCurrentSettings();
        }
    }

    private JPanel createPanel() {
        if (!isValidProject()) {
            FormBuilder builder = FormBuilder.createFormBuilder();
            JLabel noProjectLabel = new JLabel("No project open");
            noProjectLabel.setHorizontalAlignment(SwingConstants.CENTER);
            builder.addComponent(noProjectLabel);
            return builder.getPanel();
        }

        FormBuilder builder = FormBuilder.createFormBuilder();
        Map<String, Object> allSettings = CdsUserSettings.getDefaults();
        Map<String, List<String>> categoryGroups = groupSettingsByCategory(allSettings);

        for (Map.Entry<String, List<String>> categoryEntry : categoryGroups.entrySet()) {
            String category = categoryEntry.getKey();
            List<String> settingKeys = categoryEntry.getValue();

            builder.addSeparator(5);
            JLabel categoryLabel = new JLabel("<html><b>" + category + "</b></html>");
            builder.addComponent(categoryLabel);

            Map<String, List<String>> groupedSettings = groupSettingsByGroup(settingKeys);

            for (Map.Entry<String, List<String>> groupEntry : groupedSettings.entrySet()) {
                String group = groupEntry.getKey();
                List<String> groupSettingKeys = groupEntry.getValue();

                if (!group.isEmpty()) {
                    builder.addSeparator(3);
                    JLabel groupLabel = new JLabel("<html><i>" + group + "</i></html>");
                    builder.addComponent(groupLabel);
                }

                for (String settingKey : groupSettingKeys) {
                    Object defaultValue = allSettings.get(settingKey);
                    JComponent control = createControlForSetting(settingKey, defaultValue);
                    controls.put(settingKey, control);

                    String label = formatLabel(settingKey);
                    String description = getDescription(settingKey);

                    if (control instanceof JBCheckBox) {
                        ((JBCheckBox) control).setText(label);
                        control.setToolTipText(description);
                        builder.addComponent(control);
                    } else {
                        JLabel labelComponent = new JLabel(label + ":");
                        labelComponent.setToolTipText(description);
                        control.setToolTipText(description);
                        builder.addLabeledComponent(labelComponent, control);
                    }
                }
            }
        }

        builder.addComponentFillVertically(new JPanel(), 0);
        return builder.getPanel();
    }

    private boolean isValidProject() {
        return project != null && !project.isDisposed() && project.getBasePath() != null;
    }

    private Map<String, List<String>> groupSettingsByCategory(Map<String, Object> allSettings) {
        Map<String, List<String>> groups = new LinkedHashMap<>();

        for (String settingKey : allSettings.keySet()) {
            String category = CdsUserSettings.getCategory(settingKey);
            if (category == null) {
                category = extractCategory(settingKey);
            }
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

    private Map<String, List<String>> groupSettingsByGroup(List<String> settingKeys) {
        Map<String, List<String>> groups = new LinkedHashMap<>();

        for (String settingKey : settingKeys) {
            String group = CdsUserSettings.getGroup(settingKey);
            String groupKey = group != null ? group : "";
            groups.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(settingKey);
        }

        return groups;
    }

    private String formatLabel(String settingKey) {
        String schemaLabel = CdsUserSettings.getLabel(settingKey);
        if (schemaLabel != null) {
            return schemaLabel;
        }

        // Fallback to generated label if not found in schema
        String[] parts = settingKey.split("\\.");
        return capitalizeWords(parts[parts.length - 1]);
    }

    private String getDescription(String settingKey) {
        String description = CdsUserSettings.getDescription(settingKey);
        StringBuilder tooltip = new StringBuilder(TOOLTIP_HTML_START);

        if (description != null && !description.isEmpty()) {
            tooltip.append(description);
        }

        tooltip.append(TOOLTIP_KEY_START)
               .append(settingKey)
               .append(TOOLTIP_KEY_END)
               .append(TOOLTIP_HTML_END);

        return tooltip.toString();
    }

    private JComponent createControlForSetting(String settingKey, Object defaultValue) {
        if (defaultValue instanceof Boolean) {
            return new JBCheckBox();
        }

        // Check if this setting has enum values
        if (CdsUserSettings.hasEnumValues(settingKey)) {
            String[] enumValues = CdsUserSettings.getEnumValues(settingKey);
            return new JComboBox<String>(enumValues);
        }

        return new JBTextField();
    }

    private void loadCurrentSettings() {
        Map<String, Object> currentSettings = service.getSettings();
        Map<String, Object> defaults = CdsUserSettings.getDefaults();

        for (Map.Entry<String, JComponent> entry : controls.entrySet()) {
            String key = entry.getKey();
            Object value = currentSettings.getOrDefault(key, defaults.get(key));
            setControlValue(entry.getValue(), value);
        }
    }

    private void setControlValue(JComponent control, Object value) {
        if (control instanceof JBCheckBox && value instanceof Boolean) {
            ((JBCheckBox) control).setSelected((Boolean) value);
        } else if (control instanceof JBTextField) {
            ((JBTextField) control).setText(String.valueOf(value));
        } else if (control instanceof JComboBox && value != null) {
            @SuppressWarnings("unchecked")
            JComboBox<String> comboBox = (JComboBox<String>) control;
            comboBox.setSelectedItem(value);
        }
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public boolean isModified() {
        if (!isValidProject()) return false;

        Map<String, Object> currentSettings = service.getSettings();
        Map<String, Object> defaults = CdsUserSettings.getDefaults();

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

    public void apply() {
        if (!isValidProject()) return;

        Map<String, Object> newUiState = new HashMap<>();
        for (Map.Entry<String, JComponent> entry : controls.entrySet()) {
            String key = entry.getKey();
            Object controlValue = getControlValue(entry.getValue());
            newUiState.put(key, controlValue);
        }

        service.updateSettings(newUiState);
        CdsLanguageServer.restart(project);
    }

    public void reset() {
        if (!isValidProject()) return;

        loadCurrentSettings();
    }
}
