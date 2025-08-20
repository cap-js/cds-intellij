package com.sap.cap.cds.intellij.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    public static Path createTempDir(String prefix) throws IOException {
        return Files.createTempDirectory(prefix);
    }

}