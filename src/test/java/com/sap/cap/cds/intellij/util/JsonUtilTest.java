package com.sap.cap.cds.intellij.util;

import com.intellij.testFramework.LightPlatformTestCase;
import org.json.JSONObject;

import static com.sap.cap.cds.intellij.util.JsonUtil.isJsonEqual;
import static com.sap.cap.cds.intellij.util.JsonUtil.toSortedString;

public class JsonUtilTest extends LightPlatformTestCase {

    public void testIsJsonEqual() {
        assertTrue(isJsonEqual("{}", "{}"));
        assertTrue(isJsonEqual("{ \"a\":  1}", "{\"a\": 1}"));
        assertTrue(isJsonEqual("{\"a\": 1, \"b\": 2}", "{\"b\": 2, \"a\": 1}"));

        assertFalse(isJsonEqual("{}", "{\"a\": 1}"));
        assertFalse(isJsonEqual("{\"a\": 1}", "{\"a\": 2}"));
        assertFalse(isJsonEqual("{\"a\": 1}", "{\"b\": 1}"));
        assertFalse(isJsonEqual("{\"a\": 1, \"b\": 2}", "{\"a\": 1}"));
    }

    public void testToSortedString() {
        assertEquals("{}", toSortedString(new JSONObject("{}")));
        assertEquals("{\"a\": 1}", toSortedString(new JSONObject("{\"a\": 1}")));
        assertEquals("{\n  \"a\": 1,\n  \"b\": 2\n}", toSortedString(new JSONObject("{\"b\": 2, \"a\": 1}")));
        assertEquals("{\n  \"a\": 1,\n  \"b\": 2\n}", toSortedString(new JSONObject("{\"a\": 1, \"b\": 2}")));
    }
}
