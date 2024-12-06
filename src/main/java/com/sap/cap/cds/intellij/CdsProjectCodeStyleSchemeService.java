package com.sap.cap.cds.intellij;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.*;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettings;
import com.sap.cap.cds.intellij.codestyle.CdsPrettierJsonService;

@Service(Service.Level.PROJECT)
public final class CdsProjectCodeStyleSchemeService {

    private CodeStyleScheme scheme;

    public CdsProjectCodeStyleSchemeService(Project project) {
        CdsPrettierJsonService prettierJsonService = project.getService(CdsPrettierJsonService.class);
        CodeStyleSettingsManager codeStyleManager = CodeStyleSettingsManager.getInstance(project);
        codeStyleManager.subscribe(new CodeStyleSettingsListener() {
            @Override
            public void codeStyleSettingsChanged(CodeStyleSettingsChangeEvent event) {
                scheme = CodeStyleSchemes.getInstance().getCurrentScheme();
                prettierJsonService.putSettings(scheme.getCodeStyleSettings().getCustomSettings(CdsCodeStyleSettings.class));
            }
        });
    }
}
