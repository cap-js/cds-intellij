package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.codeStyle.OptionTreeWithPreviewPanel;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider.SettingsType;
import com.sap.cap.cds.intellij.CdsFileType;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider.SettingsType.LANGUAGE_SPECIFIC;
import static com.sap.cap.cds.intellij.lang.CdsLanguage.SAMPLE_SRC;

public class CdsCodeStyleCustomPanel extends OptionTreeWithPreviewPanel {

    public final Category category;

    public CdsCodeStyleCustomPanel(CodeStyleSettings settings, Category category) {
        super(settings);
        this.category = category;
        init();
    }

    @Override
    protected void initTables() {
        CdsCodeStyleSettings.CATEGORY_GROUPS.get(category).forEach(this::initCustomOptions);
    }

    @Override
    public SettingsType getSettingsType() {
        return LANGUAGE_SPECIFIC;
    }

    @Override
    protected @NotNull FileType getFileType() {
        return CdsFileType.INSTANCE;
    }

    @Override
    public com.intellij.lang.@Nullable Language getDefaultLanguage() {
        return CdsLanguage.INSTANCE;
    }

    @Override
    protected String getPreviewText() {
        return SAMPLE_SRC;
    }

    @Override
    public void apply(@NotNull CodeStyleSettings settings) {
        super.apply(settings); // Applies settings from UI to the settings object
        CdsPreviewFormattingService.acceptSettings(settings.getCustomSettings(CdsCodeStyleSettings.class));
    }

    @Override
    protected @NlsContexts.TabTitle @NotNull String getTabTitle() {
        return category.getTitle();
    }

    /**
     * Show CDS code-style options via the corresponding panel
     */
    @Override
    protected void customizeSettings() {
        LanguageCodeStyleSettingsProvider provider = LanguageCodeStyleSettingsProvider.forLanguage(CdsLanguage.INSTANCE);
        if (provider != null) {
            provider.customizeSettings(this, getSettingsType());
        }
    }

}
