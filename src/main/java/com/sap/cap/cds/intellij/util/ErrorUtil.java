package com.sap.cap.cds.intellij.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.sap.cap.cds.intellij.CdsPlugin;

public class ErrorUtil {
    public static void show(String message) {
        Notifications.Bus.notify(new Notification("com.sap.cap.cds.intellij.notifications", CdsPlugin.TITLE, message, NotificationType.ERROR));
    }

    public static void show(String message, Throwable cause) {
        ErrorUtil.show(message.replaceFirst("\\p{Punct}\\s*$", ". Original error: ") + cause.getMessage());
    }
}
