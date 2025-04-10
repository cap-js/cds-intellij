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

import static com.sap.cap.cds.intellij.lsp.CdsLspServerDescriptor.REQUIRED_NODEJS_VERSION;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.isNodeVersionSufficient;

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
                .addLabeledComponent(new JBLabel("Path to Node.js executable"), nodeJsPathText, 1, false)
//                .addComponent(myIdeaUserStatus, 1)
                .addComponent(nodeStatus, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        nodeStatus.setOpaque(true);
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

        var version = verifyNodeVersion(nodeJsPath);
        if (version.isEmpty()) {
            return "";
        }

        return nodeJsPath;
    }

    public void setNodeJsPathText(@NotNull String newText) {
        nodeJsPathText.setText(newText);
    }

    private Optional<String> findNode() {
        String cmd = Platform.current().equals(Platform.WINDOWS) ? "where" : "which";

        var result = executeCli(cmd, "node");

        if (result.isPresent() && new File(result.get()).isFile()) {
            Logger.PLUGIN.debug("Found Node.js at [%s]".formatted(result.get()));
            nodeJsPathText.setText(result.get());
            nodeStatus.setText("Node.js found");
            nodeStatus.setBackground(null);
            return result;
        }

        nodeStatus.setText("Node.js not found");
        nodeStatus.setBackground(JBColor.RED);
        return Optional.empty();
    }

    private Optional<String> verifyNodeVersion(String nodeJsPath) {
        var result = executeCli(nodeJsPath, "-v");
        if (result.isPresent()) {
            var version = result.get();
            if (isNodeVersionSufficient(nodeJsPath)) {
                Logger.PLUGIN.debug("Node.js version [%s] is sufficient".formatted(version));
                nodeStatus.setText("Node.js found and sufficient");
                nodeStatus.setBackground(null);
                return Optional.of(version);
            } else {
                Logger.PLUGIN.debug("Node.js version [%s] is insufficient".formatted(version));
                nodeStatus.setText("Node.js found but insufficient (%s required, you have %s)".formatted(REQUIRED_NODEJS_VERSION, version));
                nodeStatus.setBackground(JBColor.RED);
                return Optional.empty();
            }
        }

        nodeStatus.setText("Node.js could not be determined, please set manually");
        nodeStatus.setBackground(JBColor.RED);
        return Optional.empty();
    }
}
