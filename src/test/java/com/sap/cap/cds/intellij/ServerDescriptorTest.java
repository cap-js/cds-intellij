package com.sap.cap.cds.intellij;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.testFramework.fixtures.TempDirTestFixture;
import com.sap.cap.cds.intellij.lsp.ServerDescriptor;

import java.io.*;
import java.util.regex.Pattern;

import static org.junit.Assert.assertNotEquals;

/*
 * TODO try to write model-level functional tests instead of unit tests (cf. https://plugins.jetbrains.com/docs/intellij/testing-plugins.html)
 */

public class ServerDescriptorTest extends BasePlatformTestCase {
    public void testCommandLine() {
        Process process = null;
        try {
            process = new ServerDescriptor(getProject(), "name")
                    .getCommandLine()
                    .createProcess();
        } catch (ExecutionException e) {
            assertNull("unexpected exception", e.getMessage());
        }
        assertNotNull(process);

        String[] args = process.info().arguments().get();
        String cdsLspPath = args[0];
        assertTrue(cdsLspPath.contains("/dist/"));
    }

    public void testDebugCommandLine() throws IOException, InterruptedException {
        // Enable debug logging
        System.setProperty("DEBUG", "cds-lsp");

        Process process = null;
        GeneralCommandLine commandLine = null;
        try {
            commandLine = new ServerDescriptor(getProject(), "name").getCommandLine();
        } catch (ExecutionException e) {
            assertNull("unexpected exception", e.getMessage());
        }
        assertNotNull(commandLine);

        // It should be the MITM script
        String[] args = commandLine.getParametersList().getArray();
        String mitmPath = args[0];
        assertTrue(mitmPath.contains("mitm"));
        String logPath = args[1];
        new File(logPath).delete();

        try {
            process = commandLine.createProcess();
        } catch (ExecutionException e) {
            assertNull("unexpected exception", e.getMessage());
        }
        assertNotNull(process);
        assertEquals(0, process.getErrorStream().available());

        // Man-in-the-middle should log stdin data
        String data = "FOO INPUT 01234567890123456789";
        BufferedWriter mitmStdin = process.outputWriter();
        mitmStdin.write(data);
        mitmStdin.flush();
        Thread.sleep(500);
        String logged = new String(new FileInputStream(logPath).readAllBytes()).replaceAll("\\d{4}-[\\dTZ:.-]+", "NOW");
        assertTrue(Pattern.compile("> NOW.*" + data).matcher(logged).find());

        assertTrue(process.isAlive());

        System.clearProperty("DEBUG");
    }

    // TODO test real initialization of server

    public void testIsSupportedFile() throws IOException {
        TempDirTestFixture fixture = this.createTempDirTestFixture();
        ServerDescriptor serverDescriptor = new ServerDescriptor(getProject(), "name");
        assertTrue(serverDescriptor.isSupportedFile(fixture.createFile("a.cds")));
        assertFalse(serverDescriptor.isSupportedFile(fixture.createFile("a.txt")));
    }
}
