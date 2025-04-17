package com.sap.cap.cds.intellij.util;

import com.intellij.execution.Platform;
import com.sap.cap.cds.intellij.settings.AppSettings;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.io.File;
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

    public static Optional<ComparableVersion> getVersion(String nodeJsPath) {
        return CliUtil.executeCli(nodeJsPath, "--version")
                .map(NodeJsUtil::extractVersion);
    }

    public static ComparableVersion extractVersion(String rawVersion) {
        return new ComparableVersion(rawVersion.replaceAll("[^0-9.]", ""));
    }

    public static boolean isNodeVersionSufficient(ComparableVersion version) {
        return version.compareTo(REQUIRED_NODEJS_VERSION) >= 0;
    }
}
