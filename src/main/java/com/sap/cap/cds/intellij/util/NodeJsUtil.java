package com.sap.cap.cds.intellij.util;

import com.intellij.execution.Platform;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter;
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreterManager;
import com.intellij.openapi.extensions.PluginId;
import com.sap.cap.cds.intellij.settings.AppSettings;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.sap.cap.cds.intellij.lsp.CdsLspServerDescriptor.REQUIRED_NODEJS_VERSION;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.InterpreterStatus.*;

public class NodeJsUtil {

    public static String getInterpreterFromSetting() {
        var state = Objects.requireNonNull(AppSettings.getInstance().getState());
        if (state.nodeJsPath.isBlank()) {
            state.nodeJsPath = getSuitableInterpreter();
        }
        return state.nodeJsPath;
    }

    public static String getSuitableInterpreter() {
        Logger.PLUGIN.debug("Searching for Node.js >= v%s".formatted(REQUIRED_NODEJS_VERSION));
        Optional<String> nodeFound = whichNode();
        if (nodeFound.isEmpty() || validateInterpreter(nodeFound.get()) != OK) {
            nodeFound = getLocalInterpreter();
            if (nodeFound.isEmpty()) {
                return "NOT_FOUND";
            }
        }
        return nodeFound.get();
    }

    public static InterpreterStatus validateInterpreter(String nodeJsPath) {
        Optional<ComparableVersion> version = getVersion(nodeJsPath);
        if (version.isEmpty()) {
            Logger.PLUGIN.debug("Node.js interpreter at [%s] not found".formatted(nodeJsPath));
            return NOT_FOUND;
        }
        ComparableVersion nodeVersion = version.get();
        if (isNodeVersionSufficient(nodeVersion)) {
            Logger.PLUGIN.debug("Node.js interpreter at [%s] with version [%s] is sufficient".formatted(nodeJsPath, nodeVersion));
            return OK;
        };
        Logger.PLUGIN.debug("Node.js interpreter at [%s] with version [%s] is outdated".formatted(nodeJsPath, nodeVersion));
        return OUTDATED;
    }

    private static Optional<String> getLocalInterpreter() {
        boolean isPluginAvailable = PluginManagerCore.isPluginInstalled(PluginId.getId("NodeJS"));
        if (!isPluginAvailable) {
            return Optional.empty();
        }
        List<NodeJsLocalInterpreter> interpreters = NodeJsLocalInterpreterManager.getInstance().getInterpreters();
        for (NodeJsLocalInterpreter interpreter : interpreters) {
            Optional<ComparableVersion> version = getVersion(interpreter.getInterpreterSystemDependentPath());
            if (version.isEmpty()) {
                continue;
            }
            if (isNodeVersionSufficient(version.get())) {
                Logger.PLUGIN.debug("Local Node.js interpreter at [%s] with version [%s] is sufficient".formatted(interpreter.getInterpreterSystemDependentPath(), version.get()));
                return Optional.of(interpreter.getInterpreterSystemDependentPath());
            }
        }
        Logger.PLUGIN.debug("Suitable local Node.js interpreter not found (searched %d interpreters)".formatted(interpreters.size()));
        return Optional.empty();
    }

    private static Optional<String> whichNode() {
        String cmd = Platform.current().equals(Platform.WINDOWS) ? "where" : "which";
        return CliUtil.executeCli(cmd, "node")
                .filter(path -> new File(path).isFile())
                .or(Optional::empty);
    }

    private static Optional<ComparableVersion> getVersion(String nodeJsPath) {
        return CliUtil.executeCli(nodeJsPath, "--version")
                .map(NodeJsUtil::extractVersion);
    }

    public static ComparableVersion extractVersion(String rawVersion) {
        return new ComparableVersion(rawVersion.replaceAll("[^0-9.]", ""));
    }

    private static boolean isNodeVersionSufficient(ComparableVersion version) {
        return version.compareTo(REQUIRED_NODEJS_VERSION) >= 0;
    }

    public enum InterpreterStatus {
        OK,
        OUTDATED,
        NOT_FOUND
    }
}
