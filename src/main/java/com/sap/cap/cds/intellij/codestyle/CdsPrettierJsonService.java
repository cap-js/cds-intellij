package com.sap.cap.cds.intellij.codestyle;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.sap.cap.cds.intellij.util.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.intellij.openapi.project.ProjectUtil.guessProjectDir;
import static java.nio.file.Files.readString;

@Service(Service.Level.PROJECT)
public final class CdsPrettierJsonService {

    public static final String PRETTIER_JSON = ".cdsprettier.json";
    private static final int JSON_INDENT = 2;

    private File jsonFile;
    private String jsonWritten;

    public CdsPrettierJsonService(Project project) {
        // assuming no changes to project directory
        jsonFile = getJsonFile(guessProjectDir(project).getPath());
    }

    public void loadSettingsFromFile(@NotNull CdsCodeStyleSettings settings) {
        if (!jsonFile.exists()) {
            return;
        }
        try {
            JSONObject json = new JSONObject(readString(jsonFile.toPath()));
            settings.loadFrom(json);
        } catch (IOException e) {
            Logger.CODE_STYLE.error("Failed to read [%s]".formatted(jsonFile), e);
        }
    }

    public void saveSettingsToFile(@NotNull CdsCodeStyleSettings settings) {
        String json = settings.getNonDefaultSettings().toString(JSON_INDENT);
        if (!json.equals(jsonWritten)) {
            try (FileWriter writer = new FileWriter(jsonFile)) {
                writer.write(json);
                jsonWritten = json;
            } catch (IOException e) {
                Logger.CODE_STYLE.error("Failed to write [%s]".formatted(jsonFile), e);
            }
        }
    }

    private @NotNull File getJsonFile(String projectDir) {
        File file = new File(projectDir, PRETTIER_JSON);
        if (!file.exists()) {
            Logger.CODE_STYLE.debug("Optional file [%s] does not exist".formatted(file));
        }
        return file;
    }

}
