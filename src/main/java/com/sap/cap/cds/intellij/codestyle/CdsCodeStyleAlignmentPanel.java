package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.codeStyle.OptionTreeWithPreviewPanel;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.sap.cap.cds.intellij.CdsFileType;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOptionDef.Category;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static com.sap.cap.cds.intellij.lang.CdsLanguage.SAMPLE_SRC;

public class CdsCodeStyleAlignmentPanel extends OptionTreeWithPreviewPanel {

    public CdsCodeStyleAlignmentPanel(CodeStyleSettings settings) {
        super(settings);
        init();
    }

    @Override
    protected void init() {
        super.init();

        myPanel.setMinimumSize(new Dimension(100, 0));
    }

    @Override
    protected void initTables() {
        CdsCodeStyleSettings.CATEGORY_GROUPS.get(Category.ALIGNMENT).forEach(this::initCustomOptions);
    }

    @Override
    public LanguageCodeStyleSettingsProvider.SettingsType getSettingsType() {
        return LanguageCodeStyleSettingsProvider.SettingsType.LANGUAGE_SPECIFIC;
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
        return Category.ALIGNMENT.getTitle();
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
