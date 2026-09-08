package com.sap.cap.cds.intellij;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.testFramework.ExpectedHighlightingData;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import com.redhat.devtools.lsp4ij.ServerStatus;
import com.sap.cap.cds.intellij.lsp4ij.CdsLanguageServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

        // Wait for the server to reach 'started' status before calling doHighlighting().
        // With lsp4ij >= 0.21.0 the feature support uses awaitWithCheckCanceled (no safety
        // timeout), so triggering highlighting before the server is ready causes an indefinite
        // hang in the test JVM.
        while (System.currentTimeMillis() < deadline) {
            ServerStatus status = LanguageServerManager.getInstance(project)
                    .getServerStatus(CdsLanguageServer.ID);
            if (status == ServerStatus.started) {
                break;
            }
            sleep(100);
        }

        // Poll for diagnostics to arrive (server may still be indexing after started).
        while (System.currentTimeMillis() < deadline) {
            if (hasErrorDiagnostics(fixture)) {
                return;
            }
            sleep(250);
        }
    }

    private static boolean hasErrorDiagnostics(@NotNull CodeInsightTestFixture fixture) {
        List<HighlightInfo> highlights = fixture.doHighlighting();
        return highlights.stream()
            .anyMatch(info -> info.getSeverity() == HighlightSeverity.ERROR);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
