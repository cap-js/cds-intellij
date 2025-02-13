package com.sap.cap.cds.intellij.lsp4ij;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageServer;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider;

import java.util.concurrent.CompletableFuture;

public interface CdsCustomServerAPI  extends LanguageServer {

//    @JsonRequest("my/applications")
//    CompletableFuture<List<Application>> getApplications();

}
