package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyle;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.sap.cap.cds.intellij.lang.CdsLanguage;

public class CdsCodeStyleMainPanelTest extends BasePlatformTestCase {

    CdsCodeStyleMainPanel panel;

    @Override
    protected void tearDown() throws Exception {
        panel.dispose();
        super.tearDown();
    }

    public void testCdsCodeStyleMainPanelProperties() {
        CodeStyleSettings settings = CodeStyle.createTestSettings();
        CodeStyleSettings currentSettings = CodeStyle.createTestSettings();
        panel = new CdsCodeStyleMainPanel(currentSettings, settings);

        assertEquals(CdsLanguage.INSTANCE, panel.getDefaultLanguage());
        assertEquals(CdsCodeStyleSettings.SAMPLE_SRC, panel.getPreviewText());
    }

}
