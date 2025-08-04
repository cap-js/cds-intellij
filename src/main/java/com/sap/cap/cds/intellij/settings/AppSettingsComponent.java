package com.sap.cap.cds.intellij.settings;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import static com.intellij.ui.JBColor.RED;
import static com.sap.cap.cds.intellij.lspServer.CdsLspServerDescriptor.REQUIRED_NODEJS_VERSION;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.checkInterpreter;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.getCdsLspEnvMap;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JBTextField nodeJsPathText = new JBTextField();
    private final JBTextField cdsLspEnvText = new JBTextField();

    public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Path to Node.js executable"), nodeJsPathText, 1, false)
                .addLabeledComponent(new JBLabel("Additional env for LSP server"), cdsLspEnvText, 10, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        nodeJsPathText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateAndUpdateNodeJsPath();
            }
        });
        cdsLspEnvText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateAndUpdateEnvMap();
            }
        });
    }

    public void validateAndUpdateNodeJsPath() {
        switch (checkInterpreter(getNodeJsPathText())) {
            case OK -> {
                nodeJsPathStateHint("found and sufficient", null);
            }
            case OUTDATED ->
                    nodeJsPathStateHint("found but outdated (required version: %s)".formatted(REQUIRED_NODEJS_VERSION), RED);
            case NOT_FOUND -> nodeJsPathStateHint("not found. Please enter valid path to Node.js executable", RED);
        }
    }

    public void validateAndUpdateEnvMap() {
        try {
            getCdsLspEnvMap(getCdsLspEnvText());
            cdsLspEnvStateHint("valid", null);
        } catch (Throwable e) {
            cdsLspEnvStateHint("invalid: %s".formatted(e.getMessage()), RED);
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
        return nodeJsPathText.getText().trim();
    }

    public void setNodeJsPathText(@NotNull String newText) {
        nodeJsPathText.setText(newText);
    }

    public String getCdsLspEnvText() {
        return cdsLspEnvText.getText().trim();
    }

    public void setCdsLspEnvText(@NotNull String newText) {
        cdsLspEnvText.setText(newText);
    }

    private void nodeJsPathStateHint(String state, JBColor color) {
        nodeJsPathText.setToolTipText("Interpreter " + state);
        nodeJsPathText.setBackground(color);
    }

    private void cdsLspEnvStateHint(String state, JBColor color) {
        cdsLspEnvText.setToolTipText("Env setting " + state);
        cdsLspEnvText.setBackground(color);
    }
}
