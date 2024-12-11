package com.sap.cap.cds.intellij.codestyle;

import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider.SettingsType;

public class CdsCodeStyleOptionDef<T> {
    /**
     * Name of the option. Used as configuration key. Uses camelCase.
     */
    public final String name;
    /**
     * Label of the option. Used in UI. Uses sentence case.
     */
    public final String label;
    /**
     * Default value of the option.
     */
    public final T defaultValue;
    /**
     * Group within category. Used as collapsable heading in UI. Uses sentence case.
     */
    public final String group;
    /**
     * Category of the option. Used as tab title in UI. Uses title case.
     */
    public final Category category;

    public CdsCodeStyleOptionDef(String name, T defaultValue, String label, String group, Category category) {
        this.name = name;
        this.label = label;
        this.defaultValue = defaultValue;
        this.group = group;
        this.category = category;
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

        public static Category fromCdsPrettierSchema(String name, String category) {
            return switch (category) {
                case "Alignment" -> ALIGNMENT;
                case "Whitespace" -> SPACES;
                case "Other" -> name.contains("tab")
                        ? TABS_AND_INDENTS
                        : name.matches("(?:Empty|Single)Line")
                        ? BLANK_LINES
                        : name.matches("keep.*(?:Line|Together)|New[Ll]ine")
                        ? WRAPPING_AND_BRACES
                        : OTHER;
                default -> OTHER; // should not happen
            };
        }

        public String getTitle() {
            return title;
        }

        public SettingsType getSettingsType() {
            return settingsType;
        }
    }
}
