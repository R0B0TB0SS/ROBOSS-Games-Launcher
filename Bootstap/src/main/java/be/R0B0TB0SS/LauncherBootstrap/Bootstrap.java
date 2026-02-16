package be.R0B0TB0SS.LauncherBootstrap;

import be.R0B0TB0SS.LauncherBootstrap.utils.debug.DebugData;
import be.R0B0TB0SS.LauncherBootstrap.utils.debug.Debugger;
import be.R0B0TB0SS.LauncherBootstrap.utils.desktop.NotificationHelper;
import be.R0B0TB0SS.LauncherBootstrap.utils.games.ProcessLogManager;
import be.R0B0TB0SS.LauncherBootstrap.utils.io.FileUtil;
import be.R0B0TB0SS.LauncherBootstrap.utils.tray.RobossSystemTray;
import be.R0B0TB0SS.LauncherBootstrap.distribution.DistributionFile;
import be.R0B0TB0SS.LauncherBootstrap.distribution.DistributionOS;
import be.R0B0TB0SS.LauncherBootstrap.distribution.LauncherDistribution;
import be.R0B0TB0SS.LauncherBootstrap.ui.BootstrapFrame;
import be.R0B0TB0SS.LauncherBootstrap.ui.BootstrapPanel;
import be.R0B0TB0SS.LauncherBootstrap.utils.io.OsCheck;
import be.R0B0TB0SS.LauncherBootstrap.utils.json.JsonOnlineParser;
import be.R0B0TB0SS.LauncherBootstrap.utils.launcher.EnumProgress;
import be.R0B0TB0SS.LauncherBootstrap.utils.logger.BootstrapLogger;
import org.apache.commons.io.FileUtils;

