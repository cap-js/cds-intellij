package com.sap.cap.cds.intellij.textmate;


import com.intellij.ide.AppLifecycleListener;
import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.util.PathUtil;
import com.sap.cap.cds.intellij.CdsPlugin;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import com.sap.cap.cds.intellij.util.Logger;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.textmate.TextMateServiceImpl;
import org.jetbrains.plugins.textmate.configuration.TextMateUserBundlesSettings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.sap.cap.cds.intellij.util.StackUtil.getCaller;
import static com.sap.cap.cds.intellij.util.StackUtil.getMethod;

public class CdsTextMateBundleManager implements AppLifecycleListener, /*obsolete*/ StartupActivity, /* replacement*/ ProjectActivity, DynamicPluginListener {

    public static final CdsTextMateBundleManager INSTANCE = new CdsTextMateBundleManager();

    private volatile Boolean isBundleRegistered = false;

    @NotNull
    private static TextMateUserBundlesSettings getBundlesSettings() {
        TextMateUserBundlesSettings settings = TextMateUserBundlesSettings.getInstance();
        assert settings != null;
        return settings;
    }

    private static void logBundlePresent(TextMateUserBundlesSettings settings) {
        boolean isBundlePresent = settings.getBundles().values().stream().anyMatch(value -> CdsLanguage.LABEL.equals(value.getName()));
        Logger.PLUGIN.info("TextMate bundle present: " + isBundlePresent);
    }

    @NotNull
    private static String getBundlePath() {
        Path thisPath = Paths.get(PathUtil.getJarPathForClass(CdsTextMateBundleManager.class));
        return thisPath
                .getParent()
                .resolve(CdsTextMateBundle.RELATIVE_PATH)
                .toString();
    }

    public void registerBundle() {
        Logger.TM_BUNDLE.info("TextMate bundle registration initiated by " + getMethod(getCaller(3)));

        if (isBundleRegistered) {
            Logger.TM_BUNDLE.info("TextMate bundle: registerBundle has been called before");
            return;
        }
        isBundleRegistered = true;

        TextMateUserBundlesSettings settings = getBundlesSettings();
        logBundlePresent(settings);
        settings.addBundle(getBundlePath(), CdsLanguage.LABEL); // Will also update existing bundle.
        TextMateServiceImpl.getInstance().reloadEnabledBundles();
    }

    private void unregisterBundle() {
        TextMateUserBundlesSettings settings = getBundlesSettings();
        logBundlePresent(settings);
        settings.disableBundle(getBundlePath());
        TextMateServiceImpl.getInstance().reloadEnabledBundles();
    }

    /**
     * Overridden listener methods
     */

    @Override
    public void appFrameCreated(@NotNull List<String> commandLineArgs) {
        registerBundle();
    }

    @Override
    public void runActivity(@NotNull Project project) {
        registerBundle();
    }

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        registerBundle();
        return null;
    }

    @Override
    public void beforePluginUnload(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        if (isUpdate || !CdsPlugin.PACKAGE.equals(pluginDescriptor.getPluginId().toString())) {
            return;
        }

        unregisterBundle();
    }
}
