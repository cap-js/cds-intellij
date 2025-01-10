package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.codeStyle.CodeStyleSettingsChangeEvent;
import com.intellij.psi.codeStyle.CodeStyleSettingsListener;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.intellij.openapi.project.ProjectUtil.guessProjectDir;
import static com.sap.cap.cds.intellij.util.Logger.logger;
import static java.nio.file.Files.readString;

@Service(Service.Level.PROJECT)
public final class CdsCodeStyleSettingsService {

    public static final String PRETTIER_JSON = ".cdsprettier.json";
    private final Project project;
    private final com.intellij.openapi.diagnostic.Logger logger;
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

    public boolean isSettingsFilePresent() {
        return prettierJsonManager.isJsonFilePresent();
    }

    public boolean isSettingsReallyChanged() {
        return prettierJsonManager.isSettingsReallyChanged();
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
        }
    }

    private CdsCodeStyleSettings getSettings() {
        return CodeStyle.getSettings(project).getCustomSettings(CdsCodeStyleSettings.class);
    }

    private final class CdsPrettierJsonManager {

        static final int JSON_INDENT = 2;

        File jsonFile;
        String jsonCached;

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
            if (json != null) {
                try {
                    settings.loadFrom(new JSONObject(json));
                } catch (JSONException e) {
                    logger.error("Failed to parse JSON '%s'".formatted(json), e);
                }
            }
        }

        private String readJson() {
            if (!isJsonFilePresent()) {
                return null;
            }
            try {
                return jsonCached = readString(jsonFile.toPath());
            } catch (IOException e) {
                logger.error("Failed to read [%s]".formatted(jsonFile), e);
            }
            return null;
        }

        void saveSettingsToFile(@NotNull CdsCodeStyleSettings settings) {
            if (jsonFile == null) {
                return;
            }
            String json = settings.getNonDefaultSettings().toString(JSON_INDENT);
            if (json.equals(jsonCached)) {
                return;
            }
            if (!jsonFile.getParentFile().exists()) {
                logger.debug("Directory [%s] does not exist".formatted(jsonFile.getParentFile()));
                return;
            }
            try (FileWriter writer = new FileWriter(jsonFile)) {
                writer.write(json);
                jsonCached = json;
            } catch (IOException e) {
                logger.error("Failed to write [%s]".formatted(jsonFile), e);
            }
        }

        void reset() {
            jsonCached = null;
        }

        @NotNull File getJsonFile(String projectDir) {
            File file = new File(projectDir, PRETTIER_JSON);
            if (!file.exists()) {
                logger.debug("Optional file [%s] does not exist".formatted(file));
            }
            return file;
        }

        public boolean isSettingsReallyChanged() {
            return jsonCached == null || !jsonCached.equals(readJson());
        }
    }
}
