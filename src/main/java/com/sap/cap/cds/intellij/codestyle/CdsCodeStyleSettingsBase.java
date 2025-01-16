package com.sap.cap.cds.intellij.codestyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.*;

import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Type.BOOLEAN;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Type.ENUM;
import static com.sap.cap.cds.intellij.util.JsonUtil.toJSONObject;
import static com.sap.cap.cds.intellij.util.JsonUtil.toSortedString;
import static com.sap.cap.cds.intellij.util.ReflectionUtil.getFieldValue;
import static com.sap.cap.cds.intellij.util.ReflectionUtil.setFieldValue;
import static java.util.stream.Collectors.toMap;

public abstract class CdsCodeStyleSettingsBase extends CustomCodeStyleSettings {
    public static final Map<String, CdsCodeStyleOption> OPTIONS = new LinkedHashMap<>();
    public static final Map<Category, Set<String>> CATEGORY_GROUPS = new LinkedHashMap<>();
    protected final List<String> loadedOptions = new ArrayList<>();

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
        loadedOptions.clear();
        var json = toJSONObject(prettierJson);
        OPTIONS.forEach((name, option) -> {
            if (!json.has(name)) {
                return;
            }
            final var value = json.get(name);
            if (value != null) {
                loadedOptions.add(name);
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

    public String getNonDefaultSettings() {
        var map = OPTIONS.values().stream()
                .filter(option -> !option.defaultValue.equals(getValue(option.name)))
                .map(this::getEntry)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        return toSortedString(new JSONObject(map));
    }

    public String getLoadedOrNonDefaultSettings() {
        var map = OPTIONS.values().stream()
                .filter(option -> loadedOptions.contains(option.name) || !option.defaultValue.equals(getValue(option.name)))
                .map(this::getEntry)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        return toSortedString(new JSONObject(map));
    }

    public String toJSON() {
        var map = OPTIONS.values().stream()
                .map(this::getEntry)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        return toSortedString(new JSONObject(map));
    }

    private Map.Entry<String, ?> getEntry(CdsCodeStyleOption option) {
        Object value = getValue(option.name);
        return option.type == ENUM
                ? Map.entry(option.name, getEnumLabel(option.name, (int) value))
                : Map.entry(option.name, value);
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
