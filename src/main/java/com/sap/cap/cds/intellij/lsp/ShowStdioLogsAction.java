package com.sap.cap.cds.intellij.lsp;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.sap.cap.cds.intellij.lspServer.UserError;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;

import static com.sap.cap.cds.intellij.util.EditorUtil.openFileInEditor;
import static com.sap.cap.cds.intellij.util.StdioLogsUtil.findStdioLogFile;

public class ShowStdioLogsAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        Optional<File> logFile = findStdioLogFile();
        if (logFile.isEmpty()) {
            UserError.show("Cannot find stdio log file.");
            return;
        }

        openFileInEditor(project, logFile.get());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

}
