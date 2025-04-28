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
import static com.sap.cap.cds.intellij.util.NodeJsUtil.InterpreterStatus;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.InterpreterStatus.OK;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.InterpreterStatus.OUTDATED;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.validateInterpreter;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JBTextField nodeJsPathText = new JBTextField();
    //    private final JBCheckBox myIdeaUserStatus = new JBCheckBox("IntelliJ IDEA user");

        public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Path to Node.js executable"), nodeJsPathText, 1, false)
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

        nodeJsPathText.getDocument().addDocumentListener(new DocumentListener() {
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
        });
    };

    private boolean validateAndUpdateUI() {
        String nodeJsPath = nodeJsPathText.getText();
        InterpreterStatus status = validateInterpreter(nodeJsPath);
        if (status == OK) {
            stateHint("found and sufficient", null);
            return true;
        } else if (status == OUTDATED) {
            stateHint("found but outdated (required version: %s)".formatted(REQUIRED_NODEJS_VERSION), RED);
            return false;
        } else {
            stateHint("not found. Please enter valid path to Node.js executable", RED);
            return false;
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
        return nodeJsPathText.getText();
    }

    public void setNodeJsPathText(@NotNull String newText) {
        nodeJsPathText.setText(newText);
    }

    private void stateHint(String state, JBColor color) {
        nodeJsPathText.setToolTipText("Interpreter " + state);
        nodeJsPathText.setBackground(color);
    }
}
