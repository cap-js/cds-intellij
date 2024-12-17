package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOptionDef.Category.ALIGNMENT;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOptionDef.Category.COMMENTS;

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
        /*
         NOTE: we use default tabs because deriving from OptionTableWithPreviewPanel is unfeasible for non-boolean options
         due to visibility of required methods and fields
        */
        super.initTabs(settings);
        addTab(new CdsCodeStyleCustomPanel(settings, ALIGNMENT));
        addTab(new CdsCodeStyleCustomPanel(settings, COMMENTS));
    }

    @Override
    protected void updatePreview(boolean useDefaultSample) {
        super.updatePreview(useDefaultSample);
    }
}