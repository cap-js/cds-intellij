package com.sap.cap.cds.intellij.codestyle;

import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.OptionAnchor;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider.SettingsType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider.SettingsType.*;

// TODO remove T
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
     * Name of the parent option.
     */
    public final String parent;
    /**
     * Names of the child options.
     */
    public final List<String> children;
    /**
     * Optional values for the option. Used as dropdown values in UI.
     */
    public final @Nullable CdsCodeStyleSettings.Enum[] values;
    /**
     * Type of the option.
     */
    public final Type type;

    public CdsCodeStyleOption(String name, Type type, T defaultValue, String label, String group, Category category, @Nullable String parent, List<String> children, @Nullable CdsCodeStyleSettings.Enum... values) {
        this.name = name;
        this.type = type;
        this.label = label;
        this.defaultValue = defaultValue;
        this.group = group;
        this.category = category;
        this.parent = parent;
        this.children = children;
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

    public OptionAnchor getAnchor() {
        return parent != null ? OptionAnchor.AFTER : null;
    }

    public String getAnchorOptionName() {
        return parent;
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
        ALIGNMENT("Alignment", LANGUAGE_SPECIFIC),
        BLANK_LINES("Blank Lines", BLANK_LINES_SETTINGS),
        COMMENTS("Comments", COMMENTER_SETTINGS),
        OTHER("Other", LANGUAGE_SPECIFIC),
        SPACES("Spaces", SPACING_SETTINGS),
        TABS_AND_INDENTS("Tabs and Indents", INDENT_SETTINGS),
        WRAPPING_AND_BRACES("Wrapping and Braces", WRAPPING_AND_BRACES_SETTINGS);

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
