package com.sap.cap.cds.intellij.codestyle;

import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsService.PRETTIER_JSON;
import static com.sap.cap.cds.intellij.util.Logger.logger;

// TODO handle file deletion as well

public class CdsPrettierJsonListener implements AsyncFileListener {
    @Override
    public @Nullable ChangeApplier prepareChange(@NotNull List<? extends @NotNull VFileEvent> list) {
        list.stream()
                // NOTE this is also triggered by programmatic changes to the file
                .filter(event -> event instanceof VFileContentChangeEvent && ((VFileContentChangeEvent) event).getFile().getName().equals(PRETTIER_JSON))
                .map(event -> getApplication().getService(ProjectLocator.class).guessProjectForFile(event.getFile()))
                .filter(Objects::nonNull)
                .distinct()
                .forEach(project -> {
                    getApplication().invokeLater(() -> {
                        project.getService(CdsCodeStyleSettingsService.class).updateProjectSettingsFromFile();
                    });
                });
        return null;
    }
}
