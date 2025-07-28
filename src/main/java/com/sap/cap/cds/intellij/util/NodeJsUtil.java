package com.sap.cap.cds.intellij.util;

import com.intellij.execution.Platform;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.sap.cap.cds.intellij.lspServer.UserError;
import com.sap.cap.cds.intellij.settings.AppSettings;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.sap.cap.cds.intellij.lspServer.CdsLspServerDescriptor.REQUIRED_NODEJS_VERSION;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.InterpreterStatus.*;

public class NodeJsUtil {

    // We currently don't react to changes of registered interpreters
    private static String nodeJsPathCached = null;

    public static String getInterpreterFromSetting() {
        return Objects.requireNonNull(AppSettings.getInstance().getState()).nodeJsPath;
    }

    public static Map<String, String> getCdsLspEnvMapFromSetting() {
        try {
            return getCdsLspEnvMap(Objects.requireNonNull(AppSettings.getInstance().getState()).cdsLspEnv);
        } catch (IllegalArgumentException e) {
            return Collections.emptyMap();
        }
    }

    public static String getInterpreterFromPathOrRegistered() {
        if (nodeJsPathCached == null) {
            Logger.PLUGIN.debug("Searching for Node.js >= v%s".formatted(REQUIRED_NODEJS_VERSION));
            Optional<String> nodeFound = whichNode();
            if (nodeFound.isEmpty() || checkInterpreter(nodeFound.get()) != OK) {
                nodeFound = getLocalInterpreter();
                if (nodeFound.isEmpty()) {
                    UserError.show("Suitable Node.js interpreter not found. Please install at least version %s and set its full path at File > Settings > Languages & Frameworks > CDS".formatted(REQUIRED_NODEJS_VERSION));
                    nodeJsPathCached = "";
                    return nodeJsPathCached;
                }
            }
            nodeJsPathCached = nodeFound.get();
        }
        return nodeJsPathCached;
    }

    public static InterpreterStatus checkInterpreter(String nodeJsPath) {
        if (nodeJsPath == null || nodeJsPath.isBlank()) {
            Logger.PLUGIN.debug("Node.js interpreter path is empty");
            return NOT_FOUND;
        }
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

    public static Map<String, String> getCdsLspEnvMap(String cdsLspEnv) {
        if (cdsLspEnv == null || cdsLspEnv.isBlank()) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        Arrays.stream(cdsLspEnv.split(";"))
                .forEach(pair -> {
                    if (!pair.matches("[_A-Z0-9]+=[^=]*")) {
                        throw new IllegalArgumentException("%s is not a valid env setting".formatted(pair));
                    }
                    String[] split = pair.split("=");
                    String val = split.length > 1 ? split[1].trim() : "";
                    result.put(split[0].trim(), val);
                });
        return result;
    }

    private static Optional<String> getLocalInterpreter() {
        PluginId pluginId = PluginId.getId("NodeJS");
        boolean isPluginAvailable = PluginManager.isPluginInstalled(pluginId);
        if (!isPluginAvailable) {
            return Optional.empty();
        }

        try {
            Class<?> managerClass = Class.forName("com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreterManager");
            Object instance = managerClass.getMethod("getInstance").invoke(null);
            Class<?> interpreterClass = Class.forName("com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter");
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
