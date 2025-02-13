package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.LanguageServerWrapper;
import com.redhat.devtools.lsp4ij.server.definition.LanguageServerDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public class CdsLanguageServerWrapper extends LanguageServerWrapper {
    public CdsLanguageServerWrapper(@NotNull Project project, @NotNull LanguageServerDefinition serverDefinition) {
        super(project, serverDefinition);
//        this.addOptions();
    }

    public CdsLanguageServerWrapper(@NotNull Project project, @NotNull LanguageServerDefinition serverDefinition, @Nullable URI initialPath) {
        super(project, serverDefinition, initialPath);
//        this.addOptions();
    }

//    private addOptions() {
//        this.initParams.setInitializationOptions();
//    }
}
