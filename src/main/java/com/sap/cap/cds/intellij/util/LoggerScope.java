package com.sap.cap.cds.intellij.util;

import com.sap.cap.cds.intellij.CdsPlugin;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsProvider;
import com.sap.cap.cds.intellij.textmate.CdsTextMateBundle;
import com.sap.cap.cds.intellij.usersettings.CdsUserSettingsService;

public enum LoggerScope {
    PLUGIN(CdsPlugin.LABEL),
    TM_BUNDLE("%s/%s".formatted(CdsPlugin.LABEL, CdsTextMateBundle.LABEL)),
    CODE_STYLE("%s/%s".formatted(CdsPlugin.LABEL, CdsCodeStyleSettingsProvider.LABEL)),
    USER_SETTINGS("%s/%s".formatted(CdsPlugin.LABEL, CdsUserSettingsService.LABEL));

    final String label;

    LoggerScope(String scope) {
        this.label = scope;
    }
}
