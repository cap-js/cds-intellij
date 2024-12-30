package com.sap.cap.cds.intellij;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.lsp.api.LspServer;
import com.intellij.platform.lsp.api.LspServerManager;
import com.intellij.platform.lsp.api.LspServerManagerListener;
import com.intellij.testFramework.ExpectedHighlightingData;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.ComparisonFailure;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.intellij.platform.lsp.api.LspServerState.ShutdownNormally;
import static com.intellij.platform.lsp.api.LspServerState.ShutdownUnexpectedly;

public class TestUtil {
    public static void checkDiagnostics(CodeInsightTestFixture fixture) {
        Document document = fixture.getEditor().getDocument();
        ExpectedHighlightingData expectation = new ExpectedHighlightingData(document);
        expectation.init();
        checkDiagnostics((CodeInsightTestFixtureImpl) fixture, expectation);
    }

    public static void checkDiagnostics(CodeInsightTestFixtureImpl fixture, ExpectedHighlightingData expectation) {
        waitForDiagnosticsFromLspServer(fixture.getProject(), fixture.getFile().getVirtualFile(), 10);
        checkExpectedHighlightingData(fixture, expectation, 1);
    }


    public static void waitForDiagnosticsFromLspServer(@NotNull Project project, @NotNull VirtualFile vFile, int timeoutSec) {
        Disposable disposable = Disposer.newDisposable();

        try {
            AtomicBoolean diagnosticsReceived = new AtomicBoolean();
            AtomicBoolean serverShutdown = new AtomicBoolean();

            LspServerManager.getInstance(project).addLspServerManagerListener(new LspServerManagerListener() {
                @Override
                public void serverStateChanged(@NotNull LspServer lspServer) {
                    if (lspServer.getState() == ShutdownNormally || lspServer.getState() == ShutdownUnexpectedly) {
                        serverShutdown.set(true);
                    }
                }

                @Override
                public void diagnosticsReceived(@NotNull LspServer lspServer, @NotNull VirtualFile file) {
                    if (file.equals(vFile)) {
                        diagnosticsReceived.set(true);
                    }
                }
            }, disposable, true);

            PlatformTestUtil.waitWithEventsDispatching(
                    "Diagnostics from server for file %s not received in %d seconds".formatted(vFile.getName(), timeoutSec),
                    () -> diagnosticsReceived.get() || serverShutdown.get(), timeoutSec
            );

            if (serverShutdown.get()) {
                throw new AssertionError("LSP server initialization failed");
            }
        } finally {
            Disposer.dispose(disposable);
        }
    }

    private static void checkExpectedHighlightingData(@NotNull CodeInsightTestFixtureImpl fixture,
                                                      @NotNull ExpectedHighlightingData expectation,
                                                      int attempt) {
        final int maxAttempts = 3;

        try {
            fixture.collectAndCheckHighlighting(expectation);
        } catch (ComparisonFailure cf) {
            if (attempt >= maxAttempts) {
                throw cf;
            }

            try {
                waitForDiagnosticsFromLspServer(fixture.getProject(), fixture.getFile().getVirtualFile(), 5);
            } catch (AssertionError e) {
                throw cf;
            }

            checkExpectedHighlightingData(fixture, expectation, attempt + 1);
        }
    }
}
