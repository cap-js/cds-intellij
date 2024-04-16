package com.sap.cap.cds;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.testFramework.HeavyPlatformTestCase;

public class IDEStartupTest extends HeavyPlatformTestCase {
    public void testIDEStartup() {
        assertNotNull(ApplicationManager.getApplication());
        assertTrue(ApplicationManager.getApplication().isActive());
    }
}
