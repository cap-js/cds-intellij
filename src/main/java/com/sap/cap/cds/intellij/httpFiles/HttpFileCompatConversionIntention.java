package com.sap.cap.cds.intellij.httpFiles;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class HttpFileCompatConversionIntention extends BaseIntentionAction {

    private static final Pattern PATTERN = Pattern.compile("(?<=\\{\\{username}}):(?=\\{\\{password}})");

    @NotNull
    @Override
    public String getText() {
        return "Make compatible with Intellij HTTP client";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile) {
        if (psiFile == null) {
            return false;
        }
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null || !"http".equalsIgnoreCase(virtualFile.getExtension())) {
            return false;
        }
        return PATTERN.matcher(editor.getDocument().getText()).find();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        final Document document = editor.getDocument();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.setText(replaceAuthSeparator(document.getText()));
        });
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "HTTP file actions";
    }

    @Override
    public @NotNull IntentionPreviewInfo generatePreview(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        String text = editor.getDocument().getText();
        return new IntentionPreviewInfo.CustomDiff(PlainTextFileType.INSTANCE, file.getName(), text, replaceAuthSeparator(text));
    }

    private static String replaceAuthSeparator(String text) {
        return PATTERN.matcher(text).replaceAll(" ");
    }
}