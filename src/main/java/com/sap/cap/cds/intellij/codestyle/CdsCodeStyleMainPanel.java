package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.sap.cap.cds.intellij.Language;
import org.jetbrains.annotations.NotNull;

public class CdsCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {
    public static final String ALIGNMENT = "Alignment";
    public static final String SPACING_H = "Horizontal spacing";
    public static final String SPACING_V = "Vertical spacing";
    public static final String MISC = "Misc";

    protected CdsCodeStyleMainPanel(CodeStyleSettings currentSettings, @NotNull CodeStyleSettings settings) {
        super(Language.INSTANCE, currentSettings, settings);
    }

    @Override
    protected void initTabs(CodeStyleSettings settings) {
//        super.initTabs(settings);
        addTab(new CdsCodeStyleAlignmentPanel(settings));
    }

}
