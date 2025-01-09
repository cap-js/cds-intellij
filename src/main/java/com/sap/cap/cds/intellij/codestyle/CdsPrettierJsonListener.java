package com.sap.cap.cds.intellij.codestyle;

import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static com.intellij.openapi.vfs.VfsUtil.collectChildrenRecursively;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsService.PRETTIER_JSON;

public class CdsPrettierJsonListener implements AsyncFileListener {
    private static void handle(Stream<? extends @NotNull VFileEvent> stream) {
        ProjectLocator projectLocator = getApplication().getService(ProjectLocator.class);
        stream
                .flatMap(event -> event instanceof VFileCreateEvent e && e.getFile() != null && e.getFile().isDirectory()
                        ? collectChildrenRecursively(e.getFile()).stream()
                        : Stream.of(event.getFile()))
                .filter(Objects::nonNull)
                .filter(file -> file.getName().equals(PRETTIER_JSON))
                .map(projectLocator::guessProjectForFile)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(project -> {
                    CdsCodeStyleSettingsService service = project.getService(CdsCodeStyleSettingsService.class);
                    if (service.isSettingsReallyChanged()) {
                        service.updateProjectSettingsFromFile();
                    }
                });
    }

    @Override
    public @Nullable ChangeApplier prepareChange(@NotNull List<? extends @NotNull VFileEvent> list) {
        return new ChangeApplier() {
            @Override
            public void beforeVfsChange() {
                handle(list.stream().filter(event -> event instanceof VFileDeleteEvent));
            }

            @Override
            public void afterVfsChange() {
                handle(list.stream().filter(event -> !(event instanceof VFileDeleteEvent)));
            }
        };
    }
}
