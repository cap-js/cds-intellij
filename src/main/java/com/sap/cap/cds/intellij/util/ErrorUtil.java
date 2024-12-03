package com.sap.cap.cds.intellij.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

public class ErrorUtil {
    public static void show(String message) {
        Notifications.Bus.notify(new Notification("com.sap.cap.cds.intellij.notifications", "CDS language server", message, NotificationType.ERROR));
    }

    public static void show(String message, Throwable cause) {
        ErrorUtil.show(message.replaceFirst("\\p{Punct}\\s*$", ". Original error: ") + cause.getMessage());
    }
}
