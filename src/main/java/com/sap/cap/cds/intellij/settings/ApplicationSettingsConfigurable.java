package com.sap.cap.cds.intellij.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

import static com.sap.cap.cds.intellij.util.NodeJsUtil.getInterpreterFromPathOrRegistered;

/**
 * Provides controller functionality for application settings.
 */
final class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    // A default constructor with no arguments is required because
    // this implementation is registered as an applicationConfigurable

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "CAP CDS"; // Q: where is this used?
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());
        return !(mySettingsComponent.getNodeJsPathText().equals(state.nodeJsPath) &&
                mySettingsComponent.getCdsLspEnvText().equals(state.cdsLspEnv));

    }

    @Override
    public void apply() {
        AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());
        String text = mySettingsComponent.getNodeJsPathText();
        state.nodeJsPath = text.isBlank()
                ? getInterpreterFromPathOrRegistered()
                : text;
        state.cdsLspEnv = mySettingsComponent.getCdsLspEnvText();
    }

    @Override
    public void reset() {
        AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());
        mySettingsComponent.setNodeJsPathText(state.nodeJsPath);
        mySettingsComponent.setCdsLspEnvText(state.cdsLspEnv);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}