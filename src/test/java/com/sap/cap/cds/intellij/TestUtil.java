package com.sap.cap.cds.intellij;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import com.sap.cap.cds.intellij.lsp4ij.CdsLanguageServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import static java.nio.file.Files.readString;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TestUtil {

    private static final Pattern EXPECTED_ERROR = Pattern.compile("<error(?: descr=\"(.*?)\")?>(.*?)</error>", Pattern.DOTALL);

    public static void checkDiagnostics(@NotNull CodeInsightTestFixture fixture) {
        try {
            String expectedFileName = fixture.getFile().getName().replace(".cds", ".expected.cds");
            String expectedContent = readString(Paths.get(fixture.getTestDataPath(), expectedFileName));
            int expectedCount = countExpectedErrors(expectedContent);
            List<String> expectedDescriptions = extractExpectedErrorDescriptions(expectedContent);

            List<HighlightInfo> errors = awaitErrorDiagnostics(fixture, expectedCount);
            assertMatches(expectedCount, expectedDescriptions, errors);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read expected file for diagnostics: " + fixture.getFile().getName(), e);
        }
    }

    private static int countExpectedErrors(@NotNull String expectedContent) {
        return (int) EXPECTED_ERROR.matcher(expectedContent).results().count();
    }

    private static List<String> extractExpectedErrorDescriptions(@NotNull String expectedContent) {
        return EXPECTED_ERROR.matcher(expectedContent).results()
                .map(result -> result.group(1))
                .filter(description -> description != null)
                .sorted()
                .toList();
    }

    private static List<HighlightInfo> awaitErrorDiagnostics(@NotNull CodeInsightTestFixture fixture, int expectedCount) {
        Project project = fixture.getProject();
        PsiFile file = fixture.getFile();
        Document document = fixture.getEditor().getDocument();

        startLanguageServer(project);
        // write the diagnostics into the markup (not using doHighlighting(), which blocks the EDT under lsp4ij 0.21.0)
        restartDaemon(project, file);

        long deadline = System.currentTimeMillis() + SECONDS.toMillis(60);
        List<HighlightInfo> errors = readErrorDiagnostics(project, document);
        while (errors.size() < expectedCount && System.currentTimeMillis() < deadline) {
            sleep(500);
            errors = readErrorDiagnostics(project, document);
        }
        // wait to allow late or duplicate diagnostics to arrive
        sleep(1000);
        return readErrorDiagnostics(project, document);
    }

    private static void startLanguageServer(@NotNull Project project) {
        try {
            LanguageServerManager manager = LanguageServerManager.getInstance(project);
            manager.start(CdsLanguageServer.ID, new LanguageServerManager.StartOptions().setForceStart(true));
            manager.getLanguageServer(CdsLanguageServer.ID).get(60, SECONDS);
        } catch (Exception e) {
            throw new AssertionError("Language server did not start within 60 seconds", e);
        }
    }

    private static void restartDaemon(@NotNull Project project, @NotNull PsiFile file) {
        ApplicationManager.getApplication().invokeAndWait(() ->
                DaemonCodeAnalyzer.getInstance(project).restart(file));
    }

    private static List<HighlightInfo> readErrorDiagnostics(@NotNull Project project, @NotNull Document document) {
        return ReadAction.compute(() -> DaemonCodeAnalyzerImpl.getHighlights(document, HighlightSeverity.ERROR, project));
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void assertMatches(int expectedCount, @NotNull List<String> expectedDescriptions, @NotNull List<HighlightInfo> errors) {
        List<String> actualDescriptions = errors.stream()
                .map(HighlightInfo::getDescription)
                .toList();
        if (errors.size() != expectedCount) {
            throw new AssertionError("Expected %d error diagnostics but got %d: %s"
                    .formatted(expectedCount, errors.size(), actualDescriptions));
        }
        for (String expected : expectedDescriptions) {
            if (!actualDescriptions.contains(expected)) {
                throw new AssertionError("Missing expected error diagnostic \"%s\" in %s"
                        .formatted(expected, actualDescriptions));
            }
        }
    }
}
