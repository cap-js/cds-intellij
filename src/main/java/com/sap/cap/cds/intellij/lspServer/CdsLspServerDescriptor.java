package com.sap.cap.cds.intellij.lspServer;

import com.intellij.execution.configurations.GeneralCommandLine;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.sap.cap.cds.intellij.lspServer.CdsLspServerDescriptor.CommandLineKind.SERVER;
import static com.sap.cap.cds.intellij.lspServer.CdsLspServerDescriptor.CommandLineKind.SERVER_DEBUG;
import static com.sap.cap.cds.intellij.util.JsonUtil.getPropertyAtPath;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.*;
import static com.sap.cap.cds.intellij.util.PathUtil.resolve;
import static com.sap.cap.cds.intellij.util.SupportUtil.isDebugCdsLsp;
import static java.nio.charset.StandardCharsets.UTF_8;

public class CdsLspServerDescriptor {

    public static final String RELATIVE_SERVER_BASE_PATH = "cds-lsp/node_modules/@sap/cds-lsp/";
    private static final String RELATIVE_SERVER_PATH = RELATIVE_SERVER_BASE_PATH + "dist/main.js";
    private static final String RELATIVE_FORMAT_CLI_PATH = RELATIVE_SERVER_BASE_PATH + "scripts/formatCli.js";
    private static final String RELATIVE_SERVER_PKG_PATH = RELATIVE_SERVER_BASE_PATH + "package.json";
    public static final ComparableVersion REQUIRED_NODEJS_VERSION = getRequiredNodejsVersion();
    private static final CommandLineKind SERVER_COMMAND_LINE_KIND = getServerCommandLineKind();

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
            return SERVER_DEBUG;
        }
        return SERVER;
    }

    public static GeneralCommandLine getServerCommandLine() {
        Map<String, String> envMap = getCdsLspEnvMapFromSetting();
        final String nodeInterpreterPath = getInterpreterFromSetting();
        return switch (SERVER_COMMAND_LINE_KIND) {
            case SERVER -> new GeneralCommandLine(
                    nodeInterpreterPath,
                    "--enable-source-maps",
                    resolve(RELATIVE_SERVER_PATH),
                    "--stdio"
            )
                    .withEnvironment(envMap)
                    .withCharset(UTF_8);

            case SERVER_DEBUG -> new GeneralCommandLine(
                    nodeInterpreterPath,
                    "--enable-source-maps",
                    "--inspect",
                    resolve(RELATIVE_SERVER_PATH),
                    "--stdio"
            )
                    .withEnvironment("CDS_LSP_TRACE_COMPONENTS", "*:verbose")
                    .withEnvironment(envMap)
                    .withCharset(UTF_8);

            case CLI_FORMAT -> throw new UnsupportedOperationException("Formatting command line not supported");
        };
    }

    public static GeneralCommandLine getFormattingCommandLine(Path cwd, Path srcPath) {
        return new GeneralCommandLine(
                getInterpreterFromSetting(),
                resolve(RELATIVE_FORMAT_CLI_PATH),
                "-f",
                srcPath.toString()
        ).withWorkDirectory(cwd.toString());
    }

    public enum CommandLineKind {
        SERVER,
        SERVER_DEBUG,
        CLI_FORMAT
    }
}
