package com.sap.cap.cds.intellij.util;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Platform;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.sap.cap.cds.intellij.settings.AppSettings;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import static com.sap.cap.cds.intellij.lsp.CdsLspServerDescriptor.REQUIRED_NODEJS_VERSION;

public class NodeJsUtil {

    public static String getInterpreter(ComparableVersion requiredVersion) {
        Logger.PLUGIN.debug("Searching for Node.js >= v%s".formatted(requiredVersion));

        AppSettings instance = AppSettings.getInstance();
        String nodeJsPath = instance.getState().nodeJsPath;
        return nodeJsPath.isBlank() ? "node" : nodeJsPath;
    }

    public static Optional<String> whichNode() {
        String cmd = Platform.current().equals(Platform.WINDOWS) ? "where" : "which";
        return CliUtil.executeCli(cmd, "node")
                .filter(path -> new File(path).isFile())
                .or(Optional::empty);
    }

    public static ComparableVersion extractVersion(String rawVersion) {
        return new ComparableVersion(rawVersion.replaceAll("[^0-9.]", ""));
    }

    public static Optional<ComparableVersion> getVersion(String nodeJsPath) {
        try {
            Process process = new GeneralCommandLine(nodeJsPath, "--version").createProcess();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line == null) {
                    Logger.PLUGIN.debug("Failed to read version from [%s]: no output".formatted(nodeJsPath));
                    return Optional.empty();
                }
                return Optional.of(extractVersion(line));
            } catch (IOException e) {
                Logger.PLUGIN.error("Failed to read version from [%s]: failed to read output".formatted(nodeJsPath), e);
                return Optional.empty();
            }
        } catch (ExecutionException e) {
            Logger.PLUGIN.error("Failed to read version from [%s]: failed to start process".formatted(nodeJsPath), e);
            return Optional.empty();
        }
    }

    public static boolean isNodeVersionSufficient(ComparableVersion version) {
        return version.compareTo(REQUIRED_NODEJS_VERSION) >= 0;
    }
}
