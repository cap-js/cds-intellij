package com.sap.cap.cds.intellij.codestyle;

import com.intellij.openapi.components.Service;
import com.intellij.psi.codeStyle.CodeStyleSettingsChangeEvent;
import com.intellij.psi.codeStyle.CodeStyleSettingsListener;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import org.jetbrains.annotations.NotNull;

@Service
public final class CdsCodeStyleDefaultSettingsService {

    // TODO apply default settings to new projects

    public CdsCodeStyleDefaultSettingsService() {
        CodeStyleSettingsManager codeStyleManager = CodeStyleSettingsManager.getInstance();
        codeStyleManager.subscribe(new CodeStyleSettingsListener() {
            @Override
            public void codeStyleSettingsChanged(@NotNull CodeStyleSettingsChangeEvent event) {
                // TODO implement
            }
        });
    }
}
