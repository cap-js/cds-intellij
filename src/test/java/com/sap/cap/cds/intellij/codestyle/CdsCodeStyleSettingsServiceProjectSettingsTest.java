package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyle;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.intellij.testFramework.LoggedErrorProcessor.executeAndReturnLoggedError;

public class CdsCodeStyleSettingsServiceProjectSettingsTest extends CdsCodeStyleSettingsServiceTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setPerProjectSettings(true);
    }

    public void testUsesProjectSettings() {
        openProject();
        assertTrue(CodeStyle.usesOwnSettings(project));
    }

    public void testDefaultSettings() {
        openProject();
        assertEquals(defaults.tabSize, getCdsCodeStyleSettings().tabSize);
    }

    // Direction .cdsprettier.json → settings

    public void testPrettierJsonLifecycle() throws IOException {
        openProject();
        createPrettierJson();
        writePrettierJson("{}");
        assertEquals(defaults.tabSize, getCdsCodeStyleSettings().tabSize);

        writePrettierJson("{ \"tabSize\": %d, \"alignAs\": %b }".formatted(42, !defaults.alignAs));
        assertEquals(42, getCdsCodeStyleSettings().tabSize);
        assertEquals(!defaults.alignAs, getCdsCodeStyleSettings().alignAs);

        deletePrettierJson();
        assertEquals(defaults.tabSize, getCdsCodeStyleSettings().tabSize);
    }

    public void testExistentPrettierJson() throws Exception {
        createPrettierJson();
        writePrettierJson("{ tabSize: 42 }");
        openProject();
        assertEquals(42, getCdsCodeStyleSettings().tabSize);
    }

    public void testDoNotReformatExistingPrettierJsonIfNoChanges() throws Exception {
        createPrettierJson();
        String json = "{ \"tabSize\": 19, \"alignAs\": true }";
        writePrettierJson(json);
        openProject();

        CodeStyleSettingsManager.getInstance(project).notifyCodeStyleSettingsChanged();
        assertEquals(json, readPrettierJson());
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

    // Direction settings → .cdsprettier.json

    public void testCdsPrettierJsonCreation() throws IOException {
        openProject();
        assertEquals(defaults.toJSON(), readPrettierJson());
    }

    public void testSettingChanged() throws IOException {
        openProject();
        getCdsCodeStyleSettings().tabSize = 42;
        CodeStyleSettingsManager.getInstance(project).notifyCodeStyleSettingsChanged();

        assertEquals(42, new JSONObject(readPrettierJson()).get("tabSize"));
    }

    public void testCompletesPartialPrettierJson() throws IOException {
        createPrettierJson();
        writePrettierJson("{ tabSize: 1 }");
        openProject();
        CodeStyleSettingsManager.getInstance(project).notifyCodeStyleSettingsChanged();
        assertTrue(readPrettierJson().split("\n").length >= CdsCodeStyleSettings.OPTIONS.size() + 2);
    }

}
