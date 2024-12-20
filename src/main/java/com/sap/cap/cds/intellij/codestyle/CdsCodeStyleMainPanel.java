package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category.*;

public class CdsCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {

    protected CdsCodeStyleMainPanel(CodeStyleSettings currentSettings, @NotNull CodeStyleSettings settings) {
        super(CdsLanguage.INSTANCE, currentSettings, settings);

        // Enable saving of code-style settings
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            project.getService(CdsProjectCodeStyleSettingsService.class);
        }
        getApplication().getService(CdsDefaultCodeStyleSettingsService.class);
    }

    @Override
    protected void initTabs(CodeStyleSettings settings) {
        addTab(new CdsCodeStyleTabularPanel(settings, TABS_AND_INDENTS));
        addTab(new CdsCodeStyleTabularPanel(settings, SPACES));
        addTab(new CdsCodeStyleCustomPanel(settings, ALIGNMENT));
        addTab(new CdsCodeStyleTabularPanel(settings, WRAPPING_AND_BRACES));
        addTab(new CdsCodeStyleTabularPanel(settings, BLANK_LINES));
        addTab(new CdsCodeStyleCustomPanel(settings, COMMENTS));
        addTab(new CdsCodeStyleTabularPanel(settings, OTHER));
    }

    @Override
    protected String getPreviewText() {
        return CdsLanguage.SAMPLE_SRC;
    }
}