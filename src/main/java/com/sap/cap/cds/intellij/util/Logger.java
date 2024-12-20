package com.sap.cap.cds.intellij.util;

import com.sap.cap.cds.intellij.CdsPlugin;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsProvider;
import com.sap.cap.cds.intellij.textmate.CdsTextMateBundle;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class Logger {

    public static final com.intellij.openapi.diagnostic.Logger PLUGIN = com.intellij.openapi.diagnostic.Logger.getInstance(CdsPlugin.LABEL);

    public static final com.intellij.openapi.diagnostic.Logger TM_BUNDLE = getInstance("%s/%s".formatted(CdsPlugin.LABEL, CdsTextMateBundle.LABEL));

    public static final com.intellij.openapi.diagnostic.Logger CODE_STYLE = getInstance("%s/%s".formatted(CdsPlugin.LABEL, CdsCodeStyleSettingsProvider.LABEL));

}
