package com.sap.cap.cds.intellij.codestyle;

import com.intellij.execution.ExecutionException;
import com.intellij.formatting.FormattingRangesInfo;
import com.intellij.formatting.service.FormattingService;
import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsProvider.SAMPLE_FILE_NAME;
import static com.sap.cap.cds.intellij.codestyle.CdsPrettierJsonService.PRETTIER_JSON;
import static com.sap.cap.cds.intellij.lsp.CdsLspServerDescriptor.getFormattingCommandLine;
import static com.sap.cap.cds.intellij.util.FileUtil.createTempDir;
import static java.nio.file.Files.readString;
import static java.nio.file.Files.write;

public class CdsPreviewFormattingService implements FormattingService {

    private static final Map<String, String> formattedByPrettierJson = new HashMap<>();
    private static Path prettierJsonPath;
    private static Path tempDir;
    private static Path samplePath;
    // HOT-TODO initialize from file
    private static String prettierJson = "{}";

    CdsPreviewFormattingService() {
        try {
            tempDir = createTempDir(CdsPreviewFormattingService.class.getName() + "_");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        prettierJsonPath = tempDir.resolve(PRETTIER_JSON);
        try {
            write(prettierJsonPath, "{}".getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        samplePath = tempDir.resolve(SAMPLE_FILE_NAME);
        try {
            write(samplePath, CdsLanguage.SAMPLE_SRC.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void acceptSettings(CdsCodeStyleSettings settings) {
        prettierJson = settings.getNonDefaultSettings().toString();
    }

    @Override
    public @NotNull Set<Feature> getFeatures() {
        return Set.of();
    }

    @Override
    public boolean canFormat(@NotNull PsiFile psiFile) {
        return psiFile.getName().equals(SAMPLE_FILE_NAME);
    }

    @Override
    public @NotNull PsiElement formatElement(@NotNull PsiElement psiElement, @NotNull TextRange textRange, boolean canChangeWhiteSpaceOnly) {
        return this.formatElement(psiElement, canChangeWhiteSpaceOnly);
    }

    @Override
    public @NotNull PsiElement formatElement(@NotNull PsiElement psiElement, boolean canChangeWhiteSpaceOnly) {
        /*
         Accept changed settings from UI while it's still open. NOTE: This is not the best place to do this, but all other obvious methods are unsuitable:
         - CdsCodeStyleMainPanel.onSomethingChanged() is called before the settings are applied (even getApplication().invokeLater() doesn't help)
         - CdsCodeStyleMainPanel.apply*() methods are not called
        */
        Project project = psiElement.getProject(); // may be default project
        CdsCodeStyleSettings settings = project.getService(CdsProjectCodeStyleSettingsService.class).getSettings();
        acceptSettings(settings);

        String src = formattedByPrettierJson.get(prettierJson);
        if (src == null) {
            formattedByPrettierJson.put(prettierJson, src = formatSampleSrc(prettierJson));
        }
        psiElement.getContainingFile().getViewProvider().getDocument().setText(src);
        return psiElement;
    }

    private String formatSampleSrc(String prettierJson) {
        try {
            write(prettierJsonPath, prettierJson.getBytes());
            getFormattingCommandLine(tempDir, samplePath).createProcess().waitFor();
            return readString(samplePath);
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void formatRanges(@NotNull PsiFile psiFile, FormattingRangesInfo formattingRangesInfo, boolean canChangeWhiteSpaceOnly, boolean quickFormat) {
    }

    @Override
    public @NotNull Set<ImportOptimizer> getImportOptimizers(@NotNull PsiFile psiFile) {
        return Set.of();
    }
}
