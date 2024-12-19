package com.sap.cap.cds.intellij.util;

import com.sap.cap.cds.intellij.lang.CdsLanguage;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.intellij.util.PathUtil.getJarPathForClass;

public class PathUtil {

    public static String resolve(String relativePath) {
        Path basePath = Paths.get(getJarPathForClass(CdsLanguage.class));
        return basePath.getParent().resolve(relativePath).toString();
    }
}
