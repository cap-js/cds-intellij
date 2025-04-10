package com.sap.cap.cds.intellij.util;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.sap.cap.cds.intellij.settings.AppSettings;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import static com.sap.cap.cds.intellij.lsp.CdsLspServerDescriptor.REQUIRED_NODEJS_VERSION;

public class NodeJsUtil {

    public static String getInterpreter(ComparableVersion requiredVersion) {
        Logger.PLUGIN.debug("Searching for Node.js >= v%s".formatted(requiredVersion));

        AppSettings instance = AppSettings.getInstance();
//        instance.loadState();
        String nodeJsPath = instance.getState().nodeJsPath;
        return nodeJsPath == null || nodeJsPath.isBlank() ? "node" : nodeJsPath;
//        for (NodeJsLocalInterpreter interpreter : NodeJsLocalInterpreterManager.getInstance().getInterpreters()) {
//            Optional<ComparableVersion> version = getVersion(interpreter.getInterpreterSystemDependentPath());
//            if (version.isEmpty()) {
//                continue;
//            }
//            if (version.get().compareTo(requiredVersion) >= 0) {
//                Logger.PLUGIN.debug("Found suitable Node.js interpreter [%s]".formatted(interpreter.getInterpreterSystemDependentPath()));
//                return interpreter;
//            }
//        }
//        throw new RuntimeException("No suitable Node.js interpreter found with version >= %s (searched %d interpreters)"
//                .formatted(requiredVersion, NodeJsLocalInterpreterManager.getInstance().getInterpreters().size()));
    }

    public static ComparableVersion extractVersion(String rawVersion) {
        return new ComparableVersion(rawVersion.replaceAll("[^0-9.]", ""));
    }

    private static Optional<ComparableVersion> getVersion(String nodeJsPath) {
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

    public static boolean isNodeVersionSufficient(String nodeJsPath) {
        Optional<ComparableVersion> version = getVersion(nodeJsPath);
        return version
                .filter(comparableVersion -> comparableVersion.compareTo(REQUIRED_NODEJS_VERSION) >= 0)
                .isPresent();
    }

}
