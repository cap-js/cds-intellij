package com.sap.cap.cds.intellij.codestyle;

import com.intellij.lang.Language;
import com.intellij.openapi.util.NlsContexts.TabTitle;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CdsCodeStyleTabularPanel extends OptionTableWithPreviewPanel {

    private final LanguageCodeStyleSettingsProvider.SettingsType settingsType;
    private final String title;

    public CdsCodeStyleTabularPanel(CodeStyleSettings settings, LanguageCodeStyleSettingsProvider.SettingsType settingsType, String title) {
        super(settings);
        this.settingsType = settingsType;
        this.title = title;
        init();
    }

    @Override
    public LanguageCodeStyleSettingsProvider.SettingsType getSettingsType() {
        return settingsType;
    }

    @Override
    protected void initTables() {
    }

    @Override
    protected @TabTitle @NotNull String getTabTitle() {
        return title;
    }

    @Override
    public @Nullable Language getDefaultLanguage() {
        return CdsLanguage.INSTANCE;
    }

    @Override
    protected String getPreviewText() {
        return CdsLanguage.SAMPLE_SRC;
    }
}