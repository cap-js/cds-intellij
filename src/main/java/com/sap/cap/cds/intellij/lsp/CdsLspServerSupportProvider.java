package com.sap.cap.cds.intellij.lsp;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.lsp.api.LspServer;
import com.intellij.platform.lsp.api.lsWidget.LspServerWidgetItem;
import com.sap.cap.cds.intellij.CdsFileType;
import com.sap.cap.cds.intellij.CdsIcons;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import com.sap.cap.cds.intellij.textmate.CdsTextMateBundleManager;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CdsLspServerSupportProvider implements com.intellij.platform.lsp.api.LspServerSupportProvider {

    CdsLspServerSupportProvider() {
        CdsTextMateBundleManager.INSTANCE.registerBundle();
    }

    @Override
    public void fileOpened(@NotNull Project project, @NotNull VirtualFile virtualFile, @NotNull LspServerStarter lspServerStarter) {
        if (!CdsFileType.EXTENSION.equals(virtualFile.getExtension())) {
            return;
        }
        lspServerStarter.ensureServerStarted(new CdsLspServerDescriptor(project, CdsLanguage.LABEL));
    }

    @Override
    public LspServerWidgetItem createLspServerWidgetItem(@NotNull LspServer lspServer, @Nullable VirtualFile currentFile) {
        Intrinsics.checkNotNullParameter(lspServer, "lspServer");
        return new LspServerWidgetItem(lspServer, currentFile, CdsIcons.SERVER, null);
    }
}
