package be.R0B0TB0SS.LauncherBootstrap.utils.desktop;

import be.R0B0TB0SS.LauncherBootstrap.distribution.DistributionOS;
import be.R0B0TB0SS.LauncherBootstrap.utils.io.OsCheck;
import be.R0B0TB0SS.LauncherBootstrap.utils.tray.RobossSystemTray;

import java.awt.*;

public class NotificationHelper {
    public static void sendSystemNotification(String message, TrayIcon.MessageType type) {
        String title = "ROBOSS Games Launcher";
        if (OsCheck.getOperatingSystemType() == DistributionOS.MACOS) {
            new ProcessBuilder("osascript", "-e", "display notification \"" + message + "\" with title \"" + title + "\" subtitle \"" + type + "\" sound name \"Funk\"" );
            return;
        }
        if (SystemTray.isSupported() && RobossSystemTray.getTrayIcon() != null)
            RobossSystemTray.getTrayIcon().displayMessage("ROBOSS Games Launcher", message, type);
    }
}
