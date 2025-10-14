package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import com.redhat.devtools.lsp4ij.server.CannotStartProcessException;
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider;
import com.sap.cap.cds.intellij.usersettings.CdsUserSettingsService;
import com.sap.cap.cds.intellij.util.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public class CdsLanguageServer extends OSProcessStreamConnectionProvider {

    public static final String ID = "cds";
    public static final String LABEL = "Language Server";
    @NotNull
    private final Project project;
    private final Supplier<GeneralCommandLine> commandLineSupplier;

    public CdsLanguageServer(@NotNull Project project, Supplier<GeneralCommandLine> commandLineSupplier) {
        this.project = project;
        this.commandLineSupplier = commandLineSupplier;
        super.setCommandLine(commandLineSupplier.get());
    }

    public static void restart(@NotNull Project project) {
        Logger.SERVER.debug("Restarting CDS Language Server for project: " + project.getName());
        LanguageServerManager lspManager = LanguageServerManager.getInstance(project);
        lspManager.stop(ID);
        lspManager.start(ID);
    }

    public Object getInitializationOptions(VirtualFile rootUri) {
        // TODO: in LSP accept cds top-level node but also accept current non-cds second-level nodes. Then use cds here and also adapt vscode to send cds.
        //  Idea: LSP pulls settings. This allows to pull non-cds settings if needed
        // TODO cds.trace.level (off) -> verbose (set env and restart)
        // TODO implement ActiveEditorChanged request

        return project.getService(CdsUserSettingsService.class)
                .getSettingsStructured();
    }
}