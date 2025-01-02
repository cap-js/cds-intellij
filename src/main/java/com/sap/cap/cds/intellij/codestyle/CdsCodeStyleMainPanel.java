package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import org.jetbrains.annotations.NotNull;

public class CdsCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {

    protected CdsCodeStyleMainPanel(CodeStyleSettings currentSettings, @NotNull CodeStyleSettings settings) {
        super(CdsLanguage.INSTANCE, currentSettings, settings);

        // Enable saving of code-style settings
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            project.getService(CdsCodeStyleSettingsService.class);
        }
    }

    @Override
    protected void initTabs(CodeStyleSettings settings) {
        CdsCodeStylePanelFactory factory = new CdsCodeStylePanelFactory(settings);

        CdsCodeStyleSettings.CATEGORY_GROUPS.keySet().forEach(category -> {
            addTab(factory.createTabPanel(category));
        });
    }

    @Override
    protected String getPreviewText() {
        return CdsCodeStyleSettings.SAMPLE_SRC;
    }
}