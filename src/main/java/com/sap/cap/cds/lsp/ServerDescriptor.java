package com.sap.cap.cds.lsp;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor;
import com.intellij.platform.lsp.api.customization.LspFormattingSupport;
import com.sap.cap.cds.FileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.intellij.util.PathUtil.getJarPathForClass;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ServerDescriptor extends ProjectWideLspServerDescriptor {

    private static final String RELATIVE_SERVER_PATH = "cds-lsp/node_modules/@sap/cds-lsp/dist/main.js";
    private static final String RELATIVE_MITM_PATH = "cds-lsp/mitm.js";
    private static final String RELATIVE_LOG_PATH = "cds-lsp/stdio.log";

    public ServerDescriptor(@NotNull Project project, @NotNull String presentableName) {
        super(project, presentableName);
    }

    @NotNull
    @Override
    public GeneralCommandLine createCommandLine() throws ExecutionException {
        return (isDebugCdsLsp() ? getDebugCommandLine() : getCommandLine())
                .withEnvironment("CDS_LSP_TRACE_COMPONENTS", "*:verbose")
                // TODO check if this is really needed:
                // Suppress ANSI escape sequences in cds-compiler output
                .withEnvironment("NO_COLOR", "1")
                .withCharset(UTF_8);
    }

    private static boolean isDebugCdsLsp() {
        String debug = System.getenv("DEBUG");
        if (debug == null) {
            debug = System.getProperty("DEBUG");
        }
        return (debug != null) && debug.contains("cds-lsp");
    }

    @NotNull
    private static GeneralCommandLine getCommandLine() {
        return new GeneralCommandLine(
                "node",
                resolve(RELATIVE_SERVER_PATH),
                "--stdio"
        );
    }

    @NotNull
    private static GeneralCommandLine getDebugCommandLine() {
        return new GeneralCommandLine(
                "node",
                resolve(RELATIVE_MITM_PATH),
                resolve(RELATIVE_LOG_PATH),
                "node",
                resolve(RELATIVE_SERVER_PATH),
                "--stdio"
        );
    }

    private static String resolve(String relativePath) {
        Path thisPath = Paths.get(getJarPathForClass(ServerDescriptor.class));
        return thisPath
                .getParent()
                .resolve(relativePath)
                .toString();
    }

    @Override
    public boolean isSupportedFile(@NotNull VirtualFile virtualFile) {
        return FileType.EXTENSION.equals(virtualFile.getExtension());
    }

    // TODO Remove this in case cds-lsp ever sends a `client/registerCapability` request to the client to register its
    //  textDocument/formatting capability (see com.intellij.platform.lsp.api.customization.LspFormattingSupport#shouldFormatThisFileExclusivelyByServer)
    @Nullable
    public LspFormattingSupport getLspFormattingSupport() {
        return new LspFormattingSupport() {
            @Override
            public boolean shouldFormatThisFileExclusivelyByServer(@NotNull VirtualFile file, boolean ideCanFormatThisFileItself, boolean serverExplicitlyWantsToFormatThisFile) {
                return FileType.EXTENSION.equals(file.getExtension());
            }
        };
    }
}
