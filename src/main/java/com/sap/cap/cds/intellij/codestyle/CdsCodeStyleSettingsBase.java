package com.sap.cap.cds.intellij.codestyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.*;

import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Type.BOOLEAN;
import static com.sap.cap.cds.intellij.util.ReflectionUtil.getFieldValue;
import static com.sap.cap.cds.intellij.util.ReflectionUtil.setFieldValue;
import static java.util.stream.Collectors.toMap;

public abstract class CdsCodeStyleSettingsBase extends CustomCodeStyleSettings {
    public static final Map<String, CdsCodeStyleOption> OPTIONS = new LinkedHashMap<>();
    public static final Map<Category, Set<String>> CATEGORY_GROUPS = new LinkedHashMap<>();

    CdsCodeStyleSettingsBase(@NotNull CodeStyleSettings container) {
        super("CDSCodeStyleSettings", container);
    }

    private static String getEnumLabel(String name, int id) {
        return OPTIONS.get(name).values[id].getLabel();
    }

    private static int getEnumId(CdsCodeStyleOption option, String label) {
        return Arrays.stream(option.values).filter(v -> v.getLabel().equals(label)).findFirst().orElseThrow().getId();
    }

    public void loadFrom(String prettierJson) {
        var json = new JSONObject(prettierJson);
        OPTIONS.forEach((name, option) -> {
            if (!json.has(name)) {
                return;
            }
            final var value = json.get(name);
            if (value != null) {
                try {
                    if (option.values.length > 0) {
                        setFieldValue(this, name, getEnumId(option, (String) value));
                    } else {
                        setFieldValue(this, name, value);
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public boolean equals(String prettierJson) {
        CodeStyleSettings container = CodeStyleSettingsManager.getInstance().createSettings();
        CdsCodeStyleSettings other = container.getCustomSettings(CdsCodeStyleSettings.class);
        other.loadFrom(prettierJson);
        return this.equals(other);
    }

    public boolean equals(CdsCodeStyleSettings other) {
        return OPTIONS.keySet().stream()
                .allMatch(name -> {
                    try {
                        return Objects.equals(getFieldValue(this, name, null), getFieldValue(other, name, null));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public JSONObject getNonDefaultSettings() {
        var map = OPTIONS.entrySet().stream()
                .map(optionEntry -> {
                    Object value = getValue(optionEntry.getKey());
                    CdsCodeStyleOption option = optionEntry.getValue();
                    if (!option.defaultValue.equals(value)) {
                        if (option.values.length > 0) {
                            return Map.entry(optionEntry.getKey(), getEnumLabel(optionEntry.getKey(), (int) value));
                        }
                        return Map.entry(optionEntry.getKey(), value);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new JSONObject(map);
    }

    private Object getValue(String name) {
        final Object value;
        try {
            value = getFieldValue(this, name, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public Map<String, Boolean> getChildOptionsEnablement(Category category) {
        return OPTIONS.values().stream()
                .filter(option -> option.category == category)
                .filter(option -> option.type == BOOLEAN && !option.children.isEmpty())
                .flatMap(parent -> parent.children.stream()
                        .map(child -> Map.entry(child, (boolean) getValue(parent.name))))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
