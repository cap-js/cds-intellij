package com.sap.cap.cds.intellij;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.textmate.language.syntax.highlighting.TextMateSyntaxHighlighterFactory;

import static com.sap.cap.cds.intellij.lang.CdsLanguage.SAMPLE_SRC;

// TODO remove? currently useless

public class CdsSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    @Override
    public @NotNull SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        return new TextMateSyntaxHighlighterFactory().getSyntaxHighlighter(project, new LightVirtualFile("/sample.cds", CdsFileType.INSTANCE, SAMPLE_SRC));
    }
}