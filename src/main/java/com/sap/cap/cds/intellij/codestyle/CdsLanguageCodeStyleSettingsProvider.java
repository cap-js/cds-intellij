package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.sap.cap.cds.intellij.Language.INSTANCE;

public class CdsLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {

    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return new CdsCodeStyleSettings(settings);
    }

    @Override
    public String getConfigurableDisplayName() {
        return "CDS";
    }

    @Override
    public @Nullable String getCodeSample(@NotNull SettingsType settingsType) {
        return """
entity En {
  key k  : Integer;
      el : String;
}
                """;
    }

    @Override
    public @NotNull Language getLanguage() {
        return INSTANCE;
    }

    @Override
    public @NotNull CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings, @NotNull CodeStyleSettings cloneSettings) {
        return new CdsCodeStyleConfigurable(settings, cloneSettings);
    }

    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        switch (settingsType) {
            case BLANK_LINES_SETTINGS -> {
            }
            case SPACING_SETTINGS -> {
            }
            case WRAPPING_AND_BRACES_SETTINGS -> {
            }
            case INDENT_SETTINGS -> {
            }
            case COMMENTER_SETTINGS -> {
            }
            case LANGUAGE_SPECIFIC -> {
                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_AS", "Align 'as' keyword", "'as' keyword'");
                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_AS_IN_ENTITIES", "In entities", "'as' keyword'");
                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_AS_IN_SELECT_ITEMS", "In 'select' items", "'as' keyword'");
                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_AS_IN_USING", "In 'using' statements", "'as' keyword'");

                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_AFTER_KEY", "Align after 'key' keyword", "'key' keyword'");

                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_ACTIONS_FUNCTIONS", "Align actions and functions", "Actions and functions");

                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_ANNOTATIONS", "Align annotations", "Annotations");
                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_ANNOTATIONS_PRE", "Pre-annotations", "Annotations");
                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_ANNOTATIONS_POST", "Post-annotations", "Annotations");
                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_ANNOTATIONS_COLONS", "Colons", "Annotations");
                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_ANNOTATIONS_VALUES", "Values", "Annotations");

                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_EXPRESSIONS_CONDITIONS", "Align expressions and conditions", "Expressions and conditions");
                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_EXPRESSIONS_CONDITIONS_WITHIN_BLOCK", "Within block", "Expressions and conditions");

                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_TYPES", "Align types", "Types");
                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_TYPES_WITHIN_BLOCK", "Within block", "Types");
                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_TYPES_COLONS", "Colons", "Types");
                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_TYPES_EQUALS", "Equals", "Types");
                consumer.showCustomOption(CdsCodeStyleSettings.class, "ALIGN_TYPES_COMPOSITION_STRUCT_RIGHT", "Composition struct as type", "Types");
            }
        }
    }

    private class CdsCodeStyleConfigurable extends CodeStyleAbstractConfigurable {
        public CdsCodeStyleConfigurable(@NotNull CodeStyleSettings settings, @NotNull CodeStyleSettings cloneSettings) {
            super(settings, cloneSettings, INSTANCE.getDisplayName());
        }

        @Override
        protected @NotNull CodeStyleAbstractPanel createPanel(final @NotNull CodeStyleSettings settings) {
            return new CdsCodeStyleMainPanel(getCurrentSettings(), settings);
        }
    }
}