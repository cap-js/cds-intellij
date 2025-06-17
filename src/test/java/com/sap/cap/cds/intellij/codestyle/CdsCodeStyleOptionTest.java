package com.sap.cap.cds.intellij.codestyle;

import com.intellij.testFramework.LightPlatformTestCase;

import java.util.List;

import static com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.OptionAnchor.AFTER;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category.*;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Type.*;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleSettings.CqlKeywordCapitalization.AS_IS;
import static java.util.Arrays.stream;
import static org.junit.Assert.assertArrayEquals;

public class CdsCodeStyleOptionTest extends LightPlatformTestCase {

    public void testCdsCodeStyleOptionBoolean() {
        CdsCodeStyleOption option = new CdsCodeStyleOption("name", BOOLEAN, false, "label", "group", COMMENTS, "parent", null);

        assertEquals(option.children, List.of());
        assertEquals(AFTER, option.getAnchor());
        assertEquals("parent", option.getAnchorOptionName());
    }

    public void testCdsCodeStyleOptionEnum() {
        CdsCodeStyleOption option = new CdsCodeStyleOption("name", ENUM, AS_IS, "label", "group", ALIGNMENT, null, null, CdsCodeStyleSettings.CqlKeywordCapitalization.values());

        assertEquals(option.children, List.of());
        assertNull(option.getAnchor());
        assertNull(option.getAnchorOptionName());
        assertArrayEquals(option.getValuesLabels(), stream(CdsCodeStyleSettings.CqlKeywordCapitalization.values()).map(CdsCodeStyleSettings.Enum::getLabel).toArray(String[]::new));
        assertArrayEquals(option.getValuesIds(), stream(CdsCodeStyleSettings.CqlKeywordCapitalization.values()).map(CdsCodeStyleSettings.Enum::getId).mapToInt(Integer::intValue).toArray());
    }

    public void testCdsCodeStyleOptionInt() {
        CdsCodeStyleOption option = new CdsCodeStyleOption("name", INT, 0, "label", "group", BLANK_LINES, null, List.of("child1", "child2"));

        assertEquals(option.children, List.of("child1", "child2"));
    }
}
