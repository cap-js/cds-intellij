package com.sap.cap.cds.intellij.usersettings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@State(
        name = "com.sap.cap.cds.intellij.usersettings.CdsUserSettings",
        storages = @Storage("CdsUserSettings.xml")
)
public class CdsUserSettings implements PersistentStateComponent<CdsUserSettings.State> {

    public static class State {
        // TODO: Generator will populate this with actual fields
    }

    private State state = new State();

    public static CdsUserSettings getInstance(Project project) {
        return project.getService(CdsUserSettings.class);
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public Map<String, Object> getAllSettings() {
        // TODO: Generator will implement this properly
        return new HashMap<>();
    }
}
