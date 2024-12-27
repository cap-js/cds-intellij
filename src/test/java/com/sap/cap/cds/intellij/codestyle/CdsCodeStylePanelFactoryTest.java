package com.sap.cap.cds.intellij.codestyle;

import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.testFramework.LightPlatformTestCase;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.application.options.CodeStyle.createTestSettings;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category.ALIGNMENT;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category.WRAPPING_AND_BRACES;

public class CdsCodeStylePanelFactoryTest extends LightPlatformTestCase {

    List<CodeStyleAbstractPanel> panels = new ArrayList<>();

    @Override
    protected void tearDown() throws Exception {
        panels.forEach(CodeStyleAbstractPanel::dispose);
        super.tearDown();
    }

    public void testCdsCodeStylePanelFactoryAllBoolean() {
        CdsCodeStylePanelFactory factory = new CdsCodeStylePanelFactory(createTestSettings());

        CodeStyleAbstractPanel panel = factory.createTabPanel(ALIGNMENT);
        assertEquals(panel.getClass(), CdsCodeStyleCheckboxesPanel.class);
        panels.add(panel);
    }

    public void testCdsCodeStylePanelFactoryNotAllBoolean() {
        CdsCodeStylePanelFactory factory = new CdsCodeStylePanelFactory(createTestSettings());

        CodeStyleAbstractPanel panel = factory.createTabPanel(WRAPPING_AND_BRACES);
        assertEquals(panel.getClass(), CdsCodeStyleTabularPanel.class);
        panels.add(panel);
    }

}
