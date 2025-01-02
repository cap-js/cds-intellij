package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.codeStyle.CodeStyleSettingsChangeEvent;
import com.intellij.psi.codeStyle.CodeStyleSettingsListener;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.intellij.openapi.project.ProjectUtil.guessProjectDir;
import static com.sap.cap.cds.intellij.util.Logger.logger;
import static java.nio.file.Files.readString;

// TODO test .cdsprettier.json reading/updating on a project level

@Service(Service.Level.PROJECT)
public final class CdsCodeStyleSettingsService {

    public static final String PRETTIER_JSON = ".cdsprettier.json";
    private final Project project;
    private final com.intellij.openapi.diagnostic.Logger logger;
    private final CdsPrettierJsonManager prettierJsonManager;

    public CdsCodeStyleSettingsService(Project project) {
        this.project = project;
        this.logger = logger(project.getName()).CODE_STYLE();
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

    public CdsCodeStyleSettings getSettings() {
        return CodeStyle.getSettings(project).getCustomSettings(CdsCodeStyleSettings.class);
    }

    public void updateSettingsFile() {
        prettierJsonManager.saveSettingsToFile(getSettings());
    }

    public void updateProjectSettingsFromFile() {
        if (CodeStyle.usesOwnSettings(project) && isSettingsFilePresent()) {
            prettierJsonManager.loadSettingsFromFile(getSettings());
        }
    }

    private final class CdsPrettierJsonManager {

        static final int JSON_INDENT = 2;

        File jsonFile;
        String jsonWritten;

        CdsPrettierJsonManager() {
            // assuming no changes to project directory
            VirtualFile projectDir = guessProjectDir(project);
            if (projectDir == null) {
                // TODO handle IDE settings
            } else {
                jsonFile = getJsonFile(projectDir.getPath());
            }
        }

        boolean isJsonFilePresent() {
            return jsonFile.exists();
        }

        void loadSettingsFromFile(@NotNull CdsCodeStyleSettings settings) {
            JSONObject json = null;
            try {
                json = new JSONObject(readString(jsonFile.toPath()));
            } catch (IOException e) {
                logger.error("Failed to read [%s]".formatted(jsonFile), e);
            }
            settings.loadFrom(json);
        }

        void saveSettingsToFile(@NotNull CdsCodeStyleSettings settings) {
            String json = settings.getNonDefaultSettings().toString(JSON_INDENT);
            if (!json.equals(jsonWritten)) {
                try (FileWriter writer = new FileWriter(jsonFile)) {
                    writer.write(json);
                    jsonWritten = json;
                } catch (IOException e) {
                    logger.error("Failed to write [%s]".formatted(jsonFile), e);
                }
            }
        }

        @NotNull File getJsonFile(String projectDir) {
            File file = new File(projectDir, PRETTIER_JSON);
            if (!file.exists()) {
                logger.debug("Optional file [%s] does not exist".formatted(file));
            }
            return file;
        }

    }
}
