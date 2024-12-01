package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.*;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category;
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
                if (consumer instanceof CdsCodeStyleAlignmentPanel) {
                    CdsCodeStyleSettings.OPTIONS.forEach((name, option) -> {
                        if (option.category == Category.ALIGNMENT) {
                            consumer.showCustomOption(CdsCodeStyleSettings.class, name, option.label, option.group);
                        }
                    });
                }
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