package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.psi.PsiFile;
import com.redhat.devtools.lsp4ij.client.features.LSPFoldingRangeFeature;
import org.eclipse.lsp4j.FoldingRange;
import org.jetbrains.annotations.NotNull;

public class CdsFoldingRangeFeature extends LSPFoldingRangeFeature {

    @Override
    public boolean isCollapsedByDefault(@NotNull PsiFile file, @NotNull FoldingRange foldingRange) {
        // Idea: collapse usings - maybe client-side user option OR can we do this in server?
        return super.isCollapsedByDefault(file, foldingRange);
    }
}
