package com.sap.cap.cds.intellij.codestyle;

import com.intellij.testFramework.LightPlatformTestCase;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.intellij.application.options.CodeStyle.createTestSettings;
import static com.sap.cap.cds.intellij.codestyle.CdsCodeStyleOption.Category.ALIGNMENT;
import static com.sap.cap.cds.intellij.util.ReflectionUtil.setFieldValue;

public class CdsCodeStyleSettingsTest extends LightPlatformTestCase {

    public void testStaticFields() {
        assertNotNull(CdsCodeStyleSettings.SAMPLE_SRC);
        assertFalse(CdsCodeStyleSettings.SAMPLE_SRC.isEmpty());
    }

    public void testOptionsConsistency() {
        CdsCodeStyleSettings.OPTIONS.forEach((name, option) -> {
            assertEquals(name, option.name);
            Field field;
            try {
                field = CdsCodeStyleSettings.class.getDeclaredField(option.name);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            assertEquals(getBoxedType(field.getType()), option.type.getFieldType());
            assertEquals(getBoxedType(field.getType()), getBoxedType(option.defaultValue.getClass()));
        });
    }

    private Class<?> getBoxedType(Class<?> type) {
        if (type == int.class) return Integer.class;
        if (type == boolean.class) return Boolean.class;
        return type;
    }

    public void testGetNonDefaultSettings() throws NoSuchFieldException, IllegalAccessException {
        var settings = new CdsCodeStyleSettings(createTestSettings());
        assertEquals("{}", settings.getNonDefaultSettings());

        var firstBooleanOption = CdsCodeStyleSettings.OPTIONS.values().stream()
                .filter(option -> option.type == CdsCodeStyleOption.Type.BOOLEAN)
                .findFirst().orElseThrow();
        setFieldValue(settings, firstBooleanOption.name, !(boolean) firstBooleanOption.defaultValue);
        assertTrue(settings.getNonDefaultSettings().contains(firstBooleanOption.name));
    }

    public void testToJSON() {
        var settings = new CdsCodeStyleSettings(createTestSettings());
        String json = settings.toJSON();
        assertTrue(json.split("\n").length >= CdsCodeStyleSettings.OPTIONS.size() + 2);
    }

    public void testEquals() {
        var settings = new CdsCodeStyleSettings(createTestSettings());
        String json = settings.getNonDefaultSettings();
        assertTrue(settings.equals(json));
        settings.alignTypes = !settings.alignTypes;
        assertFalse(settings.equals(json));
    }

    public void testEqualsSameClass() {
        var settings1 = new CdsCodeStyleSettings(createTestSettings());
        var settings2 = new CdsCodeStyleSettings(createTestSettings());
        assertTrue(settings1.equals(settings2));
        settings1.alignAs = !settings1.alignAs;
        assertFalse(settings1.equals(settings2));
    }

    public void testChildOptionsEnablement() throws NoSuchFieldException, IllegalAccessException {
        var settings = new CdsCodeStyleSettings(createTestSettings());
        CdsCodeStyleOption.Category category = ALIGNMENT;

        var parent = CdsCodeStyleSettings.OPTIONS.values().stream()
                .filter(option -> option.category == category)
                .filter(option -> option.type == CdsCodeStyleOption.Type.BOOLEAN && !option.children.isEmpty())
                .findFirst().orElseThrow();

        AtomicBoolean asserted = new AtomicBoolean();
        {
            setFieldValue(settings, parent.name, true);

            asserted.set(false);
            settings.getChildOptionsEnablement(category).forEach((name, enabled) -> {
                if (parent.children.contains(name)) {
                    assertTrue(enabled);
                    asserted.set(true);
                }
            });
            assertTrue(asserted.get());
        }

        {
            setFieldValue(settings, parent.name, false);

            asserted.set(false);
            settings.getChildOptionsEnablement(category).forEach((name, enabled) -> {
                if (parent.children.contains(name)) {
                    assertFalse(enabled);
                    asserted.set(true);
                }
            });
            assertTrue(asserted.get());
        }
    }
}
