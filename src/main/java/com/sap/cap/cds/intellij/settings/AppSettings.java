package com.sap.cap.cds.intellij.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import org.jetbrains.annotations.NonNls;

@State(
        name = "com.sap.cap.cds.intellij.settings.AppSettings",
        storages = @Storage("SapCdsLanguageSupportPlugin.xml")
)
public final class AppSettings
        implements PersistentStateComponent<AppSettings.State> {

    public static class State {
        @NonNls @NotNull
        public String nodeJsPath = "node";
        public boolean nodeStatus = false;
    }

    private State myState = new State();

    public static AppSettings getInstance() {
        return ApplicationManager.getApplication().getService(AppSettings.class);
    }

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

}