package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.psi.PsiFile;
import com.redhat.devtools.lsp4ij.client.features.LSPHoverFeature;
import org.eclipse.lsp4j.MarkupContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CdsHoverFeature extends LSPHoverFeature {

    @Override
    public @Nullable String getContent(@NotNull MarkupContent content, @NotNull PsiFile file) {
        // TODO: if we cannot hook the analyze-deps command, we could remove this from the markup for the time being
        return super.getContent(content, file);
    }
}
