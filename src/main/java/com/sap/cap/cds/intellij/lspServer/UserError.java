package com.sap.cap.cds.intellij.lspServer;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

public class UserError {
    /**
     * Shows an error message in the IDE.
     * @param message The error message to show.
     */
    public static void show(String message) {
        Notifications.Bus.notify(new Notification("com.sap.cap.cds.intellij.notifications", "CDS language server", message, NotificationType.ERROR));
    }
}
