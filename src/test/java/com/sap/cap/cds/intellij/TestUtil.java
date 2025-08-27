package com.sap.cap.cds.intellij;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.testFramework.ExpectedHighlightingData;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.readString;

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
            fixture.doHighlighting();
            ((CodeInsightTestFixtureImpl) fixture).collectAndCheckHighlighting(expectation);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read expected file for diagnostics: " + fixture.getFile().getName(), e);
        }
    }
}
