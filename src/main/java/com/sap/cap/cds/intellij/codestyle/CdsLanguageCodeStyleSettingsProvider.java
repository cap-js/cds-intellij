package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.*;
import com.intellij.util.LocalTimeCounter;
import com.sap.cap.cds.intellij.FileType;
import com.sap.cap.cds.intellij.Language;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return Language.INSTANCE;
    }

    @Override
    public @NotNull CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings, @NotNull CodeStyleSettings cloneSettings) {
        return new CdsCodeStyleConfigurable(settings, cloneSettings);
    }

    @Override
    public @Nullable PsiFile createFileFromText(@NotNull Project project, @NotNull String text) {
        return PsiFileFactory.getInstance(project).createFileFromText("sample.cds", FileType.INSTANCE, text, LocalTimeCounter.currentTime(), false, false);
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
            super(settings, cloneSettings, Language.INSTANCE.getDisplayName());
        }

        @Override
        protected @NotNull CodeStyleAbstractPanel createPanel(final @NotNull CodeStyleSettings settings) {
            return new CdsCodeStyleMainPanel(getCurrentSettings(), settings);
        }
    }
}