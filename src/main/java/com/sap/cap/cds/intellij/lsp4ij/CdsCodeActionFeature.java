package com.sap.cap.cds.intellij.lsp4ij;

import com.redhat.devtools.lsp4ij.client.features.LSPCodeActionFeature;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CdsCodeActionFeature extends LSPCodeActionFeature {

    @Override
    public @Nullable String getText(@NotNull Command command) {
        return super.getText(command);
    }

    @Override
    public @Nullable String getText(@NotNull CodeAction codeAction) {
        return super.getText(codeAction);
    }
}
