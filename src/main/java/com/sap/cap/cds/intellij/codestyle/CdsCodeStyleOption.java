package com.sap.cap.cds.intellij.codestyle;

import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider.SettingsType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider.SettingsType.*;

// HOT-TODO rid of T ?
public class CdsCodeStyleOption<T> {
    /**
     * Name of the option (camelCase). Used as configuration key.
     */
    public final String name;
    /**
     * Label of the option (sentence case). Used as UI label.
     */
    public final String label;
    /**
     * Default value of the option.
     */
    public final T defaultValue;
    /**
     * Group within category (sentence case). Used as collapsable section title in UI.
     */
    public final String group;
    /**
     * Category of the option (title case). Used as tab title in UI.
     */
    public final Category category;
    /**
     * Optional values for the option. Used as dropdown values in UI.
     */
    public final @Nullable CdsCodeStyleSettings.Enum[] values;
    /**
     * Type of the option.
     */
    public final Type type;

    public CdsCodeStyleOption(String name, Type type, T defaultValue, String label, String group, Category category, @Nullable CdsCodeStyleSettings.Enum... values) {
        this.name = name;
        this.type = type;
        this.label = label;
        this.defaultValue = defaultValue;
        this.group = group;
        this.category = category;
        this.values = values;
    }

    public String[] getValuesLabels() {
        if (type != Type.ENUM) {
            throw new IllegalStateException("Option is not an enum");
        }
        return Arrays.stream(values).map(value -> value.getLabel()).toArray(String[]::new);
    }

    public int[] getValuesIds() {
        if (type != Type.ENUM) {
            throw new IllegalStateException("Option is not an enum");
        }
        return Arrays.stream(values).mapToInt(value -> value.getId()).toArray();
    }

    public enum Type {
        BOOLEAN(Boolean.class),
        ENUM(Integer.class),
        INT(Integer.class);

        private final Class<?> fieldType;

        Type(Class<?> fieldType) {
            this.fieldType = fieldType;
        }

        public Class<?> getFieldType() {
            return fieldType;
        }
    }

    public enum Category {
        ALIGNMENT("Alignment", SettingsType.LANGUAGE_SPECIFIC),
        BLANK_LINES("Blank Lines", SettingsType.BLANK_LINES_SETTINGS),
        COMMENTS("Comments", SettingsType.COMMENTER_SETTINGS),
        OTHER("Other", SettingsType.LANGUAGE_SPECIFIC),
        SPACES("Spaces", SettingsType.SPACING_SETTINGS),
        TABS_AND_INDENTS("Tabs and Indents", SettingsType.INDENT_SETTINGS),
        WRAPPING_AND_BRACES("Wrapping and Braces", SettingsType.WRAPPING_AND_BRACES_SETTINGS);

        private final String title;
        private final SettingsType settingsType;

        Category(String title, SettingsType settingsType) {
            this.title = title;
            this.settingsType = settingsType;
        }

        public String getTitle() {
            return title;
        }

        public SettingsType getSettingsType() {
            return settingsType;
        }
    }
}
