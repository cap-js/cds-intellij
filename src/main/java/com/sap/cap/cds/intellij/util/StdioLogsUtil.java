package com.sap.cap.cds.intellij.util;

import com.intellij.openapi.application.PathManager;
import com.sap.cap.cds.intellij.CdsPlugin;

import java.io.File;
import java.util.Optional;

public class StdioLogsUtil {

    public static Optional<File> findStdioLogFile() {
        String pluginsPath = PathManager.getPluginsPath();
        File stdioLogFile = new File(pluginsPath, "%s/lib/cds-lsp/stdio.json".formatted(CdsPlugin.LABEL));
        if (stdioLogFile.exists()) {
            return Optional.of(stdioLogFile);
        }
        return Optional.empty();
    }

}
