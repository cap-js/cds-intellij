package com.sap.cap.cds.intellij.util;

import com.intellij.execution.Platform;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.sap.cap.cds.intellij.lsp.UserError;
import com.sap.cap.cds.intellij.settings.AppSettings;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.sap.cap.cds.intellij.lsp.CdsLspServerDescriptor.REQUIRED_NODEJS_VERSION;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.InterpreterStatus.*;

public class NodeJsUtil {

    // We currently don't react to changes of registered interpreters
    private static String nodeJsPathCached = null;

    public static String getInterpreterFromSetting() {
        return Objects.requireNonNull(AppSettings.getInstance().getState()).nodeJsPath;
    }

    public static String getInterpreterFromPathOrRegistered() {
        if (nodeJsPathCached == null) {
            Logger.PLUGIN.debug("Searching for Node.js >= v%s".formatted(REQUIRED_NODEJS_VERSION));
            Optional<String> nodeFound = whichNode();
            if (nodeFound.isEmpty() || validateInterpreter(nodeFound.get()) != OK) {
                nodeFound = getLocalInterpreter();
                if (nodeFound.isEmpty()) {
                    UserError.show("Suitable Node.js interpreter not found. Please install at least version %s and set its full path at File > Settings > Languages & Frameworks > CDS".formatted(REQUIRED_NODEJS_VERSION));
                    nodeJsPathCached = "NOT_FOUND";
                    return nodeJsPathCached;
                }
            }
            nodeJsPathCached = nodeFound.get();
        }
        return nodeJsPathCached;
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
        }
        Logger.PLUGIN.debug("Node.js interpreter at [%s] with version [%s] is outdated".formatted(nodeJsPath, nodeVersion));
        return OUTDATED;
    }

    private static Optional<String> getLocalInterpreter() {
        boolean isPluginAvailable = PluginManagerCore.isPluginInstalled(PluginId.getId("NodeJS"));
        if (!isPluginAvailable) {
            return Optional.empty();
        }

        try {
            Class<?> managerClass = Class.forName("com.intellij.lang.javascript.interpreters.NodeJsLocalInterpreterManager");
            Object instance = managerClass.getMethod("getInstance").invoke(null);
            Class<?> interpreterClass = Class.forName("com.intellij.lang.javascript.interpreters.NodeJsLocalInterpreter");
            List<?> interpreters = (List<?>) managerClass.getMethod("getInterpreters").invoke(instance);
            for (Object interpreter : interpreters) {
                String path = (String) interpreterClass.getMethod("getInterpreterSystemDependentPath").invoke(interpreter);
                if (path != null && !path.isEmpty()) {
                    Optional<ComparableVersion> version = getVersion(path);
                    if (version.isPresent() && isNodeVersionSufficient(version.get())) {
                        Logger.PLUGIN.debug("Local Node.js interpreter at [%s] with version [%s] is sufficient".formatted(path, version.get()));
                        return Optional.of(path);
                    }
                }
            }
            Logger.PLUGIN.debug("Suitable local Node.js interpreter not found (searched %d interpreters)".formatted(interpreters.size()));
            return Optional.empty();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Logger.PLUGIN.debug("Error accessing Node.js local interpreter manager: %s".formatted(e.getMessage()));
            return Optional.empty();
        }
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
