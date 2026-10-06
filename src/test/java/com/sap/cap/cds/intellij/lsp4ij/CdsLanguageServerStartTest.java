package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.testFramework.LightPlatformTestCase;
import com.redhat.devtools.lsp4ij.server.CannotStartProcessException;

public class CdsLanguageServerStartTest extends LightPlatformTestCase {

    public void testStartRejectsBlankInterpreter() {
        CdsLanguageServer server = new CdsLanguageServer(getProject(), () -> new GeneralCommandLine(""));
        CannotStartProcessException thrown = null;
        try {
            server.start();
        } catch (CannotStartProcessException e) {
            thrown = e;
        }
        assertNotNull("start() must throw instead of launching a blank executable", thrown);
        String message = thrown.getMessage();
        assertNotNull("exception must carry a message", message);
        assertTrue(
                "guard must report the missing interpreter, got: " + message,
                message.contains("No suitable Node.js interpreter"));
    }
}
