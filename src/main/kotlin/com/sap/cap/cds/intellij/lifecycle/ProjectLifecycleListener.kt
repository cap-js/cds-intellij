package com.sap.cap.cds.intellij.lifecycle

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsService

class ProjectLifecycleListener : ProjectActivity {
    override suspend fun execute(project: Project) {
        val service = project.getService(CdsCodeStyleSettingsService::class.java)
        if (service.isSettingsFilePresent) {
            service.updateProjectSettingsFromFile()
        } else {
            service.updateSettingsFile()
        }
    }
}