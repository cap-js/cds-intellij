package com.sap.cap.cds.intellij.lsp4ij;
import com.intellij.openapi.vfs.VirtualFile;
import com.sap.cap.cds.intellij.lsp.CdsLspServerDescriptor;
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider;



public class CdsLanguageServer extends OSProcessStreamConnectionProvider {

    public CdsLanguageServer() {
        var cmd = CdsLspServerDescriptor.getServerCommandLine(CdsLspServerDescriptor.CommandLineKind.SERVER_DEBUG);//.withCharset(UTF_8);
        super.setCommandLine(cmd);
    }

    public Object getInitializationOptions(VirtualFile rootUri) {
        // TODO: in LSP accept cds top-level node but also accept current non-cds second-level nodes. Then use cds here and also adapt vscode to send cds.
        //  Idea: LSP pulls settings. This allows to pull non-cds settings if needed
        return CdsLanguageClient.getInitializationOptions().get("cds");


        // TODO: we want to keep our server alive. Default is to shut it down when the last CDS file is closed.
//        Project project = ...
//        CompletableFuture<Lease<LanguageServerItem>> serverLease =
//                LanguageServerManager.getInstance(project).getLanguageServer("myLanguageServerId")
//                        .thenApply(item -> item.keepAlive());
        // But we still want to be able to restart it via command
    }

//    @Override
//    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
//        LOGGER.info("Initializing Qute server " + getVersion() + " with " + System.getProperty("java.home"));
//
//        this.parentProcessId = params.getProcessId();
//
//        ExtendedClientCapabilities extendedClientCapabilities = InitializationOptionsExtendedClientCapabilities
//                .getExtendedClientCapabilities(params);
//        capabilityManager.setClientCapabilities(params.getCapabilities(), extendedClientCapabilities);
//        updateSettings(InitializationOptionsSettings.getSettings(params));
//
//        textDocumentService.updateClientCapabilities(params.getCapabilities(), extendedClientCapabilities);
//        ServerCapabilities serverCapabilities = ServerCapabilitiesInitializer
//                .getNonDynamicServerCapabilities(capabilityManager.getClientCapabilities());
//
//        projectRegistry.setDidChangeWatchedFilesSupported(
//                capabilityManager.getClientCapabilities().isDidChangeWatchedFilesRegistered());
//
//        InitializeResult initializeResult = new InitializeResult(serverCapabilities);
//        return CompletableFuture.completedFuture(initializeResult);
//    }

}