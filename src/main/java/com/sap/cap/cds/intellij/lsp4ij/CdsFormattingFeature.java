package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.redhat.devtools.lsp4ij.client.features.LSPFormattingFeature;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettings;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsService;
import com.sap.cap.cds.intellij.settings.AppSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CdsFormattingFeature extends LSPFormattingFeature {

    // Disable server-side onTypeFormatting: requests may arrive before didChange is synced.
    // Use client-side formatting instead (isFormatOnCloseBrace, isFormatOnStatementTerminator).
    @Override
    public boolean isOnTypeFormattingEnabled(@NotNull PsiFile file) {
        return false;
    }

    @Override
    public boolean isExistingFormatterOverrideable(@NotNull PsiFile file) {
        return true;
    }

    @Override
    public boolean isFormatOnCloseBrace(@NotNull PsiFile file) {
        return Objects.requireNonNull(AppSettings.getInstance().getState()).enableOnTypeFormatting;
    }

    @Override
    public @Nullable String getFormatOnCloseBraceCharacters(@NotNull PsiFile file) {
        return "}";
    }

    @Override
    public boolean isFormatOnStatementTerminator(@NotNull PsiFile file) {
        return Objects.requireNonNull(AppSettings.getInstance().getState()).enableOnTypeFormatting;
    }

    // CODE_BLOCK: nearest brace pair. STATEMENT would be ideal but (due to lsp4ij's logic) requires
    // selectionRange to start at column 0, which CDS server doesn't provide (it starts after indentation).
    @Override
    public @NotNull FormattingScope getFormatOnStatementTerminatorScope(@NotNull PsiFile file) {
        return FormattingScope.CODE_BLOCK;
    }

    @Override
    public @Nullable String getFormatOnStatementTerminatorCharacters(@NotNull PsiFile file) {
        return ";";
    }

    @Override
    public @Nullable Integer getTabSize(@NotNull PsiFile file, @Nullable Editor editor) {
        int defaultValue = (int) CdsCodeStyleSettings.OPTIONS.get("tabSize").defaultValue;
        if (editor == null) {
            return defaultValue;
        }
        Project project = editor.getProject();
        if (project == null) {
            return defaultValue;
        }
        CdsCodeStyleSettingsService service = project.getService(CdsCodeStyleSettingsService.class);
        if (service == null) {
            return defaultValue;
        }
        return service.getSettings().tabSize;
    }
}
