package com.sap.cap.cds.intellij.codestyle;

import com.intellij.openapi.components.Service;
import com.intellij.psi.codeStyle.*;
import org.jetbrains.annotations.NotNull;

@Service
public final class CdsDefaultCodeStyleSettingsService {

    // TODO apply default settings to new projects

    public CdsDefaultCodeStyleSettingsService() {
        CodeStyleSettingsManager codeStyleManager = CodeStyleSettingsManager.getInstance();
        codeStyleManager.subscribe(new CodeStyleSettingsListener() {
            @Override
            public void codeStyleSettingsChanged(@NotNull CodeStyleSettingsChangeEvent event) {
                // TODO implement
            }
        });
    }
}
