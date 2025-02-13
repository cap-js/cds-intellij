package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.LanguageServerFactory;
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl;
import com.redhat.devtools.lsp4ij.client.features.LSPClientFeatures;
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider;
import org.jetbrains.annotations.NotNull;
import org.eclipse.lsp4j.services.LanguageServer;

import com.sap.cap.cds.intellij.lsp4ij.*;

public class CdsLanguageServerFactory implements LanguageServerFactory {

    @Override
    public @NotNull StreamConnectionProvider createConnectionProvider(@NotNull Project project) {
        return new CdsLanguageServer();
    }

    @Override // If you need to provide client specific features
    public @NotNull LanguageClientImpl createLanguageClient(@NotNull Project project) {
        return new CdsLanguageClient(project);
    }

    @Override // If you need to expose a custom server API
    public @NotNull Class<? extends LanguageServer> getServerInterface() {
        return CdsCustomServerAPI.class;
    }

//    @Override
//    public @NotNull LSPClientFeatures createClientFeatures() {
//        var features = new LSPClientFeatures();
////        features.getServerWrapper().restart();
////        features.getServerWrapper().getLanguageServer().initialize();
//
//        features.getServerWrapper().
//        features.setServerWrapper(new CdsLanguageServerWrapper());
//        return features;
//    }

}