package com.sap.cap.cds.intellij.util;

import com.intellij.openapi.project.Project;

public record CacheKey(Project project, LoggerScope scope) {}
