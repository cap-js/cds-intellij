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
import com.sap.cap.cds.intellij.util.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.intellij.util.PathUtil.getJarPathForClass;
import static com.sap.cap.cds.intellij.util.JsonUtil.getPropertyAtPath;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ServerDescriptor extends ProjectWideLspServerDescriptor {

    private static final String RELATIVE_SERVER_PATH = "cds-lsp/node_modules/@sap/cds-lsp/dist/main.js";
    private static final String RELATIVE_SERVER_PKG_PATH = "cds-lsp/node_modules/@sap/cds-lsp/package.json";
    private static final String RELATIVE_MITM_PATH = "cds-lsp/mitm.js";
    private static final String RELATIVE_LOG_PATH = "cds-lsp/stdio.json";
    public static final int SERVER_EXIT_NODE_VERSION = 72;

    public ServerDescriptor(@NotNull Project project, @NotNull String presentableName) {
        super(project, presentableName);
    }

    @NotNull
    @Override
    public OSProcessHandler startServerProcess() throws ExecutionException {
        checkNodeVersion();

        OSProcessHandler handler = new OSProcessHandler(getCommandLine()) {
            @Override
            protected BaseOutputReader.@NotNull Options readerOptions() {
                return BaseOutputReader.Options.forMostlySilentProcess();
            }
        };
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) { /*ignore*/ }

        Process process = handler.getProcess();
        int exitValue;
        try {
            exitValue = process.exitValue();
            handleServerError(exitValue);
        } catch (RuntimeException e) { /* process still running */ }
        return handler;
    }

    private void checkNodeVersion() {
        String requiredVersionStr = "";
        try {
            String serverPkg = new String(Files.readAllBytes(Paths.get(resolve(RELATIVE_SERVER_PKG_PATH))));
            requiredVersionStr = getPropertyAtPath(serverPkg, new String[]{"engines", "node"}).toString();
        } catch (Exception e) {
            UserError.show("Cannot determine Node.js version required by CDS Language Server.", e);
            return;
        }
        requiredVersionStr = requiredVersionStr.replaceAll("[^\\d.]", "");

        String versionStr = "";
        try {
            Process process = new ProcessBuilder("node", "--version").start();
            versionStr = new String(process.getInputStream().readAllBytes()).trim();
        } catch (Exception e) {
            UserError.show("Cannot determine Node.js version. Please make sure Node.js is installed and available in the PATH.", e);
            return;
        }
        if (versionStr.startsWith("v")) {
            versionStr = versionStr.substring(1);
        }

        ComparableVersion requiredVersion = new ComparableVersion(requiredVersionStr);
        ComparableVersion version = new ComparableVersion(versionStr);
        if (version.compareTo(requiredVersion) < 0) {
            UserError.show("CDS Language Server requires Node.js version %s, but found earlier Node.js version %s. Please update your Node.js installation.".formatted(requiredVersionStr, versionStr));
        }
    }

    private void handleServerError(int exitValue) {
        UserError.show("CDS Language Server exited with code " + exitValue);
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
