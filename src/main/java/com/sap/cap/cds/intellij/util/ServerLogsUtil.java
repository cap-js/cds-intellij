package com.sap.cap.cds.intellij.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Comparator.comparingLong;

public class ServerLogsUtil {

    // TODO filter for relevance for current project
    // TODO consider log file in project directory
    public static Optional<File> findLspServerLogFile() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File lspLogDir = new File(tempDir, "cdxlsp");

        if (!lspLogDir.exists() || !lspLogDir.isDirectory()) {
            return Optional.empty();
        }

        return findMostRecentLogFile(lspLogDir);
    }

    private static Optional<File> findMostRecentLogFile(@NotNull File dir) {
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return Optional.empty();
        }

        return Arrays.stream(files)
                .filter(File::isFile)
                .filter(file -> file.getName().endsWith(".log"))
                .max(comparingLong(File::lastModified));
    }
}
