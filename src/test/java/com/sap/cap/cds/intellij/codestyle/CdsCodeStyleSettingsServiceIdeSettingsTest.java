package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyle;

import java.io.IOException;

public class CdsCodeStyleSettingsServiceIdeSettingsTest extends CdsCodeStyleSettingsServiceTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setPerProjectSettings(false); // → CodeStyle.getSettings(project) will return "Default" (IDE) code-style settings via CodeStyleSchemes.getInstance().findPreferredScheme()
    }

    public void testDefaultSettings() {
        openProject();
        assertEquals(defaults.tabSize, getCdsCodeStyleSettings().tabSize);
    }

    public void testUsesIdeSettings() {
        openProject();
        assertFalse(CodeStyle.usesOwnSettings(project));
    }

    // Direction .cdsprettier.json → settings

    public void testPrettierJsonLifecycle() throws IOException, InterruptedException {
        openProject();
        createPrettierJson();
        writePrettierJson("{}");
        assertEquals(defaults.tabSize, getCdsCodeStyleSettings().tabSize);
        assertFalse(CodeStyle.usesOwnSettings(project));

        writePrettierJson("{ tabSize: 42 }");
        assertEquals(42, getCdsCodeStyleSettings().tabSize);
        assertTrue(CodeStyle.usesOwnSettings(project));

        deletePrettierJson();
        assertEquals(defaults.tabSize, getCdsCodeStyleSettings().tabSize);
        assertTrue(CodeStyle.usesOwnSettings(project));
    }

    public void testPrettierJsonDeletedFirst() throws IOException, InterruptedException {
        createPrettierJson();
        writePrettierJson("{}");
        openProject();
        assertFalse(CodeStyle.usesOwnSettings(project));

        deletePrettierJson();
        assertEquals(defaults.tabSize, getCdsCodeStyleSettings().tabSize);
        assertFalse(CodeStyle.usesOwnSettings(project));
    }

    public void testExistentPrettierJsonWithSameSettings() throws IOException, InterruptedException {
        createPrettierJson();
        writePrettierJson("{}");
        openProject();
        assertFalse(CodeStyle.usesOwnSettings(project));
    }

    public void testExistentPrettierJsonWithDifferingSettings() throws IOException, InterruptedException {
        createPrettierJson();
        writePrettierJson("{ tabSize: 42 }");
        openProject();
        assertEquals(42, getCdsCodeStyleSettings().tabSize);
        assertTrue(CodeStyle.usesOwnSettings(project));
    }

    // TODO Direction settings → .cdsprettier.json

}
