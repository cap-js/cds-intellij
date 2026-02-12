package com.sap.cap.cds.intellij.lifecycle;

import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.sap.cap.cds.intellij.textmate.CdsTextMateBundleService;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.application.ApplicationManager.getApplication;

public class PluginLifecycleListener implements DynamicPluginListener {

    @Override
    public void pluginLoaded(@NotNull IdeaPluginDescriptor pluginDescriptor) {
        getApplication().getService(CdsTextMateBundleService.class).cleanupLegacyBundles();
    }

    // Note: beforePluginUnload is not called for this plugin because it's not
    // unload-safe (uses non-dynamic LSP4IJ extension point).
}
