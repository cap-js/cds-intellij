package com.sap.cap.cds.intellij;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.testFramework.ExpectedHighlightingData;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import com.sap.cap.cds.intellij.lsp4ij.CdsLanguageServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.readString;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
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
        boolean serverReady = false;
        long deadline = System.currentTimeMillis() + SECONDS.toMillis(10);
        var project = fixture.getProject();
        
        while (System.currentTimeMillis() < deadline && !serverReady) {
            try {
                var serverFuture = LanguageServerManager.getInstance(project)
                    .getLanguageServer(CdsLanguageServer.ID);
                if (serverFuture.get(100, MILLISECONDS) != null) {
                    serverReady = true;
                    // Allow for diagnostics to be sent and processed
                    Thread.sleep(500);
                    return;
                }
                Thread.sleep(100);
            } catch (Exception e) {
                // Ignore and retry
            }
        }
    }
}
