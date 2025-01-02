package com.sap.cap.cds.intellij.lifecycle;

import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.sap.cap.cds.intellij.CdsPlugin;
import com.sap.cap.cds.intellij.textmate.CdsTextMateBundleService;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.application.ApplicationManager.getApplication;

public class PluginLifecycleListener implements DynamicPluginListener {

    @Override
    public void pluginLoaded(@NotNull IdeaPluginDescriptor pluginDescriptor) {
        getApplication().getService(CdsTextMateBundleService.class).registerBundle();
    }

    // TODO also on plugin disablement
    @Override
    public void beforePluginUnload(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        if (!CdsPlugin.PACKAGE.equals(pluginDescriptor.getPluginId().toString())) {
            return;
        }

        getApplication().getService(CdsTextMateBundleService.class).unregisterBundle();
    }

}
