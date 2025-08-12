package com.sap.cap.cds.intellij.usersettings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CdsUserSettingsConfigurable implements Configurable {

    private final Project project;
    private CdsUserSettingsComponent component;

    public CdsUserSettingsConfigurable(Project project) {
        this.project = project;
    }

    @Override
    public @Nls String getDisplayName() {
        return "CDS Language Server";
    }

    @Override
    public @Nullable JComponent createComponent() {
        component = new CdsUserSettingsComponent(project);
        return component.getPanel();
    }

    @Override
    public boolean isModified() {
        return component != null && component.isModified();
    }

    @Override
    public void apply() {
        if (component != null) {
            component.apply();
        }
    }

    @Override
    public void reset() {
        if (component != null) {
            component.reset();
        }
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }
}
