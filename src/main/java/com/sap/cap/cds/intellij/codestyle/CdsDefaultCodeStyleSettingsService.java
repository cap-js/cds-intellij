package com.sap.cap.cds.intellij.codestyle;

import com.intellij.openapi.components.Service;
import com.intellij.psi.codeStyle.*;

@Service
public final class CdsDefaultCodeStyleSettingsService {

    // TODO apply default settings to new projects

    public CdsDefaultCodeStyleSettingsService() {
        CodeStyleSettingsManager codeStyleManager = CodeStyleSettingsManager.getInstance();
        codeStyleManager.subscribe(new CodeStyleSettingsListener() {
            @Override
            public void codeStyleSettingsChanged(CodeStyleSettingsChangeEvent event) {
                // TODO implement
            }
        });
    }
}
