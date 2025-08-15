package com.sap.cap.cds.intellij.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.sap.cap.cds.intellij.util.LoggerScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static com.intellij.openapi.vfs.VfsUtil.collectChildrenRecursively;
import static com.sap.cap.cds.intellij.util.Logger.logger;

public abstract class JsonSettingsFileListener implements AsyncFileListener {

    protected abstract Predicate<VirtualFile> fileFilter();
    protected abstract void handleFileChange(Project project);
    protected abstract LoggerScope getLoggerScope();
    protected abstract String getDebugMessage();

    private void handle(Stream<? extends @NotNull VFileEvent> stream) {
        ProjectLocator projectLocator = getApplication().getService(ProjectLocator.class);
        stream
                .flatMap(event -> event instanceof VFileCreateEvent e && e.getFile() != null && e.getFile().isDirectory()
                        ? collectChildrenRecursively(e.getFile()).stream()
                        : Stream.of(event.getFile()))
                .filter(Objects::nonNull)
                .filter(fileFilter())
                .map(projectLocator::guessProjectForFile)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(project -> {
                    logger(project, getLoggerScope()).debug(getDebugMessage());
                    handleFileChange(project);
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