import java.awt.Desktop;
import java.awt.TrayIcon;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Bootstrap {
    private static final String URL_boot = "https://www.robotboss.org/launcher_files/bootstrap.json";
    private static final String VERSION = "1.0.7";
    private static final String URL_EXE = "https://www.robotboss.org/launcher_files/ROBOSS%20Games%20Launcher.exe";
    private static final String URL_JAR = "https://www.robotboss.org/launcher_files/ROBOSS%20Games%20Launcher.jar";
    private static boolean errored;

    public static void testConnexion() throws IOException {
        URL url = new URL("https://www.robotboss.org");
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setConnectTimeout(5000);
        urlConn.connect();
        urlConn.disconnect();
    }

    private static void copyFile(File src, File dest) throws IOException {
        Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void main(final String[] args) {
        System.out.println("[ROBOSS Bootstrap] V" + VERSION);
        try {
            BootstrapLogger.setup();
        } catch (FileNotFoundException e1) {
            System.out.println("[ROBOSS Bootstrap][ERROR] " + e1);
            NotificationHelper.sendSystemNotification("Impossible de créer le logger.", TrayIcon.MessageType.ERROR);
        }

        DebugData data = Debugger.debugData();
        System.out.println("[ROBOSS Bootstrap] Starting tray");
        RobossSystemTray.create();

        if (data.getArch() != null && !data.getArch().contains("64"))
            NotificationHelper.sendSystemNotification("Invalide Configuration", TrayIcon.MessageType.ERROR);

        final BootstrapFrame frame = new BootstrapFrame();
        final DistributionOS os = OsCheck.getOperatingSystemType();

        System.out.println("[ROBOSS Bootstrap] Starting thread");
        new Thread(() -> {
            try {
                System.out.println("[ROBOSS Bootstrap] Checking jcef sanity");
                OsCheck.killJcefHelper();
                long start = System.currentTimeMillis();

                // Attempt connection
                try {
                    testConnexion();
                } catch (IOException e) {
                    System.out.println("[ROBOSS Bootstrap] Offline mode triggered.");
                    throw new Exception("Offline");
                }

                System.out.println("[ROBOSS Bootstrap] Parsing distribution");
                LauncherDistribution distribution = JsonOnlineParser.GSON.fromJson(JsonOnlineParser.parse(URL_boot), LauncherDistribution.class);

                if (distribution == null) {
                    System.out.println("[ROBOSS Bootstrap] Distribution is null, attempting local launch.");
                    throw new Exception("No Distribution");
                }

                File LinstallDir = new File(OsCheck.getLocal(), "/programs/ROBOSS-Games");
                File installDir = new File(OsCheck.getAppData(), "ROBOSS-Games");
                if (!installDir.exists()) installDir.mkdirs();

                // Bootstrap Self-Update Logic
                File tempLauncherFile = new File(LinstallDir, "bootstrap.temp.exe");
                if (tempLauncherFile.exists()) {
                    try {
                        FileUtils.forceDelete(tempLauncherFile);
                    } catch (Exception e) {
                        File to = new File(LinstallDir, "ROBOSS Games Launcher.exe");
                        try {
                            copyFile(tempLauncherFile, to);
                        } catch (IOException ex) {
                            System.out.println("[ROBOSS Bootstrap][ERROR] " + ex);
                        }
                    }
                }

                if (distribution.version != null && !VERSION.equals(distribution.version)) {
                    System.out.println("[ROBOSS Bootstrap] Outdated bootstrap: " + VERSION + " -> " + distribution.version);
                    NotificationHelper.sendSystemNotification("Update Available", TrayIcon.MessageType.INFO);
                    if (os == DistributionOS.WINDOWS) {
                        updateProgressUI(frame, EnumProgress.UPDATING);
                        if (FileUtil.downloadFile(URL_EXE, tempLauncherFile)) {
                            new ProcessBuilder(tempLauncherFile.getAbsolutePath()).start();
                            System.exit(0);
                        }
                    } else {
                        int response = JOptionPane.showConfirmDialog(frame, "Voulez-vous télécharger la mise jour");
                        if (response == 0) {
                            Desktop.getDesktop().browse(new URI(URL_JAR));
                            System.exit(0);
                        }
                    }
                }

                // Launcher update logic
                DistributionFile launcher = distribution.launcher.get(os);
                if (launcher == null) {
                    NotificationHelper.sendSystemNotification("Launcher non trouvé.", TrayIcon.MessageType.ERROR);
                    return;
                }

                File launcherFile = new File(installDir, "launcher.jar");
                boolean downloadLauncher = !launcherFile.exists() || !FileUtil.checkSha1(launcherFile, launcher.sha1);

                if (downloadLauncher) {
                    if (launcherFile.exists() && !launcherFile.delete()) {
                        OsCheck.killJava();
                        NotificationHelper.sendSystemNotification("Veuillez fermer le launcher ouvert.", TrayIcon.MessageType.ERROR);
                        restart(args);
                        return;
                    }
                    updateProgressUI(frame, EnumProgress.UPDATING);
                    if (!FileUtil.downloadFile(launcher.url, launcherFile)) {
                        throw new Exception("Download failed");
                    }
                }

                long end = System.currentTimeMillis();
                if (end - start < 1000L) Thread.sleep(1000L - (end - start));

                panel(args, frame, installDir, launcherFile);

            } catch (Exception d) {
                System.out.println("[ROBOSS Bootstrap] Falling back to local files...");
                File installDir = new File(OsCheck.getAppData(), "ROBOSS-Games");
                File launcherFile = new File(installDir, "launcher.jar");

                if (!launcherFile.exists()) {
                    NotificationHelper.sendSystemNotification("Le Launcher n'est pas installé.", TrayIcon.MessageType.ERROR);
                } else {
                    panel(args, frame, installDir, launcherFile);
                }
            }
        }, "ROBOSS-Games-Thread").start();
    }

    private static void panel(String[] args, BootstrapFrame frame, File installDir, File launcherFile) {
        updateProgressUI(frame, EnumProgress.INITIALIZED);

        // Use the current running Java path to ensure consistency
        String javaPath = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

        ProcessBuilder builder = new ProcessBuilder(javaPath, "-jar", launcherFile.getAbsolutePath());
        builder.directory(installDir);
        builder.inheritIO(); // Critical: Forwards child logs to your current console/IDE

        System.out.println("[ROBOSS Bootstrap] Spawning process: " + launcherFile.getAbsolutePath());

        try {
            Process p = builder.start();
            // Optional: Still use the manager if it handles specific logging logic
            new ProcessLogManager(p).start();

            Thread.sleep(4000); // Give the jar 4 seconds to start before killing bootstrap

            if (p.isAlive()) {
                System.exit(0);
            } else {
                System.out.println("[ROBOSS Bootstrap] Process died immediately. Exit code: " + p.exitValue());
                NotificationHelper.sendSystemNotification("Le Launcher a crashé au démarrage.", TrayIcon.MessageType.ERROR);
            }
        } catch (Exception e) {
            System.out.println("[ROBOSS Bootstrap][ERROR] " + e);
            NotificationHelper.sendSystemNotification("Erreur de lancement Java.", TrayIcon.MessageType.ERROR);
            restart(args);
        }
    }

    private static void updateProgressUI(BootstrapFrame frame, EnumProgress state) {
        SwingUtilities.invokeLater(() -> {
            BootstrapPanel panel = (BootstrapPanel) frame.getContentPane();
            if (panel != null) panel.setProgress(state);
        });
    }

    private static void restart(String[] args) {
        if (errored) return;
        errored = true;
        main(args);
    }
}