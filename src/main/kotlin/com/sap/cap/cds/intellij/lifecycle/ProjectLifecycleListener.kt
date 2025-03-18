package com.sap.cap.cds.intellij.lifecycle

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsService

// Q: do we want to keep two languages, java and kotlin? Or migrate this to Java, or all other to kotlin?
class ProjectLifecycleListener : ProjectActivity {
    override suspend fun execute(project: Project) {
        val service = project.getService(CdsCodeStyleSettingsService::class.java)
        if (service.isSettingsFilePresent) {
            service.updateProjectSettingsFromFile()
        }
    }
}