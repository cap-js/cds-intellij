package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.redhat.devtools.lsp4ij.client.features.LSPFormattingFeature;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettings;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CdsFormattingFeature extends LSPFormattingFeature {

    @Override
    public boolean isFormatOnCloseBrace(@NotNull PsiFile file) {
        // Q: do we get this automatically when onTypeFormatting is enabled, or do we need to do something special?
        return super.isFormatOnCloseBrace(file);
    }

    @Override
    public @Nullable String getFormatOnCloseBraceCharacters(@NotNull PsiFile file) {
        // Q: '}' ?
        // Q: where to define "the language's default close characters" (that is mentioned in the docs)?
        return super.getFormatOnCloseBraceCharacters(file);
    }

    @Override
    public boolean isFormatOnStatementTerminator(@NotNull PsiFile file) {
        return super.isFormatOnStatementTerminator(file);
    }

    @Override
    public @Nullable String getFormatOnStatementTerminatorCharacters(@NotNull PsiFile file) {
        // Q: ';' ?
        return super.getFormatOnStatementTerminatorCharacters(file);
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
