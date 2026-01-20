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

        boolean firstCategory = true;
        for (Map.Entry<String, List<String>> categoryEntry : categoryGroups.entrySet()) {
            String category = categoryEntry.getKey();
            List<String> settingKeys = categoryEntry.getValue();

            if (!firstCategory) {
                builder.addSeparator(15);
            }
            firstCategory = false;
            
            JLabel categoryLabel = new JLabel("<html><span style='font-size:110%'><b>" + category + "</b></span></html>");
            builder.addComponent(categoryLabel);
            builder.addVerticalGap(8);

            for (String settingKey : settingKeys) {
                Object defaultValue = allSettings.get(settingKey);
                JComponent control = createControlForSetting(settingKey, defaultValue);
                controls.put(settingKey, control);

                String label = formatLabel(settingKey);
                String description = getDescription(settingKey);

                // Add label on top
                JLabel labelComponent = new JLabel(label);
                builder.addComponent(labelComponent);
                
                if (control instanceof JBCheckBox) {
                    // Checkbox next to description (checkbox has no text)
                    if (description != null && !description.isEmpty()) {
                        JLabel descLabel = new JLabel("<html><font color='gray' style='font-size:90%'>" + description + "</font></html>");
                        builder.addLabeledComponent(control, descLabel);
                    } else {
                        builder.addComponent(control);
                    }
                } else {
                    // Add description below label if available
                    if (description != null && !description.isEmpty()) {
                        JLabel descLabel = new JLabel("<html><font color='gray' style='font-size:90%'>" + description + "</font></html>");
                        builder.addComponent(descLabel);
                    }
                    // Add control below description
                    builder.addComponent(control);
                }

                builder.addVerticalGap(8);
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

    private String formatLabel(String settingKey) {
        // Generate label from key in VS Code style: "cds.typeGenerator.enabled" -> "Type Generator: Enabled"
        String[] parts = settingKey.split("\\.");
        if (parts.length < 2) {
            return settingKey;
        }

        // Skip the first part (cds or sapbas)
        StringBuilder label = new StringBuilder("<html>");
        for (int i = 1; i < parts.length; i++) {
            if (i > 1) {
                if (i == parts.length - 1) {
                    label.append(": ");
                } else {
                    label.append(" › ");
                }
            }
            // Make only the last part bold
            if (i == parts.length - 1) {
                label.append("<b>").append(capitalizeWords(parts[i])).append("</b>");
            } else {
                label.append(capitalizeWords(parts[i]));
            }
        }
        label.append("</html>");

        return label.toString();
    }

    private String getDescription(String settingKey) {
        return CdsUserSettings.getDescription(settingKey);
    }

    private JComponent createControlForSetting(String settingKey, Object defaultValue) {
        if (defaultValue instanceof Boolean) {
            return new JBCheckBox();
        }

        // Check if this setting has enum values
        if (CdsUserSettings.hasEnumValues(settingKey)) {
            String[] enumValues = CdsUserSettings.getEnumValues(settingKey);
            JComboBox<String> comboBox = new JComboBox<>(enumValues);
            comboBox.setPreferredSize(new java.awt.Dimension(500, comboBox.getPreferredSize().height));
            return comboBox;
        }

        JBTextField textField = new JBTextField();
        textField.setColumns(45);
        return textField;
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
