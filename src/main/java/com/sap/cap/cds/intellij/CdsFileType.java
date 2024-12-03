package com.sap.cap.cds.intellij;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import com.sap.cap.cds.intellij.lang.CdsLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.TextMateBackedFileType;

import javax.swing.*;

public final class CdsFileType extends LanguageFileType implements TextMateBackedFileType {

    public static final CdsFileType INSTANCE = new CdsFileType();
    public static final String EXTENSION = "cds";

    private CdsFileType() {
        super(CdsLanguage.INSTANCE);
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
        return CdsIcons.FILE;
    }
}
