package com.sap.cap.cds.intellij.util;

import com.sap.cap.cds.intellij.CdsPlugin;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsProvider;
import com.sap.cap.cds.intellij.textmate.CdsTextMateBundle;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class Logger {

    public static final com.intellij.openapi.diagnostic.Logger PLUGIN = com.intellij.openapi.diagnostic.Logger.getInstance(CdsPlugin.LABEL);

    public static final com.intellij.openapi.diagnostic.Logger TM_BUNDLE = getInstance("%s/%s".formatted(CdsPlugin.LABEL, CdsTextMateBundle.LABEL));

    public static final com.intellij.openapi.diagnostic.Logger CODE_STYLE = getInstance("%s/%s".formatted(CdsPlugin.LABEL, CdsCodeStyleSettingsProvider.LABEL));

    private String project;

    private Logger() {}

    Logger(String project) {
        this.project = " [%s]".formatted(project);
    }

    public static Logger logger(String project) {
        return new Logger(project);
    }

    public com.intellij.openapi.diagnostic.@NotNull Logger PLUGIN() {
        return getInstance("%s%s".formatted(CdsPlugin.LABEL, project));
    }

    public com.intellij.openapi.diagnostic.@NotNull Logger TM_BUNDLE() {
        return getInstance("%s/%s%s".formatted(CdsPlugin.LABEL, CdsTextMateBundle.LABEL, project));
    }

    public com.intellij.openapi.diagnostic.@NotNull Logger CODE_STYLE() {
        return getInstance("%s/%s%s".formatted(CdsPlugin.LABEL, CdsCodeStyleSettingsProvider.LABEL, project));
    }

}
