package com.sap.cap.cds.intellij.codestyle;

import org.json.JSONException;

import java.io.IOException;

import static com.intellij.testFramework.LoggedErrorProcessor.executeAndReturnLoggedError;

public class CdsCodeStyleSettingsServiceProjectSettingsTest extends CdsCodeStyleSettingsServiceTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setPerProjectSettings(true);
    }

    // TODO test other direction

    // Direction .cdsprettier.json → settings

    public void testPrettierJsonLifecycle() throws IOException, InterruptedException {
        openProject();
        createPrettierJson();

        writePrettierJson("{}");
        assertEquals(defaults.tabSize, getCdsCodeStyleSettings().tabSize);

        writePrettierJson("{ tabSize: 42 }");
        assertEquals(42, getCdsCodeStyleSettings().tabSize);

        deletePrettierJson();
        assertEquals(defaults.tabSize, getCdsCodeStyleSettings().tabSize);
    }

    public void testExistentPrettierJson() throws Exception {
        createPrettierJson();
        writePrettierJson("{ tabSize: 42 }");
        openProject();
        assertEquals(42, getCdsCodeStyleSettings().tabSize);
    }

    public void testInvalidPrettierJson() {
        Throwable exception = executeAndReturnLoggedError(() -> {
            createPrettierJson();
            try {
                writePrettierJson("invalid JSON");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            openProject();
        });
        assertInstanceOf(exception, JSONException.class);
        assertEquals(defaults.tabSize, getCdsCodeStyleSettings().tabSize);
    }

}
