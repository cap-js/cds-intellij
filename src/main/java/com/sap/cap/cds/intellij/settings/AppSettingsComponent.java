package com.sap.cap.cds.intellij.settings;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.sap.cap.cds.intellij.util.Logger;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Optional;

import static com.intellij.ui.JBColor.RED;
import static com.sap.cap.cds.intellij.lsp.CdsLspServerDescriptor.REQUIRED_NODEJS_VERSION;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.*;

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

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return nodeJsPathText;
    }

    @NotNull
    public String getNodeJsPathText() {
        String path = nodeJsPathText.getText();
        if (path.isBlank()) {
            Optional<String> nodeFound = whichNode();
            if (nodeFound.isEmpty()) {
                nodejsLabel("not found", RED);
                return "";
            }
            Logger.PLUGIN.debug("found at [%s]".formatted(path));
            setNodeJsPathText(path);
            nodejsLabel("found", null);
            path = nodeFound.get();
        }

        if (!verifyNodeVersion(path)) {
            return "";
        }

        return path;
    }

    public void setNodeJsPathText(@NotNull String newText) {
        nodeJsPathText.setText(newText);
    }

    private void nodejsLabel(String state, JBColor color) {
        nodeStatus.setText("Node.js " + state);
        nodeStatus.setBackground(color);
    }

    private boolean verifyNodeVersion(String nodeJsPath) {
        Optional<ComparableVersion> version = getVersion(nodeJsPath);
        if (version.isEmpty()) {
            nodejsLabel("not found. Please set path to executable", RED);
            return false;
        }
        ComparableVersion nodeVersion = version.get();
        if (isNodeVersionSufficient(nodeVersion)) {
            Logger.PLUGIN.debug("version [%s] is sufficient".formatted(nodeVersion));
            nodejsLabel("found and sufficient", null);
            return true;
        }
        Logger.PLUGIN.debug("version [%s] is insufficient".formatted(nodeVersion));
        nodejsLabel("found but outdated (required: %s but found: %s)".formatted(REQUIRED_NODEJS_VERSION, nodeVersion), RED);
        return false;
    }
}
