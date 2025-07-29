package com.sap.cap.cds.intellij.lsp;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sap.cap.cds.intellij.lspServer.UserError;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.Optional;

import static com.sap.cap.cds.intellij.util.StdioLogsUtil.findStdioLogFile;
import static com.sap.cap.cds.intellij.util.SupportUtil.isDebugCdsLsp;

public class CopyStdioLogPathAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Optional<File> logFile = findStdioLogFile();
        if (logFile.isEmpty()) {
            UserError.show("Stdio log file not found.");
            return;
        }

        StringSelection stringSelection = new StringSelection(logFile.get().getAbsolutePath());
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(isDebugCdsLsp());
    }

}
