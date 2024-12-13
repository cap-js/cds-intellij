package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.codeStyle.WrappingAndBracesPanel;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.sap.cap.cds.intellij.CdsFileType;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOptionDef.Category;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.sap.cap.cds.intellij.lang.CdsLanguage.SAMPLE_SRC;

public class CdsCodeStyleWrappingAndBracesPanel extends WrappingAndBracesPanel {

    public CdsCodeStyleWrappingAndBracesPanel(CodeStyleSettings settings) {
        super(settings);
        init();
    }

    @Override
    protected void init() {
        // HOT-TODO otherwise, nothing is shown
        super.init();

//        myPanel.setMinimumSize(new Dimension(100, 0));
    }

    /* NOTE addOption not working here due to custom settings class, cf. com.intellij.application.options.codeStyle.OptionTableWithPreviewPanel.addOption
               - also, super.initTables does not work because it relies on the same mechanism
     */

//    @Override
//    protected void initTables() {
//        CdsCodeStyleSettings.OPTION_DEFS.forEach((name, option) -> {
//            if (option.category == Category.WRAPPING_AND_BRACES) {
//                if (option.defaultValue instanceof Boolean) {
//                    addOption(option.name, option.label, option.group);
//                } else if (option.defaultValue instanceof Integer d) {
//                    addOption(option.name, option.label, option.group, 0, 100, d, new Function<Integer, String>() {
//                        @Override
//                        public String apply(Integer integer) {
//                            return integer.toString();
//                        }
//                    });
//                }
//            }
//        });
//    }

    @Override
    public LanguageCodeStyleSettingsProvider.SettingsType getSettingsType() {
        return LanguageCodeStyleSettingsProvider.SettingsType.WRAPPING_AND_BRACES_SETTINGS;
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
        try {
            super.apply(settings); // Applies settings from UI to the settings object
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
        CdsPreviewFormattingService.acceptSettings(settings.getCustomSettings(CdsCodeStyleSettings.class));
    }

    @Override
    protected @NlsContexts.TabTitle @NotNull String getTabTitle() {
        return Category.WRAPPING_AND_BRACES.getTitle();
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
