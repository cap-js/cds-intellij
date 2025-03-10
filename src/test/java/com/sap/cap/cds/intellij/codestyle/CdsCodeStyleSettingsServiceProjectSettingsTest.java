package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.intellij.testFramework.LoggedErrorProcessor.executeAndReturnLoggedError;
import static com.sap.cap.cds.intellij.util.JsonUtil.isJsonEqual;

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

    // TODO test project open with non-project settings → no .cdsprettier.json

    public void testProjectOpenedWithDefaultSettings_noCdsPrettierJsonCreation() throws IOException {
        openProject();
        assertFalse(prettierJson.exists());
    }

    // HOT-TODO(reenable): failing due to file is not seen as open in second notify call
//    public void testSettingChanged_createsPrettierJsonOnlyIfCdsEditorOpen() throws IOException {
//        CodeStyleSettingsManager codeStyleSettingsManager = CodeStyleSettingsManager.getInstance(project);
//
//        openProject();
//        getCdsCodeStyleSettings().tabSize = 42;
//        codeStyleSettingsManager.notifyCodeStyleSettingsChanged();
//        assertFalse(prettierJson.exists());
//
//        createCdsFile();
//        var editors = openCdsFile();
//        assertEquals(1, editors.length);
//        var state = editors[0].getState(FileEditorStateLevel.FULL);
//        getCdsCodeStyleSettings().tabSize = 23;
//        codeStyleSettingsManager.notifyCodeStyleSettingsChanged();
//        assertTrue(prettierJson.exists());
//        assertEquals(42, new JSONObject(readPrettierJson()).get("tabSize"));
//    }

    public void testSettingChangedWithExistingCdsPrettierJson_updatesFile() throws IOException {
        openProject();
        createPrettierJson();
        writePrettierJson("{}");
        getCdsCodeStyleSettings().tabSize = 42;
        CodeStyleSettingsManager.getInstance(project).notifyCodeStyleSettingsChanged();
        assertEquals(42, new JSONObject(readPrettierJson()).get("tabSize"));
    }

    public void testWriteOnlyLoadedOrNonDefaultOptions() throws Exception {
        createPrettierJson();
        writePrettierJson("{ \"tabSize\": %d, \"alignAs\": %b }".formatted(defaults.tabSize, !defaults.alignAs));
        openProject();
        assertEquals(defaults.tabSize, getCdsCodeStyleSettings().tabSize);
        assertEquals(!defaults.alignAs, getCdsCodeStyleSettings().alignAs);

        getCdsCodeStyleSettings().alignTypes = !defaults.alignTypes;
        CodeStyleSettingsManager.getInstance(project).notifyCodeStyleSettingsChanged();
        String expected = "{ \"tabSize\": %d, \"alignAs\": %b, \"alignTypes\": %b }".formatted(defaults.tabSize, !defaults.alignAs, !defaults.alignTypes);
        assertTrue(isJsonEqual(expected, readPrettierJson()));
    }

    public void testNoChanges_doNotReformatExistingPrettierJson() throws Exception {
        createPrettierJson();
        String json = "{ \"tabSize\": 19, \"alignAs\": true }";
        writePrettierJson(json);
        openProject();

        CodeStyleSettingsManager.getInstance(project).notifyCodeStyleSettingsChanged();
        assertEquals(json, readPrettierJson());
    }

}
