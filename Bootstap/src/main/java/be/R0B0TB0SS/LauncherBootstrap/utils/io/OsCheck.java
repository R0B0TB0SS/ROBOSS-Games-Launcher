package be.R0B0TB0SS.LauncherBootstrap.utils.io;

import be.R0B0TB0SS.LauncherBootstrap.distribution.DistributionOS;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class OsCheck {
    private static DistributionOS detectedOS;

    public static DistributionOS getOperatingSystemType() {
        if (detectedOS == null) {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if (OS.contains("mac") || OS.contains("darwin")) {
                detectedOS = DistributionOS.MACOS;
            } else if (OS.contains("win")) {
                detectedOS = DistributionOS.WINDOWS;
            } else if (OS.contains("nux")) {
                detectedOS = DistributionOS.LINUX;
            } else {
                detectedOS = DistributionOS.WINDOWS;
            }
        }
        return detectedOS;
    }

    public static File getAppData() {
        DistributionOS os = getOperatingSystemType();
        if (os == DistributionOS.WINDOWS)
            return new File(System.getenv("APPDATA"));
        if (os == DistributionOS.MACOS)
            return new File(System.getProperty("user.home") + "/Library/Application Support");
        return new File(System.getProperty("user.home"));
    }

    public static File getLocal() {
        DistributionOS os = getOperatingSystemType();
        if (os == DistributionOS.WINDOWS)
            return new File(System.getenv("LOCALAPPDATA"));
        if (os == DistributionOS.MACOS)
            return new File(System.getProperty("user.home") + "/Library/Application Support");
        return new File(System.getProperty("user.home"));
    }

    public static void killJava() {
        try {
            if (getOperatingSystemType() == DistributionOS.WINDOWS) {
                Runtime.getRuntime().exec("taskkill /f /im javaw.exe");
            } else {
                Runtime.getRuntime().exec("kill -9 java");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void killJcefHelper() {
        try {
            if (getOperatingSystemType() == DistributionOS.WINDOWS) {
                Runtime.getRuntime().exec("taskkill /f /im jcef_helper.exe");
            } else {
                Runtime.getRuntime().exec("kill -9 jcef_helper");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

