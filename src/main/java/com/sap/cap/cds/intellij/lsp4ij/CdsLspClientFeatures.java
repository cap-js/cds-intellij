package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.redhat.devtools.lsp4ij.ServerStatus;
import com.redhat.devtools.lsp4ij.client.features.LSPClientFeatures;
import org.eclipse.lsp4j.InitializeParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public class CdsLspClientFeatures extends LSPClientFeatures {

    @Override
    public void initializeParams(@NotNull InitializeParams initializeParams) {
        // Idea:  modify them before `onInitialize` is called
        super.initializeParams(initializeParams);


        // Idea:
//        this.getLanguageServer().getTextDocumentService().hover().handleAsync()  Q: can we hook this to somehow handle the command:cds.analyzeDependencies URI?


    }

    @Override
    public void handleServerStatusChanged(@NotNull ServerStatus serverStatus) {
        // Idea: log
        super.handleServerStatusChanged(serverStatus);
    }

    @Override
    public boolean keepServerAlive() {
        // Keep server alive even if all files associated with the language server are closed
        return true;
    }

    @Override
    public boolean isEnabled(@NotNull VirtualFile file) {
        // Idea: enable/disable CDS LSP for the file e.g. CSN JSON files vs. other JSON files - But actually the server does this already
        return super.isEnabled(file);
    }

    @Override
    public @Nullable URI getFileUri(@NotNull VirtualFile file) {
        // Idea: canonicalize URI like VSCode's uriConverters
        URI fileUri = super.getFileUri(file);
        return fileUri;
    }

    @Override
    public @Nullable VirtualFile findFileByUri(@NotNull String fileUri) {
        // Idea: canonicalize URI like VSCode's uriConverters
        return super.findFileByUri(fileUri);
    }

    @Override
    public boolean isCaseSensitive(@NotNull PsiFile file) {
        // default: false
        // Q: return true for CDS files?
        return super.isCaseSensitive(file);
    }

    // Idea:
    /*
    String getLineCommentPrefix(PsiFile file)	Returns the language grammar line comment prefix for the file.
String getBlockCommentPrefix(PsiFile file)	Returns the language grammar block comment prefix for the file.
String getBlockCommentSuffix(PsiFile file)	Returns the language grammar block comment suffix for the file.
String getStatementTerminatorCharacters(PsiFile file)	Returns the language grammar statement terminator characters for the file.
     */
    
}
