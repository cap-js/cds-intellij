package com.sap.cap.cds.intellij.util;

import com.sap.cap.cds.intellij.Language;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.intellij.util.PathUtil.getJarPathForClass;

public class PathUtil {

    public static String resolve(String relativePath) {
        Path basePath = Paths.get(getJarPathForClass(Language.class));
        return basePath.getParent().resolve(relativePath).toString();
    }
}
