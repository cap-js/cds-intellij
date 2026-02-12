package com.sap.cap.cds.intellij.textmate;

import com.sap.cap.cds.intellij.lang.CdsLanguage;
import com.sap.cap.cds.intellij.util.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static com.intellij.util.PathUtil.getJarPathForClass;

public class CdsTextMateBundleProvider implements TextMateBundleProvider {

    @NotNull
    @Override
    public List<PluginBundle> getBundles() {
        Path bundlePath = getBundlePath();

        if (!Files.exists(bundlePath)) {
            Logger.TM_BUNDLE.warn("TextMate bundle path does not exist: '%s'".formatted(bundlePath));
            return Collections.emptyList();
        }

        Logger.TM_BUNDLE.info("Providing TextMate bundle: name='%s', path='%s'".formatted(CdsLanguage.LABEL, bundlePath));
        return List.of(new PluginBundle(CdsLanguage.LABEL, bundlePath));
    }

    @NotNull
    private static Path getBundlePath() {
        Path thisPath = Paths.get(getJarPathForClass(CdsTextMateBundleProvider.class));
        return thisPath
                .getParent()
                .resolve(CdsTextMateBundle.RELATIVE_PATH);
    }
}
