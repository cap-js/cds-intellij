package com.sap.cap.cds.intellij.usersettings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.sap.cap.cds.intellij.settings.JsonSettingsFileListener;
import com.sap.cap.cds.intellij.util.LoggerScope;

import java.util.function.Predicate;

import static com.sap.cap.cds.intellij.util.LoggerScope.USER_SETTINGS;

public class CdsUserSettingsListener extends JsonSettingsFileListener {

    @Override
    protected Predicate<VirtualFile> fileFilter() {
        return file -> file.getPath().endsWith(".settings.json") && file.getPath().contains(".cds-lsp");
    }

    @Override
    protected void handleFileChange(Project project) {
        CdsUserSettingsService service = project.getService(CdsUserSettingsService.class);
        if (service.isSettingsFileChanged()) {
            service.updateProjectSettingsFromFile();
        }
    }

    @Override
    protected LoggerScope getLoggerScope() {
        return USER_SETTINGS;
    }

    @Override
    protected String getDebugMessage() {
        return ".cds-lsp/.settings.json changed";
    }
}
