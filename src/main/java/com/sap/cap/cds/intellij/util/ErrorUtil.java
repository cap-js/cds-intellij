package com.sap.cap.cds.intellij.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.sap.cap.cds.intellij.CdsPlugin;

public class ErrorUtil {
    /**
     * Shows an error message in the IDE.
     * @param message The error message to show.
     */
    public static void show(String message) {
        Notifications.Bus.notify(new Notification("com.sap.cap.cds.intellij.notifications", CdsPlugin.TITLE, message, NotificationType.ERROR));
    }

    /**
     * Shows an error message in the IDE with a cause.
     * @param message The error message to show.
     * @param cause The cause of the error.
     */
    public static void show(String message, Throwable cause) {
        ErrorUtil.show(message.replaceFirst("\\p{Punct}\\s*$", ". Original error: ") + cause.getMessage());
    }
}
