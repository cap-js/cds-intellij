package com.sap.cap.cds.intellij.codestyle;

import com.intellij.lang.Language;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts.TabTitle;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Custom code-style panel for CDS language with a tabular layout. Supports all types of options (boolean, integer, enum).
 */
public class CdsCodeStyleTabularPanel extends CdsCodeStyleTabularPanelBase implements CdsCodeStylePanel {

    private final CdsCodeStyleOption.Category category;

    public CdsCodeStyleTabularPanel(CodeStyleSettings settings, CdsCodeStyleOption.Category category) {
        super(settings);
        this.category = category;
        init();

        getEditor().getSettings().setRightMarginShown(false);
    }

    @Override
    public CdsCodeStyleOption.Category getCategory() {
        return category;
    }

    @Override
    public LanguageCodeStyleSettingsProvider.SettingsType getSettingsType() {
        if (category == null) {
            return null;
        }
        return category.getSettingsType();
    }

    @Override
    protected void initTables() {
    }

    @Override
    protected @TabTitle @NotNull String getTabTitle() {
        if (category == null) {
            return "";
        }
        return category.getTitle();
    }

    @Override
    public @Nullable Language getDefaultLanguage() {
        return CdsLanguage.INSTANCE;
    }

    @Override
    protected String getPreviewText() {
        return CdsCodeStyleSettings.SAMPLE_SRC;
    }

    @Override
    public void apply(@NotNull CodeStyleSettings settings) throws ConfigurationException {
        super.apply(settings);
        CdsCodeStyleSettings cdsSettings = settings.getCustomSettings(CdsCodeStyleSettings.class);
        setOptionsEnablement(cdsSettings.getChildOptionsEnablement(category));
        CdsCodeStylePreviewFormattingService.acceptSettings(cdsSettings);
    }
}