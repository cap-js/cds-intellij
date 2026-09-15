package com.sap.cap.cds.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.redhat.devtools.lsp4ij.LSPIJUtils;
import com.redhat.devtools.lsp4ij.LanguageServerItem;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import com.redhat.devtools.lsp4ij.OpenedDocument;
import com.sap.cap.cds.intellij.lsp4ij.CdsLanguageServer;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
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

            List<Diagnostic> errors = awaitErrorDiagnostics(fixture, expectedCount);
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

    private static List<Diagnostic> awaitErrorDiagnostics(@NotNull CodeInsightTestFixture fixture, int expectedCount) {
        LanguageServerItem server = startLanguageServer(fixture.getProject());
        VirtualFile file = fixture.getFile().getVirtualFile();

        long deadline = System.currentTimeMillis() + SECONDS.toMillis(60);
        List<Diagnostic> errors = readErrorDiagnostics(server, file);
        while (errors.size() < expectedCount && System.currentTimeMillis() < deadline) {
            sleep(500);
            errors = readErrorDiagnostics(server, file);
        }
        // settle: let late or duplicate diagnostic publishes surface before asserting the count
        sleep(1000);
        return readErrorDiagnostics(server, file);
    }

    private static LanguageServerItem startLanguageServer(@NotNull Project project) {
        try {
            LanguageServerManager manager = LanguageServerManager.getInstance(project);
            manager.start(CdsLanguageServer.ID, new LanguageServerManager.StartOptions().setForceStart(true));
            return manager.getLanguageServer(CdsLanguageServer.ID).get(60, SECONDS);
        } catch (Exception e) {
            throw new AssertionError("Language server did not start within 60 seconds", e);
        }
    }

    // read the diagnostics lsp4ij holds for the file, bypassing doHighlighting() which blocks the EDT under lsp4ij 0.21.0
    private static List<Diagnostic> readErrorDiagnostics(@NotNull LanguageServerItem server, @NotNull VirtualFile file) {
        OpenedDocument document = server.getServerWrapper().getOpenedDocument(LSPIJUtils.toUri(file));
        if (document == null) {
            return List.of();
        }
        return document.getDiagnostics().stream()
                .filter(diagnostic -> diagnostic.getSeverity() == DiagnosticSeverity.Error)
                .toList();
    }

    private static void assertMatches(int expectedCount, @NotNull List<String> expectedDescriptions, @NotNull List<Diagnostic> errors) {
        List<String> actualDescriptions = errors.stream().map(Diagnostic::getMessage).toList();
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

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
