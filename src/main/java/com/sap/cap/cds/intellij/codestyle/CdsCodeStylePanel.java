package com.sap.cap.cds.intellij.codestyle;

import java.util.Map;

public interface CdsCodeStylePanel {
    CdsCodeStyleOption.Category getCategory();

    void addOption(CdsCodeStyleOption option);

    void setOptionsEnablement(Map<String, Boolean> enablementMap);
}
