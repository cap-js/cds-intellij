package com.sap.cap.cds.intellij.lifecycle

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsService
import com.sap.cap.cds.intellij.util.Logger.logger
import com.sap.cap.cds.intellij.util.LoggerScope.CODE_STYLE

// Impl in Kotlin is required due to suspension, see https://plugins.jetbrains.com/docs/intellij/plugin-components.html#project-open
class ProjectLifecycleListener : ProjectActivity {
    override suspend fun execute(project: Project) {
        val service = project.getService(CdsCodeStyleSettingsService::class.java)
        if (service.isSettingsFilePresent) {
            logger(project, CODE_STYLE).debug("Project with .cdsprettier.json opened")
            service.updateProjectSettingsFromFile()
        }
    }
}