package com.sap.cap.cds.intellij;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.TextMateBackedFileType;

import javax.swing.*;

public final class FileType extends LanguageFileType implements TextMateBackedFileType {

    public static final FileType INSTANCE = new FileType();
    public static final String EXTENSION = "cds";

    private FileType() {
        super(Language.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "cds";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "CDS file";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return EXTENSION;
    }

    @Override
    public Icon getIcon() {
        return Icons.FILE;
    }
}
