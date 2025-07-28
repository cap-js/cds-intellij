package com.sap.cap.cds.intellij.lspServer;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.Optional;

import static com.sap.cap.cds.intellij.util.ServerLogsUtil.findLspServerLogFile;

public class CopyLspServerLogPathAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Optional<File> logFile = findLspServerLogFile(e.getProject());
        if (logFile.isEmpty()) {
            UserError.show("No LSP server log files found.");
            return;
        }
        StringSelection stringSelection = new StringSelection(logFile.get().getAbsolutePath());
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean isProjectOpen = e.getProject() != null;
        e.getPresentation().setEnabledAndVisible(isProjectOpen);
    }

}
