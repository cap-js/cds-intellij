package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsChangeEvent;
import com.intellij.psi.codeStyle.CodeStyleSettingsListener;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.intellij.openapi.project.ProjectUtil.guessProjectDir;
import static com.sap.cap.cds.intellij.util.JsonUtil.isJsonEqual;
import static com.sap.cap.cds.intellij.util.Logger.logger;
import static java.nio.file.Files.readString;

@Service(Service.Level.PROJECT)
public final class CdsCodeStyleSettingsService {

    public static final String PRETTIER_JSON = ".cdsprettier.json";
    private final Project project;
    private final Logger logger;
    private final CdsPrettierJsonManager prettierJsonManager;

    public CdsCodeStyleSettingsService(Project project) {
        this.project = project;
        this.logger = logger(project).CODE_STYLE();
        prettierJsonManager = new CdsPrettierJsonManager();
        CodeStyleSettingsManager.getInstance(project).subscribe(new CodeStyleSettingsListener() {
            @Override
            public void codeStyleSettingsChanged(@NotNull CodeStyleSettingsChangeEvent event) {
                updateSettingsFile();
            }
        });
    }

    private static @NotNull CdsCodeStyleSettings getIdeSettings() {
        return CodeStyle.getDefaultSettings().getCustomSettings(CdsCodeStyleSettings.class);
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
                prettierJsonManager.loadSettingsFromFile(getSettings());
            } else {
                prettierJsonManager.reset();
                CodeStyle.setMainProjectSettings(project, CodeStyleSettingsManager.getInstance(project).createSettings());
            }
        } else if (isSettingsFilePresent()) { // deletion of .cdsprettier.json has no effect
            String prettierJson = prettierJsonManager.readJson();
            if (!prettierJson.isEmpty() && !getIdeSettings().equals(prettierJson)) {
                CodeStyleSettings projectSettings = CodeStyleSettingsManager.getInstance().createSettings();
                projectSettings.getCustomSettings(CdsCodeStyleSettings.class).loadFrom(prettierJson);
                CodeStyle.setMainProjectSettings(project, projectSettings);
            }
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
            if (projectDir != null) {
                jsonFile = getJsonFile(projectDir);
            }
            reset();
        }

        boolean isJsonFilePresent() {
            return jsonFile != null && jsonFile.exists();
        }

        void loadSettingsFromFile(@NotNull CdsCodeStyleSettings settings) {
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
            if (!isJsonFilePresent()) {
                return "";
            }
            try {
                return jsonCached = readString(jsonFile.toPath());
            } catch (IOException e) {
                logger.error("Failed to read [%s]".formatted(jsonFile), e);
            }
            return "";
        }

        void saveSettingsToFile(@NotNull CdsCodeStyleSettings settings) {
            if (jsonFile == null) {
                return;
            }
            if (!jsonFile.getParentFile().exists()) {
                logger.debug("Directory [%s] does not exist".formatted(jsonFile.getParentFile()));
                return;
            }
            if (!jsonFile.exists() && settings.isDefault()) {
                return;
            }
            if (!jsonCached.isEmpty() && settings.equals(jsonCached)) {
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
            return !isJsonEqual(jsonCached, readJson());
        }
    }
}
