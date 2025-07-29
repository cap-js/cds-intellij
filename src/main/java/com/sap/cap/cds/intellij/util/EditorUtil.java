package com.sap.cap.cds.intellij.util;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.sap.cap.cds.intellij.lspServer.UserError;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class EditorUtil {

    public static void openFileInEditor(@NotNull Project project, @NotNull File file) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
        if (virtualFile != null) {
            FileEditorManager.getInstance(project).openFile(virtualFile, true);
        } else {
            UserError.show("Cannot open file: " + file.getAbsolutePath());
        }
    }

}
