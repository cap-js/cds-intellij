package com.sap.cap.cds.util;

import com.sap.cap.cds.Plugin;
import com.sap.cap.cds.textmate.Bundle;

import static com.intellij.openapi.diagnostic.Logger.*;

public class Logger {

    public static final com.intellij.openapi.diagnostic.Logger PLUGIN = com.intellij.openapi.diagnostic.Logger.getInstance(Plugin.LABEL);

    public static final com.intellij.openapi.diagnostic.Logger TM_BUNDLE = getInstance("%s/%s".formatted(Plugin.LABEL, Bundle.LABEL));

}
