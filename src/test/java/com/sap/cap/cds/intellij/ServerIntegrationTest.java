package com.sap.cap.cds.intellij;

import com.intellij.testFramework.fixtures.*;

import static com.sap.cap.cds.intellij.TestUtil.checkDiagnostics;

public class ServerIntegrationTest extends CodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.setTestDataPath("src/test/data/serverIntegration");
    }

    public void testDiagnostics() {
        myFixture.configureByFile(getTestName(true) + ".cds");
        checkDiagnostics(myFixture);
    }
}
