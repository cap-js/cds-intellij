package com.sap.cap.cds.lsp;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.lsp.api.LspServer;
import com.intellij.platform.lsp.api.lsWidget.LspServerWidgetItem;
import com.sap.cap.cds.FileType;
import com.sap.cap.cds.Icons;
import com.sap.cap.cds.Language;
import com.sap.cap.cds.textmate.BundleManager;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerSupportProvider implements com.intellij.platform.lsp.api.LspServerSupportProvider {

    ServerSupportProvider() {
        BundleManager.INSTANCE.registerBundle();
    }

    @Override
    public void fileOpened(@NotNull Project project, @NotNull VirtualFile virtualFile, @NotNull LspServerStarter lspServerStarter) {
        if (!FileType.EXTENSION.equals(virtualFile.getExtension())) {
            return;
        }
        lspServerStarter.ensureServerStarted(new ServerDescriptor(project, Language.LABEL));
    }

    @Override
    public LspServerWidgetItem createLspServerWidgetItem(@NotNull LspServer lspServer, @Nullable VirtualFile currentFile) {
        Intrinsics.checkNotNullParameter(lspServer, "lspServer");
        return new LspServerWidgetItem(lspServer, currentFile, Icons.SERVER, null);
    }
}
