package com.sap.cap.cds.intellij.lsp4ij;

import com.redhat.devtools.lsp4ij.client.features.LSPWorkspaceSymbolFeature;

public class CdsWorkspaceSymbolFeature extends LSPWorkspaceSymbolFeature {


    @Override
    public boolean supportsGotoClass() {
        return true;
    }
}
