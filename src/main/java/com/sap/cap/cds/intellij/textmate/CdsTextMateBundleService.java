package com.sap.cap.cds.intellij.textmate;


import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Service.Level;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import com.sap.cap.cds.intellij.util.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.TextMateServiceImpl;
import org.jetbrains.plugins.textmate.configuration.TextMateUserBundlesSettings;

/**
 * Manages cleanup of legacy bundle entries in TextMateUserBundlesSettings
 * that were created by older plugin versions using imperative registration.
 */
@Service(Level.APP)
public final class CdsTextMateBundleService {

    private volatile boolean cleanupDone = false;

    @NotNull
    private static TextMateUserBundlesSettings getBundlesSettings() {
        TextMateUserBundlesSettings settings = TextMateUserBundlesSettings.getInstance();
        assert settings != null;
        return settings;
    }

    private static void logAllCdsBundles(String stage, TextMateUserBundlesSettings settings) {
        var cdsBundles = settings.getBundles().entrySet().stream()
                .filter(entry -> CdsLanguage.LABEL.equals(entry.getValue().getName()))
                .toList();

        Logger.TM_BUNDLE.info("[%s] Found %d CDS bundle(s) in user settings".formatted(stage, cdsBundles.size()));
        cdsBundles.forEach(entry ->
                Logger.TM_BUNDLE.info("[%s]   Path: '%s', enabled: %s".formatted(stage, entry.getKey(), entry.getValue().getEnabled()))
        );
    }

    /**
     * Cleans up legacy CDS bundle entries from settings.
     */
    public void cleanupLegacyBundles() {
        if (cleanupDone) {
            return;
        }
        cleanupDone = true;

        Logger.TM_BUNDLE.info("Checking for legacy CDS bundles in user settings");

        TextMateUserBundlesSettings settings = getBundlesSettings();
        logAllCdsBundles("BEFORE_CLEANUP", settings);

        var cdsBundlePaths = settings.getBundles().entrySet().stream()
                .filter(entry -> CdsLanguage.LABEL.equals(entry.getValue().getName()))
                .map(entry -> entry.getKey())
                .toList();

        if (cdsBundlePaths.isEmpty()) {
            Logger.TM_BUNDLE.info("No legacy CDS bundles found in user settings");
            return;
        }

        Logger.TM_BUNDLE.info("Removing %d legacy CDS bundle(s) from user settings".formatted(cdsBundlePaths.size()));
        cdsBundlePaths.forEach(path -> {
            Logger.TM_BUNDLE.info("Removing legacy CDS bundle at path: '%s'".formatted(path));
            settings.removeBundle(path);
        });

        logAllCdsBundles("AFTER_CLEANUP", settings);
        TextMateServiceImpl.getInstance().reloadEnabledBundles();
    }
}
