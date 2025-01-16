package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.testFramework.HeavyPlatformTestCase;
import com.intellij.testFramework.PlatformTestUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.intellij.application.options.CodeStyle.createTestSettings;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettingsService.PRETTIER_JSON;
import static java.util.Objects.requireNonNull;

@SuppressWarnings("NewClassNamingConvention")
public class CdsCodeStyleSettingsServiceTestBase extends HeavyPlatformTestCase {
    protected CdsCodeStyleSettings defaults;
    protected Project project;
    protected File prettierJson;
    private Path projectDir;
    private VirtualFile projectDirVFile;

    protected static void setPerProjectSettings(boolean perProjectSettings) {
        CodeStyleSettingsManager.getInstance().USE_PER_PROJECT_SETTINGS = perProjectSettings;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        defaults = new CdsCodeStyleSettings(createTestSettings());
        File tempDirectory = getTempDirectory();
        projectDir = tempDirectory.toPath();
        projectDirVFile = getVFile(tempDirectory);
        prettierJson = projectDir.resolve(PRETTIER_JSON).toFile();
    }

    @Override
    protected void tearDown() throws Exception {
        if (project != null) {
            PlatformTestUtil.forceCloseProjectWithoutSaving(project);
        }
        super.tearDown();
    }

    private @NotNull File getTempDirectory() throws IOException {
        File tempDirectory = createTempDirectory();
        refreshVfsForDirAndChildren(tempDirectory.toPath());
        return tempDirectory;
    }

    private @NotNull VirtualFile getVFile(File file) {
        return requireNonNull(LocalFileSystem.getInstance().findFileByIoFile(file));
    }

    protected void createPrettierJson() {
        try {
            WriteAction.computeAndWait(() -> projectDirVFile.findOrCreateChildData(this, PRETTIER_JSON));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void deletePrettierJson() throws IOException {
        refreshVfsForFile(prettierJson, false);
        WriteAction.runAndWait(() -> getVFile(prettierJson).delete(this));
    }

    protected void writePrettierJson(String settings) throws IOException {
        if (!prettierJson.exists()) {
            throw new IOException(".cdsprettier.json does not exist");
        }
        refreshVfsForFile(prettierJson, false);
        WriteAction.runAndWait(() -> getVFile(prettierJson).setBinaryContent(settings.getBytes()));
    }

    protected String readPrettierJson() throws IOException {
        if (!prettierJson.exists()) {
            throw new IOException(".cdsprettier.json does not exist");
        }
        refreshVfsForFile(prettierJson, true);
        return new String(requireNonNull(getVFile(prettierJson).contentsToByteArray()));
    }

    protected void refreshVfsForFile(File file, boolean includingContent) {
        VirtualFile vFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
        if (includingContent && vFile != null) {
            vFile.refresh(false, true);
        }
    }

    protected void refreshVfsForDirAndChildren(Path dir) {
        VirtualFile vFile = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(dir);
        if (vFile != null) {
            requireNonNull(vFile).refresh(false, true);
        }
    }

    protected void openProject() {
        project = PlatformTestUtil.loadAndOpenProject(projectDir, getTestRootDisposable());
        WriteAction.runAndWait(() -> {
            Module module = ModuleManager.getInstance(project).newModule(projectDir.resolve("test.iml").toString(), "ffo");
            ModuleRootModificationUtil.addContentRoot(module, projectDir.toString());
        });
    }

    @NotNull
    protected CdsCodeStyleSettings getCdsCodeStyleSettings() {
        return CodeStyle.getSettings(project).getCustomSettings(CdsCodeStyleSettings.class);
    }
}
