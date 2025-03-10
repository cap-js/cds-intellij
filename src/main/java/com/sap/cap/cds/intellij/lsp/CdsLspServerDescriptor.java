package com.sap.cap.cds.intellij.lsp;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor;
import com.intellij.platform.lsp.api.customization.LspFormattingSupport;
import com.intellij.util.io.BaseOutputReader;
import com.sap.cap.cds.intellij.CdsFileType;
import com.sap.cap.cds.intellij.util.ErrorUtil;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

import static com.sap.cap.cds.intellij.util.JsonUtil.getPropertyAtPath;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.extractVersion;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.getInterpreter;
import static com.sap.cap.cds.intellij.util.PathUtil.resolve;
import static java.nio.charset.StandardCharsets.UTF_8;

public class CdsLspServerDescriptor extends ProjectWideLspServerDescriptor {

    public static final String RELATIVE_SERVER_BASE_PATH = "cds-lsp/node_modules/@sap/cds-lsp/";
    private static final String RELATIVE_SERVER_PATH = RELATIVE_SERVER_BASE_PATH + "dist/main.js";
    private static final String RELATIVE_FORMAT_CLI_PATH = RELATIVE_SERVER_BASE_PATH + "scripts/formatCli.js";
    private static final String RELATIVE_SERVER_PKG_PATH = RELATIVE_SERVER_BASE_PATH + "package.json";
    private static final ComparableVersion REQUIRED_NODEJS_VERSION = getRequiredNodejsVersion();
    private static final String RELATIVE_MITM_PATH = "cds-lsp/mitm.js";
    private static final String RELATIVE_LOG_PATH = "cds-lsp/stdio.json";
    private static final Map<CommandLineKind, GeneralCommandLine> COMMAND_LINES = new EnumMap<>(CommandLineKind.class);

    static {
        COMMAND_LINES.put(CommandLineKind.SERVER, null);
        COMMAND_LINES.put(CommandLineKind.SERVER_DEBUG, null);
        COMMAND_LINES.put(CommandLineKind.CLI_FORMAT, null);
    }

    public CdsLspServerDescriptor(@NotNull Project project, @NotNull String presentableName) {
        super(project, presentableName);
    }

    private static ComparableVersion getRequiredNodejsVersion() {
        String serverPkgPath = resolve(RELATIVE_SERVER_PKG_PATH);
        String rawVersion;
        try {
            String serverPkg = new String(Files.readAllBytes(Paths.get(serverPkgPath)));
            rawVersion = getPropertyAtPath(serverPkg, new String[]{"engines", "node"}).toString();
        } catch (Exception e) {
            throw new RuntimeException("Cannot determine Node.js version required by CDS Language Server at [%s]".formatted(serverPkgPath), e);
        }
        return extractVersion(rawVersion);
    }

    private static boolean isDebugCdsLsp() {
        String debug = System.getenv("DEBUG");
        if (debug == null) {
            debug = System.getProperty("DEBUG");
        }
        return (debug != null) && debug.contains("cds-lsp");
    }

    public static GeneralCommandLine getServerCommandLine(CommandLineKind kind) {
        if (COMMAND_LINES.get(kind) != null) {
            return COMMAND_LINES.get(kind);
        }
        final String nodeInterpreterPath = getInterpreter(REQUIRED_NODEJS_VERSION).getInterpreterSystemDependentPath();
        switch (kind) {
            case SERVER -> COMMAND_LINES.put(CommandLineKind.SERVER,
                    new GeneralCommandLine(
                            nodeInterpreterPath,
                            "--enable-source-maps",
                            resolve(RELATIVE_SERVER_PATH),
                            "--stdio"
                    ).withEnvironment("CDS_LSP_TRACE_COMPONENTS", "*:verbose").withCharset(UTF_8)
            );
            case SERVER_DEBUG -> COMMAND_LINES.put(CommandLineKind.SERVER_DEBUG,
                    new GeneralCommandLine(
                            nodeInterpreterPath,
                            "--enable-source-maps",
                            resolve(RELATIVE_MITM_PATH),
                            resolve(RELATIVE_LOG_PATH),
                            nodeInterpreterPath,
                            "--inspect",
                            resolve(RELATIVE_SERVER_PATH),
                            "--stdio"
                    ).withEnvironment("CDS_LSP_TRACE_COMPONENTS", "*:debug").withCharset(UTF_8)
            );
            case CLI_FORMAT -> throw new UnsupportedOperationException("Formatting command line not supported");
        }

        if (COMMAND_LINES.get(kind) == null) {
            throw new RuntimeException("Failed to create command line for %s".formatted(kind));
        }
        return COMMAND_LINES.get(kind);
    }

    public static GeneralCommandLine getFormattingCommandLine(Path cwd, Path srcPath) {
        if (COMMAND_LINES.get(CommandLineKind.CLI_FORMAT) != null) {
            return COMMAND_LINES.get(CommandLineKind.CLI_FORMAT);
        }

        COMMAND_LINES.put(CommandLineKind.CLI_FORMAT,
                new GeneralCommandLine(
                        getInterpreter(REQUIRED_NODEJS_VERSION).getInterpreterSystemDependentPath(),
                        resolve(RELATIVE_FORMAT_CLI_PATH),
                        "-f",
                        srcPath.toString()
                ).withWorkDirectory(cwd.toString())
        );
        return COMMAND_LINES.get(CommandLineKind.CLI_FORMAT);
    }

    public GeneralCommandLine getServerCommandLine() throws ExecutionException {
        CommandLineKind kind = isDebugCdsLsp() ? CommandLineKind.SERVER_DEBUG : CommandLineKind.SERVER;
        return getServerCommandLine(kind);
//                .withCharset(UTF_8);
    }

    @NotNull
    @Override
    public OSProcessHandler startServerProcess() throws ExecutionException {
        OSProcessHandler handler = new OSProcessHandler(getServerCommandLine()) {
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

    private void handleServerError(int exitValue) {
        ErrorUtil.show("CDS Language Server exited with code " + exitValue);
    }

    @Override
    public boolean isSupportedFile(@NotNull VirtualFile virtualFile) {
        return CdsFileType.EXTENSION.equals(virtualFile.getExtension());
    }

    // TODO Remove this in case cds-lsp ever sends a `client/registerCapability` request to the client to register its
    //  textDocument/formatting capability (see com.intellij.platform.lsp.api.customization.LspFormattingSupport#shouldFormatThisFileExclusivelyByServer)
    @Nullable
    public LspFormattingSupport getLspFormattingSupport() {
        return new LspFormattingSupport() {
            @Override
            public boolean shouldFormatThisFileExclusivelyByServer(@NotNull VirtualFile file, boolean ideCanFormatThisFileItself, boolean serverExplicitlyWantsToFormatThisFile) {
                return CdsFileType.EXTENSION.equals(file.getExtension());
            }
        };
    }

    public enum CommandLineKind {
        SERVER,
        SERVER_DEBUG,
        CLI_FORMAT
    }
}
