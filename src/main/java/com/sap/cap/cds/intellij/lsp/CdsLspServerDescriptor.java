package com.sap.cap.cds.intellij.lsp;

import com.intellij.execution.configurations.GeneralCommandLine;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

import static com.sap.cap.cds.intellij.util.JsonUtil.getPropertyAtPath;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.*;
import static com.sap.cap.cds.intellij.util.PathUtil.resolve;
import static java.nio.charset.StandardCharsets.UTF_8;

public class CdsLspServerDescriptor {

    public static final String RELATIVE_SERVER_BASE_PATH = "cds-lsp/node_modules/@sap/cds-lsp/";
    private static final String RELATIVE_SERVER_PATH = RELATIVE_SERVER_BASE_PATH + "dist/main.js";
    private static final String RELATIVE_FORMAT_CLI_PATH = RELATIVE_SERVER_BASE_PATH + "scripts/formatCli.js";
    private static final String RELATIVE_SERVER_PKG_PATH = RELATIVE_SERVER_BASE_PATH + "package.json";
    public static final ComparableVersion REQUIRED_NODEJS_VERSION = getRequiredNodejsVersion();
    private static final String RELATIVE_MITM_PATH = "cds-lsp/mitm.js";
    private static final String RELATIVE_LOG_PATH = "cds-lsp/stdio.json";
    private static final Map<CommandLineKind, GeneralCommandLine> COMMAND_LINES = new EnumMap<>(CommandLineKind.class);
    private static final CommandLineKind SERVER_COMMAND_LINE_KIND = getServerCommandLineKind();

    static {
        COMMAND_LINES.put(CommandLineKind.SERVER, null);
        COMMAND_LINES.put(CommandLineKind.SERVER_DEBUG, null);
        COMMAND_LINES.put(CommandLineKind.CLI_FORMAT, null);
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

    private static CommandLineKind getServerCommandLineKind() {
        if (isDebugCdsLsp()) {
            return CommandLineKind.SERVER_DEBUG;
        }
        return CommandLineKind.SERVER;
    }

    private static boolean isDebugCdsLsp() {
        String debug = System.getenv("DEBUG");
        if (debug == null) {
            debug = System.getProperty("DEBUG");
        }
        return (debug != null) && debug.contains("cds-lsp");
    }

    public static GeneralCommandLine getServerCommandLine() {
        GeneralCommandLine cached = COMMAND_LINES.get(SERVER_COMMAND_LINE_KIND);
        if (cached != null) {
            return cached;
        }
        Map<String, String> envMap = getCdsLspEnvMapFromSetting();
        final String nodeInterpreterPath = getInterpreterFromSetting();
        switch (SERVER_COMMAND_LINE_KIND) {
            case SERVER -> COMMAND_LINES.put(CommandLineKind.SERVER,
                    new GeneralCommandLine(
                            nodeInterpreterPath,
                            "--enable-source-maps",
                            resolve(RELATIVE_SERVER_PATH),
                            "--stdio"
                    )
                            .withEnvironment(envMap)
                            .withCharset(UTF_8)
            );
            case SERVER_DEBUG -> COMMAND_LINES.put(CommandLineKind.SERVER_DEBUG,
                    new GeneralCommandLine(
                            nodeInterpreterPath,
                            resolve(RELATIVE_MITM_PATH),
                            resolve(RELATIVE_LOG_PATH),
                            nodeInterpreterPath,
                            "--enable-source-maps",
                            "--inspect",
                            resolve(RELATIVE_SERVER_PATH),
                            "--stdio"
                    )
                            .withEnvironment("CDS_LSP_TRACE_COMPONENTS", "*:verbose")
                            .withEnvironment(envMap)
                            .withCharset(UTF_8)
            );
            case CLI_FORMAT -> throw new UnsupportedOperationException("Formatting command line not supported");
        }

        GeneralCommandLine result = COMMAND_LINES.get(SERVER_COMMAND_LINE_KIND);
        if (result == null) {
            throw new RuntimeException("Failed to create command line for %s".formatted(SERVER_COMMAND_LINE_KIND));
        }
        return result;
    }

    public static GeneralCommandLine getFormattingCommandLine(Path cwd, Path srcPath) {
        if (COMMAND_LINES.get(CommandLineKind.CLI_FORMAT) != null) {
            return COMMAND_LINES.get(CommandLineKind.CLI_FORMAT);
        }

        COMMAND_LINES.put(CommandLineKind.CLI_FORMAT,
                new GeneralCommandLine(
                        getInterpreterFromSetting(),
                        resolve(RELATIVE_FORMAT_CLI_PATH),
                        "-f",
                        srcPath.toString()
                ).withWorkDirectory(cwd.toString())
        );
        return COMMAND_LINES.get(CommandLineKind.CLI_FORMAT);
    }

    public enum CommandLineKind {
        SERVER,
        SERVER_DEBUG,
        CLI_FORMAT
    }
}
