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

import java.awt.*;

import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Type.BOOLEAN;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsBase.CATEGORY_GROUPS;

/**
 * Custom code-style panel for CDS language with a checkboxes-tree layout. Supports boolean options only.
 */
public class CdsCodeStyleCheckboxesPanel extends OptionTreeWithPreviewPanel implements CdsCodeStylePanel {

    private final Category category;

    public CdsCodeStyleCheckboxesPanel(CodeStyleSettings settings, Category category) {
        super(settings);
        this.category = category;
        init();

        // Prevent long option labels getting cut off
        Dimension preferredSize = this.myPanel.getPreferredSize();
        preferredSize.width *= 2;
        this.myPanel.setMinimumSize(preferredSize);
    }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    protected void initTables() {
        CATEGORY_GROUPS.get(category).forEach(this::initCustomOptions);
    }

    @Override
    public SettingsType getSettingsType() {
        if (category == null) {
            return null;
        }
        return category.getSettingsType();
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
        return CdsCodeStyleSettings.SAMPLE_SRC;
    }

    @Override
    public void apply(@NotNull CodeStyleSettings settings) {
        super.apply(settings); // Applies settings from UI to the settings object
        CdsPreviewFormattingService.acceptSettings(settings.getCustomSettings(CdsCodeStyleSettings.class));
    }

    @Override
    protected @NlsContexts.TabTitle @NotNull String getTabTitle() {
        if (category == null) {
            return "";
        }
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

    @Override
    public void addOption(CdsCodeStyleOption<?> option) {
        if (option.type == BOOLEAN) {
            showCustomOption(CdsCodeStyleSettings.class, option.name, option.label, option.group, option.getAnchor(), option.getAnchorOptionName());
        }
    }
}
