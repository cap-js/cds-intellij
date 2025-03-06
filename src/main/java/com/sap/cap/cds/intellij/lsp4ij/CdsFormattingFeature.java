package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.psi.PsiFile;
import com.redhat.devtools.lsp4ij.client.features.LSPFormattingFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CdsFormattingFeature extends LSPFormattingFeature {

    @Override
    public boolean isFormatOnCloseBrace(@NotNull PsiFile file) {
        // Q: do we get this automatically when onTypeFormatting is enabled, or do we need to do something special?
        return super.isFormatOnCloseBrace(file);
    }

    @Override
    public @Nullable String getFormatOnCloseBraceCharacters(@NotNull PsiFile file) {
        // Q: '}' ?
        // Q: where to define "the language's default close characters" (that is mentioned in the docs)?
        return super.getFormatOnCloseBraceCharacters(file);
    }

    @Override
    public boolean isFormatOnStatementTerminator(@NotNull PsiFile file) {
        return super.isFormatOnStatementTerminator(file);
    }

    @Override
    public @Nullable String getFormatOnStatementTerminatorCharacters(@NotNull PsiFile file) {
        // Q: ';' ?
        return super.getFormatOnStatementTerminatorCharacters(file);
    }
}
