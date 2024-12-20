package com.sap.cap.cds.intellij.codestyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.*;

import static com.sap.cap.cds.intellij.util.ReflectionUtil.getFieldValue;
import static com.sap.cap.cds.intellij.util.ReflectionUtil.setFieldValue;
import static java.util.stream.Collectors.toMap;

public class CdsCodeStyleSettingsBase extends CustomCodeStyleSettings {
    public static final Map<String, CdsCodeStyleOption<?>> OPTIONS = new LinkedHashMap<>();
    public static final Map<CdsCodeStyleOption.Category, Set<String>> CATEGORY_GROUPS = new LinkedHashMap<>();

    public CdsCodeStyleSettingsBase(@NonNls @NotNull String tagName, @NotNull CodeStyleSettings container) {
        super(tagName, container);
    }

    private static String getEnumLabel(String name, int id) {
        return OPTIONS.get(name).values[id].getLabel();
    }

    private static int getEnumId(CdsCodeStyleOption<?> option, String label) {
        return Arrays.stream(option.values).filter(v -> v.getLabel().equals(label)).findFirst().orElseThrow().getId();
    }

    public void loadFrom(JSONObject json) {
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

    public JSONObject getNonDefaultSettings() {
        var map = OPTIONS.entrySet().stream()
                .map(optionEntry -> {
                    final Object value;
                    try {
                        value = getFieldValue(this, optionEntry.getKey(), null);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }

                    CdsCodeStyleOption<?> option = optionEntry.getValue();
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
}
