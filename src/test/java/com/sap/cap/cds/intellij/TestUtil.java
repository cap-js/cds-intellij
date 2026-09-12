package com.sap.cap.cds.intellij;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.testFramework.ExpectedHighlightingData;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import com.sap.cap.cds.intellij.lsp4ij.CdsLanguageServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.nio.file.Files.readString;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TestUtil {

    public static void checkDiagnostics(@NotNull CodeInsightTestFixture fixture) {
        try {
            String testDataPath = fixture.getTestDataPath();
            String testFileName = fixture.getFile().getName();
            String expectedFileName = testFileName.replace(".cds", ".expected.cds");
            Path expectedFilePath = Paths.get(testDataPath, expectedFileName);
            String expectedFileContent = readString(expectedFilePath);
            Document expectedDocumentWithMarkup = EditorFactory.getInstance().createDocument(expectedFileContent);
            ExpectedHighlightingData expectation = new ExpectedHighlightingData(expectedDocumentWithMarkup);
            expectation.init();
            
            waitForLspDiagnostics(fixture);
            fixture.doHighlighting();
            ((CodeInsightTestFixtureImpl) fixture).collectAndCheckHighlighting(expectation);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read expected file for diagnostics: " + fixture.getFile().getName(), e);
        }
    }
    
    private static void waitForLspDiagnostics(@NotNull CodeInsightTestFixture fixture) {
        var project = fixture.getProject();
        long deadline = System.currentTimeMillis() + SECONDS.toMillis(60);

        // Block until the server is fully started. getLanguageServer() triggers startup and
        // resolves when the server is ready. With lsp4ij >= 0.21.0, feature support uses
        // awaitWithCheckCanceled (no safety timeout), so doHighlighting() must not be called
        // until the server is ready to respond to LSP requests.
        try {
            LanguageServerManager.getInstance(project)
                    .getLanguageServer(CdsLanguageServer.ID)
                    .get(60, SECONDS);
        } catch (Exception e) {
            // Server did not start in time; proceed — test will likely fail with no highlights.
        }

        // Poll for diagnostics to arrive (server may still be indexing after started).
        // Wrap doHighlighting() with a cancellable ProgressIndicator so that lsp4ij's
        // awaitWithCheckCanceled can bail out if an LSP response hasn't arrived yet,
        // allowing us to retry rather than blocking indefinitely.
        ScheduledExecutorService cancelScheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            while (System.currentTimeMillis() < deadline) {
                if (hasErrorDiagnosticsWithTimeout(fixture, cancelScheduler, 2000)) {
                    return;
                }
                sleep(250);
            }
        } finally {
            cancelScheduler.shutdownNow();
        }
    }

    private static boolean hasErrorDiagnosticsWithTimeout(@NotNull CodeInsightTestFixture fixture,
                                                           @NotNull ScheduledExecutorService cancelScheduler,
                                                           long timeoutMs) {
        EmptyProgressIndicator indicator = new EmptyProgressIndicator();
        // Schedule cancellation after timeout so awaitWithCheckCanceled doesn't block forever
        // if the LSP server hasn't responded yet.
        var cancelTask = cancelScheduler.schedule(indicator::cancel, timeoutMs, TimeUnit.MILLISECONDS);
        try {
            List<HighlightInfo> highlights = ProgressManager.getInstance()
                    .runProcess(fixture::doHighlighting, indicator);
            return highlights != null && highlights.stream()
                    .anyMatch(info -> info.getSeverity() == HighlightSeverity.ERROR);
        } catch (Exception e) {
            return false;
        } finally {
            cancelTask.cancel(false);
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
