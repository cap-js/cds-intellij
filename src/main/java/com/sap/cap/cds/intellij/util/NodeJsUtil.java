package com.sap.cap.cds.intellij.util;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter;
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreterManager;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class NodeJsUtil {

    public static NodeJsLocalInterpreter getInterpreter(ComparableVersion requiredVersion) {
        Logger.PLUGIN.debug("Searching for Node.js interpreter with required version %s".formatted(requiredVersion));

        for (NodeJsLocalInterpreter interpreter : NodeJsLocalInterpreterManager.getInstance().getInterpreters()) {
            Optional<ComparableVersion> version = getRuntimeVersion(interpreter.getInterpreterSystemDependentPath());
            if (version.isEmpty()) {
                continue;
            }
            if (version.get().compareTo(requiredVersion) >= 0) {
                Logger.PLUGIN.debug("Found suitable Node.js interpreter [%s] with version %s".formatted(interpreter.getInterpreterSystemDependentPath(), version.get()));
                return interpreter;
            }
        }
        throw new RuntimeException("No suitable Node.js interpreter found with required version %s (searched %d interpreters)"
                .formatted(requiredVersion, NodeJsLocalInterpreterManager.getInstance().getInterpreters().size()));
    }

    public static ComparableVersion extractVersion(String rawVersion) {
        return new ComparableVersion(rawVersion.replaceAll("[^0-9.]", ""));
    }

    public static Optional<ComparableVersion> getRuntimeVersion(String nodeJsPath) {
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

}
