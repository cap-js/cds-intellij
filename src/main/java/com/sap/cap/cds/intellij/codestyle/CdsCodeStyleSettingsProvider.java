package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.*;
import com.intellij.util.LocalTimeCounter;
import com.sap.cap.cds.intellij.CdsFileType;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CdsCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {

    public static final String SAMPLE_FILE_NAME = ".cds-codestyle.sample.cds";
    public static final String LABEL = "Code Style";

    @Override
    public CustomCodeStyleSettings createCustomSettings(@NotNull CodeStyleSettings settings) {
        return new CdsCodeStyleSettings(settings);
    }

    @Override
    public String getConfigurableDisplayName() {
        return "CDS";
    }

    @Override
    public @Nullable String getCodeSample(@NotNull SettingsType settingsType) {
        return CdsCodeStyleSettings.SAMPLE_SRC;
    }

    @Override
    public @NotNull CdsLanguage getLanguage() {
        return CdsLanguage.INSTANCE;
    }

    /**
     * @param initialSettings  The base (initial) settings before changes.
     * @param modifiedSettings The settings to which UI changes are applied (a.k.a. model/clone settings).
     * @see com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider#createConfigurable(com.intellij.psi.codeStyle.CodeStyleSettings, com.intellij.psi.codeStyle.CodeStyleSettings)
     */
    @Override
    public @NotNull CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings initialSettings, @NotNull CodeStyleSettings modifiedSettings) {
        return new CdsCodeStyleConfigurable(initialSettings, modifiedSettings);
    }

    @Override
    public @Nullable PsiFile createFileFromText(@NotNull Project project, @NotNull String text) {
        return PsiFileFactory.getInstance(project).createFileFromText(SAMPLE_FILE_NAME, CdsFileType.INSTANCE, text, LocalTimeCounter.currentTime(), false, false);
    }

    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        if (!(consumer instanceof CdsCodeStylePanel panel)) {
            return;
        }
        CdsCodeStyleSettings.OPTIONS.values().stream()
                .filter(option -> option.category == panel.getCategory())
                .forEach(panel::addOption);
    }

    private static class CdsCodeStyleConfigurable extends CodeStyleAbstractConfigurable {

        /**
         * @param initialSettings  The base (initial) settings before changes.
         * @param modifiedSettings The settings to which UI changes are applied. Initially a clone of the initial settings.
         */
        public CdsCodeStyleConfigurable(@NotNull CodeStyleSettings initialSettings, @NotNull CodeStyleSettings modifiedSettings) {
            super(initialSettings, modifiedSettings, CdsLanguage.INSTANCE.getDisplayName());
        }

        @Override
        protected @NotNull CodeStyleAbstractPanel createPanel(final @NotNull CodeStyleSettings modifiedSettings) {
            return new CdsCodeStyleMainPanel(getCurrentSettings(), modifiedSettings);
        }
    }
}