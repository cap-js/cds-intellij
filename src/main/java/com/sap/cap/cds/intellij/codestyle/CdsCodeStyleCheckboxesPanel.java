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

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Map;

import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Type.BOOLEAN;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsBase.CATEGORY_GROUPS;
import static com.sap.cap.cds.intellij.util.ReflectionUtil.setCustomOptionsEnablement;

/**
 * Custom code-style panel for CDS language with a checkboxes-tree layout. Supports boolean options only.
 */
public class CdsCodeStyleCheckboxesPanel extends OptionTreeWithPreviewPanel implements CdsCodeStylePanel {

    private final Category category;

    public CdsCodeStyleCheckboxesPanel(CodeStyleSettings settings, Category category) {
        super(settings);
        this.category = category;
        init();

        // Prevent long option labels getting cut off in scroll pane
        Dimension minSize = new JLabel("this is quite a long description for a simple setting, eh?").getPreferredSize();
        myPanel.setMinimumSize(minSize);

        getEditor().getSettings().setRightMarginShown(false);
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
        CdsCodeStyleSettings cdsSettings = settings.getCustomSettings(CdsCodeStyleSettings.class);
        setOptionsEnablement(cdsSettings.getChildOptionsEnablement(category));
        CdsPreviewFormattingService.acceptSettings(cdsSettings);
    }

    @Override
    public void setOptionsEnablement(Map<String, Boolean> enablementMap) {
        setCustomOptionsEnablement((DefaultTreeModel) myOptionsTree.getModel(), enablementMap);
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
