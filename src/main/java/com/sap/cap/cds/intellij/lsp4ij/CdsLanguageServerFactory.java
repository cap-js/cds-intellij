package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.LanguageServerFactory;
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl;
import com.redhat.devtools.lsp4ij.client.features.LSPClientFeatures;
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider;
import com.sap.cap.cds.intellij.lspServer.CdsLspServerDescriptor;
import org.eclipse.lsp4j.services.LanguageServer;
import org.jetbrains.annotations.NotNull;

public class CdsLanguageServerFactory implements LanguageServerFactory { // TODO: check ExtensionLanguageServerDefinition.getIcon etc.

    @Override
    public @NotNull StreamConnectionProvider createConnectionProvider(@NotNull Project project) {
        return new CdsLanguageServer(project, CdsLspServerDescriptor::getServerCommandLine);
    }

    @Override // If you need to provide client specific features
    public @NotNull LanguageClientImpl createLanguageClient(@NotNull Project project) {
        return new CdsLanguageClient(project);
    }

    @Override // If you need to expose a custom server API
    public @NotNull Class<? extends LanguageServer> getServerInterface() {
        return CdsCustomServerAPI.class;
    }

    @Override
    public @NotNull LSPClientFeatures createClientFeatures() {
        var features = new CdsLspClientFeatures()
//                .setServerInstaller() // TODO: install latest cds-lsp on-the-fly, maybe blue/green like for annotation modeler
//                .setEditorFeatures() // TODO: API already documented but not yet released
                .setWorkspaceSymbolFeature(new CdsWorkspaceSymbolFeature())
                .setSemanticTokensFeature(new CdsSemanticTokensFeature())
                .setProgressFeature(new CdsProgressFeature())
                .setHoverFeature(new CdsHoverFeature())
                .setFormattingFeature(new CdsFormattingFeature())
                .setFoldingRangeFeature(new CdsFoldingRangeFeature())
                .setDiagnosticFeature(new CdsDiagnosticFeature())
                .setCodeActionFeature(new CdsCodeActionFeature());
        return features;
    }
}