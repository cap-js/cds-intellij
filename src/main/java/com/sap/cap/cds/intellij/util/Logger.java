package com.sap.cap.cds.intellij.util;

import com.intellij.openapi.project.Project;
import com.sap.cap.cds.intellij.CdsPlugin;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsProvider;
import com.sap.cap.cds.intellij.textmate.CdsTextMateBundle;

import java.util.Map;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class Logger {

    public static final com.intellij.openapi.diagnostic.Logger PLUGIN = getInstance(CdsPlugin.LABEL);

    public static final com.intellij.openapi.diagnostic.Logger TM_BUNDLE = getInstance("%s/%s".formatted(CdsPlugin.LABEL, CdsTextMateBundle.LABEL));

    public static final com.intellij.openapi.diagnostic.Logger CODE_STYLE = getInstance("%s/%s".formatted(CdsPlugin.LABEL, CdsCodeStyleSettingsProvider.LABEL));

    private static final Map<Project, Logger> INSTANCES = new java.util.concurrent.ConcurrentHashMap<>();

    private final Map<LoggerScope, com.intellij.openapi.diagnostic.Logger> loggers = new java.util.EnumMap<>(LoggerScope.class);

    private final String project;

    private Logger(Project project) {
        this.project = " [%s]".formatted(project.getName());
    }

    public static Logger logger(Project project) {
        return INSTANCES.computeIfAbsent(project, Logger::new);
    }

    public com.intellij.openapi.diagnostic.Logger scope(LoggerScope scope) {
        return loggers.computeIfAbsent(scope, s -> getInstance("%s%s".formatted(s.scope, project)));
    }

}
