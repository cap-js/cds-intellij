package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.LanguageServerFactory;
import com.redhat.devtools.lsp4ij.LanguageServerWrapper;
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl;
import com.redhat.devtools.lsp4ij.client.features.LSPClientFeatures;
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider;
import org.eclipse.lsp4j.InitializeParams;
import org.jetbrains.annotations.NotNull;
import org.eclipse.lsp4j.services.LanguageServer;

import com.sap.cap.cds.intellij.lsp4ij.*;

public class CdsLanguageServerFactory implements LanguageServerFactory { // TODO: check ExtensionLanguageServerDefinition.getIcon etc.

    @Override
    public @NotNull StreamConnectionProvider createConnectionProvider(@NotNull Project project) {
        return new CdsLanguageServer();
    }

    @Override // If you need to provide client specific features
    public @NotNull LanguageClientImpl createLanguageClient(@NotNull Project project) {
        var client = new CdsLanguageClient(project);
//        var features = client.getClientFeatures(); // TODO: uses wrapper, available after connect
//        var wrapper = features.getServerWrapper();
//        wrapper.getClientFeatures().getCodeLensFeature()
//        wrapper.getServerCapabilities().getExecuteCommandProvider()
//        wrapper.getClass().getDeclaredMethod() // TODO try reflection
//        features.setServerWrapper(new CdsLanguageServerWrapper(project, wrapper.getServerDefinition())); // TODO: wrapper first available after connect. Q: can we hook this?
//        features.initializeParams(new InitializeParams());
        return client;
    }

    @Override // If you need to expose a custom server API
    public @NotNull Class<? extends LanguageServer> getServerInterface() {
        return CdsCustomServerAPI.class;
    }

    @Override
    public @NotNull LSPClientFeatures createClientFeatures() {
        var features = new CdsLspClientFeatures()
//                .setServerInstaller() // TODO
//                .setEditorFeatures() // TODO
                .setWorkspaceSymbolFeature(new CdsWorkspaceSymbolFeature())
                .setSemanticTokensFeature(new CdsSemanticTokensFeature())
                .setProgressFeature(new CdsProgressFeature())
                .setHoverFeature(new CdsHoverFeature())
                .setFormattingFeature(new CdsFormattingFeature())
                .setFoldingRangeFeature(new CdsFoldingRangeFeature())
                .setDiagnosticFeature(new CdsDiagnosticFeature())
                .setCodeActionFeature(new CdsCodeActionFeature());
        // Q: where is EditorFeature? Docs mentions it, but not in the API
        return features;
    }


}