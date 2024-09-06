package com.sap.cap.cds.intellij.lsp;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor;
import com.intellij.platform.lsp.api.customization.LspFormattingSupport;
import com.intellij.util.io.BaseOutputReader;
import com.sap.cap.cds.intellij.FileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.intellij.util.PathUtil.getJarPathForClass;
import static java.nio.charset.Charset.defaultCharset;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ServerDescriptor extends ProjectWideLspServerDescriptor {

    private static final String RELATIVE_SERVER_PATH = "cds-lsp/node_modules/@sap/cds-lsp/dist/main.js";
    private static final String RELATIVE_MITM_PATH = "cds-lsp/mitm.js";
    private static final String RELATIVE_LOG_PATH = "cds-lsp/stdio.json";

    private volatile boolean started;

    public ServerDescriptor(@NotNull Project project, @NotNull String presentableName) {
        super(project, presentableName);
    }

    @NotNull
    @Override
    public OSProcessHandler startServerProcess() throws ExecutionException {
        OSProcessHandler handler = new OSProcessHandler(getCommandLine()) {
            @Override
            protected BaseOutputReader.@NotNull Options readerOptions() {
                return BaseOutputReader.Options.forMostlySilentProcess();
            }
            @Override
            protected void onOSProcessTerminated(int exitCode) {
                super.onOSProcessTerminated(exitCode);
                if (started && exitCode != 0) {
                    showUserError(getProcess());
                }
            }
        };
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) { /*ignore*/ }
        tryHandleServerError(handler);
        return handler;
    }

    private void tryHandleServerError(OSProcessHandler handler) {
        Process process = handler.getProcess();
        try {
            process.exitValue();
            showUserError(process);
        } catch (RuntimeException e) {
            // process is running
            started = true;
        }
    }

    private void showUserError(Process process) {
        try (BufferedReader reader = process.errorReader(defaultCharset())) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Error: ")) {
                    UserError.show(line);
                    return;
                }
            }
        } catch (IOException e) { /* ignore */ }
        UserError.show("CDS Language Server exited with code " + process.exitValue());
    }

    public GeneralCommandLine getCommandLine() throws ExecutionException {
        return (isDebugCdsLsp() ? getDebugCommandLine() : getDefaultCommandLine())
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

    private static GeneralCommandLine getDefaultCommandLine() {
        return new GeneralCommandLine(
                "node",
                resolve(RELATIVE_SERVER_PATH),
                "--stdio"
        ).withEnvironment("CDS_LSP_TRACE_COMPONENTS", "*:verbose");
    }

    private static GeneralCommandLine getDebugCommandLine() {
        return new GeneralCommandLine(
                "node",
                resolve(RELATIVE_MITM_PATH),
                resolve(RELATIVE_LOG_PATH),
                "node",
                "--inspect",
                resolve(RELATIVE_SERVER_PATH),
                "--stdio"
        ).withEnvironment("CDS_LSP_TRACE_COMPONENTS", "*:debug");
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
