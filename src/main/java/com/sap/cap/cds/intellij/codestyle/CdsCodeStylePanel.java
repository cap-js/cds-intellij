package com.sap.cap.cds.intellij.codestyle;

public interface CdsCodeStylePanel {
    void addOption(CdsCodeStyleOption<?> option);

    CdsCodeStyleOption.Category getCategory();
}
