package com.sap.cap.cds.intellij.lsp4ij;

import com.redhat.devtools.lsp4ij.client.features.LSPWorkspaceSymbolFeature;

public class CdsWorkspaceSymbolFeature extends LSPWorkspaceSymbolFeature {


    @Override
    public boolean supportsGotoClass() {
        // Q: default is false. Is there a performance impact if we enable it for our CDS LSP? A: likely yes until we keep an up-to-date global index
        return super.supportsGotoClass();
    }
}
