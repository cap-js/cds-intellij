package com.sap.cap.cds.intellij.lsp;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;

import static com.sap.cap.cds.intellij.util.ServerLogsUtil.findLspServerLogFile;

public class ShowLspServerLogsAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        Optional<File> logFile = findLspServerLogFile(project);
        if (logFile.isEmpty()) {
            UserError.show("Cannot find LSP server log files.");
            return;
        }
        openFileInEditor(project, logFile.get());
    }

    private void openFileInEditor(@NotNull Project project, @NotNull File file) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
        if (virtualFile != null) {
            FileEditorManager.getInstance(project).openFile(virtualFile, true);
        } else {
            UserError.show("Cannot open LSP server log file: " + file.getAbsolutePath());
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }
}
