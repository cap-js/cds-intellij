package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.openapi.vfs.VirtualFile;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageServer;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider;

import java.util.concurrent.CompletableFuture;



public class CdsLanguageServer extends OSProcessStreamConnectionProvider {

    public CdsLanguageServer() {
        GeneralCommandLine commandLine = new GeneralCommandLine("/Users/d027643/.volta/bin/node", "--inspect=6009",  "--enable-source-maps", "/Users/d027643/SAPDevelop/src/cap/lsp/lsp8/dist/main.js", "--stdio")
                .withEnvironment("CDS_LSP_TRACE_COMPONENTS", "*:verbose");
        super.setCommandLine(commandLine);
    }

    public Object getInitializationOptions(VirtualFile rootUri) {
        return CdsLanguageClient.getInitializationOptions();
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