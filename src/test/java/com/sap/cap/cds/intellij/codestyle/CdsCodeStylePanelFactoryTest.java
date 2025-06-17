package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.testFramework.LightPlatformTestCase;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.application.options.CodeStyle.createTestSettings;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category.ALIGNMENT;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category.WRAPPING_AND_BRACES;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Type.BOOLEAN;
import static org.junit.Assume.assumeTrue;

public class CdsCodeStylePanelFactoryTest extends LightPlatformTestCase {

    List<CodeStyleAbstractPanel> panels = new ArrayList<>();

    @Override
    protected void tearDown() throws Exception {
        panels.forEach(CodeStyleAbstractPanel::dispose);
        super.tearDown();
    }

    public void testCdsCodeStylePanelFactoryAllBoolean() {
        CdsCodeStyleOption.Category allBooleanCategory = ALIGNMENT;
        assumeTrue("Assuming all options in category to be boolean", CdsCodeStyleSettings.OPTIONS.values().stream()
                .filter(option -> option.category == allBooleanCategory)
                .allMatch(option -> option.type == BOOLEAN));

        CdsCodeStylePanelFactory factory = new CdsCodeStylePanelFactory(createTestSettings());
        CodeStyleAbstractPanel panel = factory.createTabPanel(allBooleanCategory);
        assertEquals(CdsCodeStyleCheckboxesPanel.class, panel.getClass());
        panels.add(panel);
    }

    public void testCdsCodeStylePanelFactoryNotAllBoolean() {
        CdsCodeStylePanelFactory factory = new CdsCodeStylePanelFactory(createTestSettings());
        CodeStyleAbstractPanel panel = factory.createTabPanel(WRAPPING_AND_BRACES);
        assertEquals(CdsCodeStyleTabularPanel.class, panel.getClass());
        panels.add(panel);
    }

}
