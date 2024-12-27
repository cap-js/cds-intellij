package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyle;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.testFramework.LightPlatformTestCase;
import com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category;
import com.sap.cap.cds.intellij.lang.CdsLanguage;

import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category.SPACES;

public class CdsCodeStyleCheckboxesPanelTest extends LightPlatformTestCase {

    private final Category category = SPACES;
    private CdsCodeStyleCheckboxesPanel panel;

    @Override
    protected void tearDown() throws Exception {
        panel.dispose();
        super.tearDown();
    }

    public void testCdsCodeStyleCheckboxesPanelProperties() {
        CodeStyleSettings settings = CodeStyle.createTestSettings();
        panel = new CdsCodeStyleCheckboxesPanel(settings, category);

        assertEquals(category, panel.getCategory());
        assertEquals(CdsLanguage.INSTANCE, panel.getDefaultLanguage());
        assertEquals(CdsCodeStyleSettings.SAMPLE_SRC, panel.getPreviewText());
        assertEquals(category.getSettingsType(), panel.getSettingsType());
        assertEquals(category.getTitle(), panel.getTabTitle());
    }
}
