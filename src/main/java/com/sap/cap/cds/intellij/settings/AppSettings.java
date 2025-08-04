package com.sap.cap.cds.intellij.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.sap.cap.cds.intellij.util.NodeJsUtil;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import org.jetbrains.annotations.NonNls;

import static com.sap.cap.cds.intellij.util.NodeJsUtil.checkInterpreter;
import static com.sap.cap.cds.intellij.util.NodeJsUtil.getInterpreterFromPathOrRegistered;

@State(
        name = "com.sap.cap.cds.intellij.settings.AppSettings",
        storages = @Storage("SapCdsLanguageSupportPlugin.xml")
)
public final class AppSettings
        implements PersistentStateComponent<AppSettings.State> {

    private State myState = new State();

    public static AppSettings getInstance() {
        AppSettings instance = ApplicationManager.getApplication().getService(AppSettings.class);
        fixNodeJsPath(instance.getState());
        return instance;
    }

    private static void fixNodeJsPath(State state) {
        if (state != null && checkInterpreter(state.nodeJsPath) != NodeJsUtil.InterpreterStatus.OK) {
            state.nodeJsPath = getInterpreterFromPathOrRegistered();
        }
    }

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public static class State {
        @NonNls @NotNull
        public String nodeJsPath = getInterpreterFromPathOrRegistered(); // on first plugin start
        public String cdsLspEnv = "";
        public boolean nodeStatus = false;
    }

}