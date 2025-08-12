package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.sap.cap.cds.intellij.CdsFileType;
import com.sap.cap.cds.intellij.settings.JsonSettingsManager;
import com.sap.cap.cds.intellij.settings.JsonSettingsService;
import com.sap.cap.cds.intellij.util.LoggerScope;
import org.jetbrains.annotations.NotNull;

import static com.intellij.application.options.CodeStyle.getDefaultSettings;
import static com.sap.cap.cds.intellij.util.LoggerScope.CODE_STYLE;
import static java.util.Arrays.stream;

@Service(Service.Level.PROJECT)
public final class CdsCodeStyleSettingsService extends JsonSettingsService<CdsCodeStyleSettings> {

    public static final String PRETTIER_JSON = ".cdsprettier.json";

    public CdsCodeStyleSettingsService(Project project) {
        super(project, PRETTIER_JSON, CODE_STYLE);
        CodeStyleSettingsManager.getInstance(project).subscribe(event -> {
            logger.debug("Code-style settings changed");
            if (shouldBeSavedImmediately()) {
                updateSettingsFile();
            }
        });
    }

    @Override
    protected JsonSettingsManager<CdsCodeStyleSettings> createJsonManager(Project project, String fileName, LoggerScope loggerScope) {
        return new CdsPrettierJsonManager(project);
    }

    @Override
    protected CdsCodeStyleSettings getSettings() {
        return CodeStyle.getSettings(project).getCustomSettings(CdsCodeStyleSettings.class);
    }

    private static @NotNull CdsCodeStyleSettings getIdeSettings() {
        return getDefaultSettings().getCustomSettings(CdsCodeStyleSettings.class);
    }

    private boolean shouldBeSavedImmediately() {
        if (isSettingsFilePresent()) {
            return true;
        }

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        return stream(fileEditorManager.getOpenFiles())
                .anyMatch(file -> CdsFileType.EXTENSION.equalsIgnoreCase(file.getExtension()));
    }

    @Override
    public void updateProjectSettingsFromFile() {
        if (CodeStyle.usesOwnSettings(project)) {
            if (isSettingsFilePresent()) {
                logger.debug("Loading project-specific settings from file");
                jsonManager.loadSettingsFromFile(getSettings());
            } else {
                logger.debug("Resetting project-specific settings because file is absent");
                jsonManager.reset();
                CodeStyle.setMainProjectSettings(project, CodeStyleSettingsManager.getInstance(project).createSettings());
            }
        } else if (isSettingsFilePresent()) {
            logger.debug("Loading IDE-wide settings from file");
            String prettierJson = ((CdsPrettierJsonManager) jsonManager).readJson();
            if (!prettierJson.isEmpty() && !getIdeSettings().equals(prettierJson)) {
                logger.debug("Updating settings");
                CodeStyleSettings projectSettings = CodeStyleSettingsManager.getInstance().createSettings();
                projectSettings.getCustomSettings(CdsCodeStyleSettings.class).loadFrom(prettierJson);
                CodeStyle.setMainProjectSettings(project, projectSettings);
            }
        } else {
            logger.debug("Keeping IDE-wide settings because file is absent");
        }
    }
}
