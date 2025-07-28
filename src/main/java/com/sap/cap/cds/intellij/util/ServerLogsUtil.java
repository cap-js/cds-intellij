package com.sap.cap.cds.intellij.util;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Comparator.comparingLong;

public class ServerLogsUtil {

    /**
     * Finds the newest LSP server log file in the project directory or in the temporary directory.
     * @param project the current project
     * @return an Optional containing the log file if found, otherwise an empty Optional
     */
    public static Optional<File> findLspServerLogFile(Project project) {
        Optional<File> localFile = ServerLogsUtil.findLogFileInProjectDir(project);
        if (localFile.isPresent()) {
            return localFile;
        }

        return findLogFileInTempDir();
    }

    private static Optional<File> findLogFileInProjectDir(Project project) {
        if (project == null || project.getBasePath() == null) {
            return Optional.empty();
        }
        File projectDir = new File(project.getBasePath());

        File logDirBase = new File(projectDir, ".cds-lsp");
        File logDir = new File(logDirBase, "logs");
        if (!logDir.exists() || !logDir.isDirectory()) {
            return Optional.empty();
        }

        return findRelevantLogFile(logDir);
    }

    private static Optional<File> findLogFileInTempDir() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File logDir = new File(tempDir, "cdxlsp");

        if (!logDir.exists() || !logDir.isDirectory()) {
            return Optional.empty();
        }

        return findRelevantLogFile(logDir);
    }

    private static Optional<File> findRelevantLogFile(@NotNull File dir) {
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return Optional.empty();
        }

        return Arrays.stream(files)
                .filter(File::isFile)
                .filter(file -> file.getName().matches("^.*(?<!_context)\\.log$"))
                .max(comparingLong(File::lastModified));
    }
}
