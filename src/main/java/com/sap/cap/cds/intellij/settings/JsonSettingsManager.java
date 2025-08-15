package com.sap.cap.cds.intellij.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.sap.cap.cds.intellij.util.Logger;
import com.sap.cap.cds.intellij.util.LoggerScope;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.intellij.openapi.project.ProjectUtil.guessProjectDir;
import static com.sap.cap.cds.intellij.util.JsonUtil.isJsonEqual;
import static com.sap.cap.cds.intellij.util.Logger.logger;
import static java.nio.file.Files.readString;

public abstract class JsonSettingsManager<T> {

    protected final Project project;
    protected final Logger logger;
    protected final File jsonFile;
    protected String jsonCached = "";

    protected JsonSettingsManager(Project project, String fileName, LoggerScope loggerScope) {
        this.project = project;
        this.logger = logger(project, loggerScope);
        this.jsonFile = resolveJsonFile(fileName);
        reset();
    }

    private File resolveJsonFile(String fileName) {
        VirtualFile guessed = guessProjectDir(project);
        String projectDir = guessed != null ? guessed.getPath() : project.getBasePath();
        logger.debug("Project directory: %s".formatted(projectDir));

        if (projectDir != null) {
            File file = new File(projectDir, fileName);
            logger.debug("Settings file: %s".formatted(file));
            if (!file.exists()) {
                logger.debug("Optional file [%s] does not exist".formatted(file));
            }
            return file;
        }
        return null;
    }

    public boolean isJsonFilePresent() {
        boolean present = jsonFile != null && jsonFile.exists();
        logger.debug("Settings file present: %s".formatted(present));
        return present;
    }

    public boolean isSettingsFileChanged() {
        boolean isChanged = !isJsonEqual(jsonCached, readJson());
        if (isChanged) {
            logger.debug("Settings file is changed");
        }
        return isChanged;
    }

    @NotNull
    public String readJson() {
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

    protected void writeJson(String json) {
        logger.debug("Saving settings to file");
        if (jsonFile == null) {
            logger.debug("File is null");
            return;
        }
        if (!jsonFile.getParentFile().exists()) {
            logger.debug("Creating directory [%s]".formatted(jsonFile.getParentFile()));
            if (!jsonFile.getParentFile().mkdirs()) {
                logger.error("Failed to create directory [%s]".formatted(jsonFile.getParentFile()));
                return;
            }
        }
        if (!jsonCached.isEmpty() && isJsonEqual(jsonCached, json)) {
            logger.debug("Settings are equal, skipping save");
            return;
        }

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
            jsonCached = json;
            logger.debug("Saved settings: %s".formatted(json));
        } catch (IOException e) {
            logger.error("Failed to write [%s]".formatted(jsonFile), e);
        }
    }

    public void reset() {
        jsonCached = "";
    }

    public abstract void loadSettingsFromFile(T settings);
    public abstract void saveSettingsToFile(T settings);
}
