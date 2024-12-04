package com.sap.cap.cds.intellij.codestyle;

import com.intellij.execution.ExecutionException;
import com.intellij.formatting.FormattingRangesInfo;
import com.intellij.formatting.service.FormattingService;
import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import com.sap.cap.cds.intellij.lsp.CdsLspServerDescriptor;
import com.sap.cap.cds.intellij.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsProvider.SAMPLE_FILE_NAME;
import static java.nio.file.Files.readString;
import static java.nio.file.Files.write;

public class CdsFormattingService implements FormattingService {

    private static Path cdsPrettierJsonPath;
    private final Path tempDir;
    private final Path samplePath;

    CdsFormattingService() {
        try {
            tempDir = FileUtil.createTempDir("cds-formatting-options-");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        cdsPrettierJsonPath = tempDir.resolve(".cdsprettier.json");

        try {
            write(cdsPrettierJsonPath, "{}".getBytes());
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
        if (cdsPrettierJsonPath == null) {
            return;
        }
        JSONObject cdsPrettierJson = new JSONObject(settings.getNonDefaultSettings());
        try {
            write(cdsPrettierJsonPath, cdsPrettierJson.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    public @NotNull PsiElement formatElement(@NotNull PsiElement psiElement, boolean canChangeWhiteSpaceOnly) {
        try {
            CdsLspServerDescriptor.getFormattingCommandLine(tempDir, samplePath).createProcess().waitFor();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        String formatted;
        try {
            formatted = readString(samplePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        psiElement.getContainingFile().getViewProvider().getDocument().setText(formatted);

        return psiElement;
    }

    @Override
    public @NotNull PsiElement formatElement(@NotNull PsiElement psiElement, @NotNull TextRange textRange, boolean canChangeWhiteSpaceOnly) {
        return this.formatElement(psiElement, canChangeWhiteSpaceOnly);
    }

    @Override
    public void formatRanges(@NotNull PsiFile psiFile, FormattingRangesInfo formattingRangesInfo, boolean canChangeWhiteSpaceOnly, boolean quickFormat) {
    }

    @Override
    public @NotNull Set<ImportOptimizer> getImportOptimizers(@NotNull PsiFile psiFile) {
        return Set.of();
    }
}
