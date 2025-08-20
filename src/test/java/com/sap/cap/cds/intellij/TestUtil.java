package com.sap.cap.cds.intellij;

import com.intellij.openapi.editor.Document;
import com.intellij.testFramework.ExpectedHighlightingData;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;

public class TestUtil {
    public static void checkDiagnostics(CodeInsightTestFixture fixture) {
        Document document = fixture.getEditor().getDocument();
        ExpectedHighlightingData expectation = new ExpectedHighlightingData(document);
        expectation.init();
        checkDiagnostics((CodeInsightTestFixtureImpl) fixture, expectation);
    }

    public static void checkDiagnostics(CodeInsightTestFixtureImpl fixture, ExpectedHighlightingData expectation) {
        // TODO: re-enable: see https://github.com/redhat-developer/lsp4ij/issues/949
        fixture.doHighlighting();
        fixture.collectAndCheckHighlighting(expectation);
    }
}
