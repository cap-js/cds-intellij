package com.sap.cap.cds.intellij.util;

import com.sap.cap.cds.intellij.Plugin;
import com.sap.cap.cds.intellij.textmate.Bundle;

import static com.intellij.openapi.diagnostic.Logger.*;

public class Logger {

    public static final com.intellij.openapi.diagnostic.Logger PLUGIN = com.intellij.openapi.diagnostic.Logger.getInstance(Plugin.LABEL);

    public static final com.intellij.openapi.diagnostic.Logger TM_BUNDLE = getInstance("%s/%s".formatted(Plugin.LABEL, Bundle.LABEL));

}
