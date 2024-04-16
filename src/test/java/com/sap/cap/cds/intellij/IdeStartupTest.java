package com.sap.cap.cds.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.testFramework.HeavyPlatformTestCase;

public class IdeStartupTest extends HeavyPlatformTestCase {
    public void testIDEStartup() {
        assertNotNull(ApplicationManager.getApplication());
        assertTrue(ApplicationManager.getApplication().isActive());
    }
}
