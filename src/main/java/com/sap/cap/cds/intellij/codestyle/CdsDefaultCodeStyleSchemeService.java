package com.sap.cap.cds.intellij.codestyle;

import com.intellij.openapi.components.Service;
import com.intellij.psi.codeStyle.*;

@Service
public final class CdsDefaultCodeStyleSchemeService {

    private CodeStyleScheme scheme;

    // TODO apply default scheme to new projects

    public CdsDefaultCodeStyleSchemeService() {
        CodeStyleSettingsManager codeStyleManager = CodeStyleSettingsManager.getInstance();
        codeStyleManager.subscribe(new CodeStyleSettingsListener() {
            @Override
            public void codeStyleSettingsChanged(CodeStyleSettingsChangeEvent event) {
                scheme = CodeStyleSchemes.getInstance().getDefaultScheme();
            }
        });
    }
}
