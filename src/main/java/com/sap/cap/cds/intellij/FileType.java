package com.sap.cap.cds.intellij;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.TextMateBackedFileType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public final class FileType extends LanguageFileType implements TextMateBackedFileType {

    public static final FileType INSTANCE = new FileType();
    public static final List<String> EXTENSIONS = List.of("cds", "properties");

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
        return EXTENSIONS.get(0);
    }

    @Override
    public Icon getIcon() {
        return Icons.FILE;
    }
}
