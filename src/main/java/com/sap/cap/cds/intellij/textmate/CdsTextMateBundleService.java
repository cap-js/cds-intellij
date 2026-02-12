package com.sap.cap.cds.intellij.textmate;


import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Service.Level;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import com.sap.cap.cds.intellij.util.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.TextMateServiceImpl;
import org.jetbrains.plugins.textmate.configuration.TextMateUserBundlesSettings;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.intellij.util.PathUtil.getJarPathForClass;
import static com.sap.cap.cds.intellij.util.StackUtil.getCaller;
import static com.sap.cap.cds.intellij.util.StackUtil.getMethod;

@Service(Level.APP)
public final class CdsTextMateBundleService {

    private volatile Boolean isBundleRegistered = false;

    @NotNull
    private static TextMateUserBundlesSettings getBundlesSettings() {
        TextMateUserBundlesSettings settings = TextMateUserBundlesSettings.getInstance();
        assert settings != null;
        return settings;
    }

    private static void logBundlePresent(TextMateUserBundlesSettings settings) {
        boolean isBundlePresent = settings.getBundles().values().stream().anyMatch(value -> CdsLanguage.LABEL.equals(value.getName()));
        Logger.TM_BUNDLE.info("TextMate bundle present before action: " + isBundlePresent);
    }

    @NotNull
    private static String getBundlePath() {
        Path thisPath = Paths.get(getJarPathForClass(CdsTextMateBundleService.class));
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

    public void unregisterBundle() {
        TextMateUserBundlesSettings settings = getBundlesSettings();
        logBundlePresent(settings);
        settings.removeBundle(getBundlePath());
        TextMateServiceImpl.getInstance().reloadEnabledBundles();
    }
}
