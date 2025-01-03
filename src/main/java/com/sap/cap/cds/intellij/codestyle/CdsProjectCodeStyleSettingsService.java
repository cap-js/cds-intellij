package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.CodeStyleSettingsChangeEvent;
import com.intellij.psi.codeStyle.CodeStyleSettingsListener;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import org.jetbrains.annotations.NotNull;

// TODO test .cdsprettier.json reading/updating on a project level

@Service(Service.Level.PROJECT)
public final class CdsProjectCodeStyleSettingsService {

    private final Project project;

    public CdsProjectCodeStyleSettingsService(Project project) {
        this.project = project;
        CdsPrettierJsonService prettierJsonService = project.getService(CdsPrettierJsonService.class);
        CodeStyleSettingsManager manager = CodeStyleSettingsManager.getInstance(project);
        manager.subscribe(new CodeStyleSettingsListener() {
            @Override
            public void codeStyleSettingsChanged(@NotNull CodeStyleSettingsChangeEvent event) {
                prettierJsonService.saveSettingsToFile(getSettings());
            }
        });
    }

    public CdsCodeStyleSettings getSettings() {
        return CodeStyle.getSettings(project).getCustomSettings(CdsCodeStyleSettings.class);
    }

    public void updateSettingsFromFile() {
        CdsPrettierJsonService prettierJsonService = project.getService(CdsPrettierJsonService.class);
        prettierJsonService.loadSettingsFromFile(getSettings());
    }
}
