package com.sap.cap.cds.intellij.lifecycle;

import com.intellij.ide.AppLifecycleListener;
import com.sap.cap.cds.intellij.textmate.CdsTextMateBundleService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.openapi.application.ApplicationManager.getApplication;

public class IdeLifecycleListener implements AppLifecycleListener {
    @Override
    public void appFrameCreated(@NotNull List<String> commandLineArgs) {
        getApplication().getService(CdsTextMateBundleService.class).registerBundle();
    }
}
