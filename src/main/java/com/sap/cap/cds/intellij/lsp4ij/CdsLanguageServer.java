package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.openapi.vfs.VirtualFile;
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider;
import com.sap.cap.cds.intellij.lspServer.CdsLspServerDescriptor;



public class CdsLanguageServer extends OSProcessStreamConnectionProvider {

    public CdsLanguageServer() {
        super.setCommandLine(CdsLspServerDescriptor.getServerCommandLine());
    }

    public Object getInitializationOptions(VirtualFile rootUri) {
        // TODO: in LSP accept cds top-level node but also accept current non-cds second-level nodes. Then use cds here and also adapt vscode to send cds.
        //  Idea: LSP pulls settings. This allows to pull non-cds settings if needed
        return CdsLanguageClient.getInitializationOptions().get("cds");
    }
}