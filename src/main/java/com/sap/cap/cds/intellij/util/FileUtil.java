package com.sap.cap.cds.intellij.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.sap.cap.cds.intellij.util.StringUtil.randomString;
import static java.nio.file.Files.createDirectories;

public class FileUtil {

    public static Path createTempDir(String prefix) throws IOException {
        // TODO: replace with Files.createTempDirectory()

        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        Path subDir = tempDir.resolve(prefix + randomString(8));
        createDirectories(subDir);
        return subDir;
    }

}