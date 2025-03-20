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

    private static final Map<CacheKey, Logger> INSTANCES = new java.util.concurrent.ConcurrentHashMap<>();

    private final Project project;
    private final com.intellij.openapi.diagnostic.Logger logger;

    private Logger(Project project, com.intellij.openapi.diagnostic.Logger ijLogger) {
        this.project = project;
        this.logger = ijLogger;
    }

    public static Logger logger(Project project, LoggerScope scope) {
        return INSTANCES.computeIfAbsent(new CacheKey(project, scope), k -> new Logger(project, getInstance(scope.label)));
    }

    public void debug(String message) {
        logger.debug("[%s] %s".formatted(project.getName(), message));
    }

    public void info(String message) {
        logger.info("[%s] %s".formatted(project.getName(), message));
    }

    public void warn(String message) {
        logger.warn("[%s] %s".formatted(project.getName(), message));
    }

    public void error(String message) {
        logger.error("[%s] %s".formatted(project.getName(), message));
    }

    public void error(String message, Throwable t) {
        logger.error("[%s] %s".formatted(project.getName(), message), t);
    }

}
