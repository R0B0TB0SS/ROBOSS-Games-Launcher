package be.R0B0TB0SS.launcher.utils.desktop;

import java.awt.*;
import java.io.IOException;
import be.R0B0TB0SS.launcher.utils.os.DistributionOS;
import be.R0B0TB0SS.launcher.utils.os.OsCheck;
import be.R0B0TB0SS.launcher.utils.tray.RobossSystemTray;

public class Notification {
    public static void sendSystemNotification(String message, TrayIcon.MessageType type) {
        String title = "ROBOSS Games Launcher";
        if (OsCheck.getOperatingSystemType() == DistributionOS.MACOS) {
            try {
                Runtime.getRuntime().exec(new String[] { "osascript", "-e", "display notification \"" + message + "\" with title \"" + title + "\" subtitle \"" + type + "\" sound name \"Funk\"" });
            } catch (IOException ignored) {}
            return;
        }
        if (SystemTray.isSupported() && RobossSystemTray.getTrayIcon() != null)
            RobossSystemTray.getTrayIcon().displayMessage("ROBOSS Games Launcher", message, type);
    }
}
