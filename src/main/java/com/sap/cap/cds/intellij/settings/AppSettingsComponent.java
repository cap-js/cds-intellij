package com.sap.cap.cds.intellij.settings;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import static com.intellij.ui.JBColor.RED;
import static com.sap.cap.cds.intellij.lsp.CdsLspServerDescriptor.REQUIRED_NODEJS_VERSION;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.*;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.InterpreterStatus.OK;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.InterpreterStatus.OUTDATED;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JBTextField nodeJsPathText = new JBTextField();
    private final JBTextField cdsLspEnvText = new JBTextField();
    //    private final JBCheckBox myIdeaUserStatus = new JBCheckBox("IntelliJ IDEA user");

        public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Path to Node.js executable"), nodeJsPathText, 1, false)
                .addLabeledComponent(new JBLabel("Additional env for LSP server"), cdsLspEnvText, 10, false)
//                .addComponent(myIdeaUserStatus, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        InputVerifier verifier = new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                return validateAndUpdateUI();
            }
        };
        nodeJsPathText.setInputVerifier(verifier);
        cdsLspEnvText.setInputVerifier(verifier);

        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateAndUpdateUI();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateAndUpdateUI();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateAndUpdateUI();
            }
        };
        nodeJsPathText.getDocument().addDocumentListener(listener);
        cdsLspEnvText.getDocument().addDocumentListener(listener);
    };

    private boolean validateAndUpdateUI() {
        boolean valid = true;
        String nodeJsPath = nodeJsPathText.getText();
        InterpreterStatus status = validateInterpreter(nodeJsPath);
        if (status == OK) {
            nodeJsPathStateHint("found and sufficient", null);
        } else if (status == OUTDATED) {
            nodeJsPathStateHint("found but outdated (required version: %s)".formatted(REQUIRED_NODEJS_VERSION), RED);
            valid = false;
        } else {
            nodeJsPathStateHint("not found. Please enter valid path to Node.js executable", RED);
            valid = false;
        }
        try {
            getCdsLspEnvMap(cdsLspEnvText.getText());
            cdsLspEnvStateHint("valid", null);
        } catch (Throwable e) {
            valid = false;
            cdsLspEnvStateHint("invalid: %s".formatted(e.getMessage()), RED);
        }
        return valid;
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return nodeJsPathText;
    }

    @NotNull
    public String getNodeJsPathText() {
        return nodeJsPathText.getText();
    }

    public void setNodeJsPathText(@NotNull String newText) {
        nodeJsPathText.setText(newText);
    }

    public String getCdsLspEnvText() {
        return cdsLspEnvText.getText();
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
