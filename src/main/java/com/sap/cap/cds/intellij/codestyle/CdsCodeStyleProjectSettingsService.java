package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsChangeEvent;
import com.intellij.psi.codeStyle.CodeStyleSettingsListener;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

// TODO test .cdsprettier.json reading/updating on a project level

@Service(Service.Level.PROJECT)
public final class CdsCodeStyleProjectSettingsService {

    private final Project project;
    private final CdsPrettierJsonService prettierJsonService;

    public CdsCodeStyleProjectSettingsService(Project project) {
        this.project = project;
        prettierJsonService = project.getService(CdsPrettierJsonService.class);
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

    public void updateSettings(CodeStyleSettings settings) {
        CodeStyle.setMainProjectSettings(project, settings);
        prettierJsonService.saveSettingsToFile(getSettings());
    }

    public void updateSettingsFromFile() throws IOException {
        CdsPrettierJsonService prettierJsonService = project.getService(CdsPrettierJsonService.class);
        prettierJsonService.loadSettingsFromFile(getSettings());
    }
}
