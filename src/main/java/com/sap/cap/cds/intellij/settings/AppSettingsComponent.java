package com.sap.cap.cds.intellij.settings;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Platform;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.sap.cap.cds.intellij.util.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JBTextField nodeJsPathText = new JBTextField();
    //    private final JBCheckBox myIdeaUserStatus = new JBCheckBox("IntelliJ IDEA user");
    private final JLabel nodeStatus = new JLabel("");

    public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Path to Node.JS executable"), nodeJsPathText, 1, false)
//                .addComponent(myIdeaUserStatus, 1)
                .addComponent(nodeStatus, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        nodeStatus.setOpaque(true);
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return nodeJsPathText;
    }

    @NotNull
    public String getNodeJsPathText() {
        String path = nodeJsPathText.getText();

        String nodeJsPath = path.isBlank() ? findNode().orElse("") : path;

        if (nodeJsPath.isBlank()) {
            return "";
        }

        var version = verifyNodeVersion(nodeJsPath, "v20.18.1"); // TODO: read required version from LSP package.json
        if (version.isEmpty()) {
            return "";
        }

        return nodeJsPath;
    }


    private Optional<String> findNode() {
        String cmd = Platform.current().equals(Platform.WINDOWS) ? "where" : "which";

        var result = executeCli(cmd, "node");

        if (result.isPresent() && new File(result.get()).isFile()) {
            Logger.PLUGIN.debug("Found Node.JS at [%s]".formatted(result.get()));
            nodeJsPathText.setText(result.get());
            nodeStatus.setText("Node.JS found");
            nodeStatus.setBackground(null);
            return result;
        }

        nodeStatus.setText("Node.JS not found");
        nodeStatus.setBackground(JBColor.RED);
        return Optional.empty();
    }

    private Optional<String> verifyNodeVersion(String nodeJsPath, String requiredVersion) {

        var result = executeCli(nodeJsPath, "-v");

        if (result.isPresent()) {
            var version = result.get();

            var isOk = isNodeVersionSufficient(version, requiredVersion);

            if (isOk) {
                Logger.PLUGIN.debug("Node.JS version [%s] is sufficient".formatted(version));
                nodeStatus.setText("Node.JS found and sufficient");
                nodeStatus.setBackground(null);
                return Optional.of(version);
            } else {
                Logger.PLUGIN.debug("Node.JS version [%s] is insufficient".formatted(version));
                nodeStatus.setText("Node.JS found but insufficient (%s required, you have %s)".formatted(requiredVersion, version));
                nodeStatus.setBackground(JBColor.RED);
                return Optional.empty();
            }
        }

        nodeStatus.setText("Node.JS could not be determined, please set manually");
        nodeStatus.setBackground(JBColor.RED);
        return Optional.empty();
    }


    Optional<int[]> parseSemVer(String version) {
        Pattern versionPattern = Pattern.compile("^v?([1-9]\\d*)\\.(\\d+)\\.(\\d+)(?:-[a-zA-Z0-9]+)?$");
        Matcher matcher = versionPattern.matcher(version);
        if (matcher.matches()) {
            return Optional.of(new int[]{Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3))});
        }
        return Optional.empty();
    }

    boolean isNodeVersionSufficient(String nodeJsVersion, String requiredVersion) {
        var actualV = parseSemVer(nodeJsVersion);
        var requiredV = parseSemVer(requiredVersion);
        if (actualV.isEmpty() || requiredV.isEmpty()) {
            return false;
        }

        for (int i = 0; i < 3; i++) {
            int a = actualV.get()[i];
            int r = requiredV.get()[i];
            if (a < r) {
                Logger.PLUGIN.debug("Node.JS version [%s] is too low".formatted(nodeJsVersion));
                return false;
            }
            if (r < a) {
                Logger.PLUGIN.debug("Node.JS version [%s] is sufficient".formatted(nodeJsVersion));
                return true;
            }
        }
        return true;
    }


    public static Optional<String> executeCli(String... args) {
        // TODO? what if the IDE wasn't started from a terminal i.e. without an env? Will the GeneralCommandLine work and use the default shell?
        try {
            Process process = new GeneralCommandLine(args).createProcess();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.readLine();
            return output == null ? Optional.empty() : Optional.of(output);
        } catch (ExecutionException | IOException e) {
            Logger.PLUGIN.error("Failed to execute [%s]".formatted(String.join(" ", args)), e);
            return Optional.empty();
        }
    }


    public void setNodeJsPathText(@NotNull String newText) {
        nodeJsPathText.setText(newText);
    }

//    public boolean getIdeaUserStatus() {
//        return myIdeaUserStatus.isSelected();
//    }

//    public void setIdeaUserStatus(boolean newStatus) {
//        myIdeaUserStatus.setSelected(newStatus);
//    }

}
