package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category;

import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Type.BOOLEAN;

public class CdsCodeStyleTabPanelFactory {

    private final CodeStyleSettings settings;

    CdsCodeStyleTabPanelFactory(CodeStyleSettings settings) {
        this.settings = settings;
    }

    public CodeStyleAbstractPanel createTabPanel(Category category) {
        boolean allBoolean = CdsCodeStyleSettings.OPTIONS.values().stream()
                .filter(option -> option.category == category)
                .allMatch(option -> option.type == BOOLEAN);

        return allBoolean
                ? new CdsCodeStyleCheckboxesPanel(settings, category)
                : new CdsCodeStyleTabularPanel(settings, category);
    }

}
