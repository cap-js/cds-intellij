package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.sap.cap.cds.intellij.CdsFileType;
import com.sap.cap.cds.intellij.util.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.intellij.openapi.project.ProjectUtil.guessProjectDir;
import static com.sap.cap.cds.intellij.util.JsonUtil.isJsonEqual;
import static com.sap.cap.cds.intellij.util.Logger.logger;
import static com.sap.cap.cds.intellij.util.LoggerScope.CODE_STYLE;
import static java.nio.file.Files.readString;
import static java.util.Arrays.stream;

@Service(Service.Level.PROJECT)
public final class CdsCodeStyleSettingsService {

    public static final String PRETTIER_JSON = ".cdsprettier.json";
    private final Project project;
    private final Logger logger;
    private final CdsPrettierJsonManager prettierJsonManager;

    public CdsCodeStyleSettingsService(Project project) {
        this.project = project;
        this.logger = logger(project, CODE_STYLE);
        prettierJsonManager = new CdsPrettierJsonManager();
        CodeStyleSettingsManager.getInstance(project).subscribe(event -> {
            logger.debug("Code-style settings changed");
            if (shouldBeSavedImmediately()) {
                updateSettingsFile();
            }
        });
    }

    private static @NotNull CdsCodeStyleSettings getIdeSettings() {
        return CodeStyle.getDefaultSettings().getCustomSettings(CdsCodeStyleSettings.class);
    }

    private boolean shouldBeSavedImmediately() {
        if (isSettingsFilePresent()) {
            return true;
        }

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        return stream(fileEditorManager.getOpenFiles())
                .anyMatch(file -> CdsFileType.EXTENSION.equalsIgnoreCase(file.getExtension()));
    }

    public boolean isSettingsFilePresent() {
        return prettierJsonManager.isJsonFilePresent();
    }

    public boolean isSettingsFileChanged() {
        return prettierJsonManager.isSettingsFileChanged();
    }

    public void updateSettingsFile() {
        prettierJsonManager.saveSettingsToFile(getSettings());
    }

    public void updateProjectSettingsFromFile() {
        if (CodeStyle.usesOwnSettings(project)) {
            if (isSettingsFilePresent()) {
                logger.debug("Loading project-specific settings from file");
                prettierJsonManager.loadSettingsFromFile(getSettings());
            } else {
                logger.debug("Resetting project-specific settings because file is absent");
                prettierJsonManager.reset();
                CodeStyle.setMainProjectSettings(project, CodeStyleSettingsManager.getInstance(project).createSettings());
            }
        } else if (isSettingsFilePresent()) { // deletion of .cdsprettier.json has no effect
            logger.debug("Loading IDE-wide settings from file");
            String prettierJson = prettierJsonManager.readJson();
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

    private CdsCodeStyleSettings getSettings() {
        return CodeStyle.getSettings(project).getCustomSettings(CdsCodeStyleSettings.class);
    }

    private final class CdsPrettierJsonManager {

        File jsonFile;
        @NotNull String jsonCached = "";

        CdsPrettierJsonManager() {
            // assuming no changes to project directory
            VirtualFile guessed = guessProjectDir(project);
            String projectDir = guessed != null
                    ? guessed.getPath()
                    : project.getBasePath();
            logger.debug("Project directory: %s".formatted(projectDir));
            if (projectDir != null) {
                jsonFile = getJsonFile(projectDir);
                logger.debug("Settings file: %s".formatted(jsonFile));
            }
            reset();
        }

        boolean isJsonFilePresent() {
            boolean present = jsonFile != null && jsonFile.exists();
            logger.debug("Settings file present: %s".formatted(present));
            return present;
        }

        void loadSettingsFromFile(@NotNull CdsCodeStyleSettings settings) {
            logger.debug("Loading settings from file");
            String json = readJson();
            if (!json.isEmpty()) {
                try {
                    settings.loadFrom(json);
                } catch (JSONException e) {
                    logger.error("Failed to parse JSON '%s'".formatted(json), e);
                }
            }
        }

        @NotNull
        private String readJson() {
            logger.debug("Reading settings from file");
            if (!isJsonFilePresent()) {
                return "";
            }
            try {
                jsonCached = readString(jsonFile.toPath());
                logger.debug("Read settings: %s".formatted(jsonCached.replaceAll(" *\n *", " ")));
                return jsonCached;
            } catch (IOException e) {
                logger.error("Failed to read [%s]".formatted(jsonFile), e);
            }
            return "";
        }

        void saveSettingsToFile(@NotNull CdsCodeStyleSettings settings) {
            logger.debug("Saving settings to file");
            if (jsonFile == null) {
                logger.debug("File is null");
                return;
            }
            if (!jsonFile.getParentFile().exists()) {
                logger.debug("Directory [%s] does not exist".formatted(jsonFile.getParentFile()));
                return;
            }
            if (!jsonFile.exists() && settings.isDefault()) {
                logger.debug("Settings are default, skipping save");
                return;
            }
            if (!jsonCached.isEmpty() && settings.equals(jsonCached)) {
                logger.debug("Settings are equal, skipping save");
                return;
            }
            String json = settings.getLoadedOrNonDefaultSettings();
            try (FileWriter writer = new FileWriter(jsonFile)) {
                writer.write(json);
                jsonCached = json;
            } catch (IOException e) {
                logger.error("Failed to write [%s]".formatted(jsonFile), e);
            }
        }

        void reset() {
            jsonCached = "";
        }

        @NotNull File getJsonFile(String projectDir) {
            File file = new File(projectDir, PRETTIER_JSON);
            if (!file.exists()) {
                logger.debug("Optional file [%s] does not exist".formatted(file));
            }
            return file;
        }

        public boolean isSettingsFileChanged() {
            boolean isChanged = !isJsonEqual(jsonCached, readJson());
            if (isChanged) {
                logger.debug("Settings file is changed");
            }
            return isChanged;
        }
    }
}
