package com.sap.cap.cds.util;

public class StackUtil {
    public static StackTraceElement getCaller(int depth) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length <= depth) {
            return null;
        }
        return stackTrace[depth];
    }

    public static String getMethod(StackTraceElement caller) {
        if (caller == null) {
            return "(unknown)";
        }
        return caller.getClassName() + "." + caller.getMethodName();
    }
}
