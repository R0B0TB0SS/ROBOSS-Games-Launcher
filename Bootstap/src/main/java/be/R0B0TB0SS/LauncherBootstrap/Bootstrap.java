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
    private static final String URL_boot = "https://robossfactory.alwaysdata.net/launcher_files/bootstrap.json";

    private static final String VERSION = "1.0.4";

    private static final String URL_EXE = "https://robossfactory.alwaysdata.net/launcher_files/ROBOSS%20Games%20Launcher.exe";

    private static final String URL_JAR = "https://robossfactory.alwaysdata.net/launcher_files/ROBOSS%20Games%20Launcher.jar";

    private static boolean errored;

    public static void testConnexion() throws IOException{

        URL url = new URL("http://www.google.com");
        HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
        urlConn.connect();

    }

    private static void copyFile(File src, File dest) throws IOException {
        Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void main(final String[] args) {
        System.out.println("[ROBOSS Bootstrap] V"+VERSION);
        try {
            BootstrapLogger.setup();
        } catch (FileNotFoundException e1) {
            System.out.println("[ROBOSS Bootstrap][ERROR] "+e1);
            NotificationHelper.sendSystemNotification("Impossible de créer le logger.", TrayIcon.MessageType.ERROR);
            System.exit(1);
        }
        DebugData data = Debugger.debugData();
        System.out.println("[ROBOSS Bootstrap] Starting tray");
        RobossSystemTray.create();
        System.out.println("[ROBOSS Bootstrap] Checking cpu arch");
        if (data.getArch() != null && !data.getArch().contains("64"))
            NotificationHelper.sendSystemNotification("Invalide Configuration", TrayIcon.MessageType.ERROR);
        final BootstrapFrame frame = new BootstrapFrame();
        final DistributionOS os = OsCheck.getOperatingSystemType();
        System.out.println("[ROBOSS Bootstrap] Starting thread");
        (new Thread(() -> {
            try {
                System.out.println("[ROBOSS Bootstrap] Checking jcef sanity");
                OsCheck.killJcefHelper();
                long start = System.currentTimeMillis();
                System.out.println("[ROBOSS Bootstrap] Parsing distribution");
                    testConnexion();

                LauncherDistribution distribution = JsonOnlineParser.GSON.fromJson(JsonOnlineParser.parse(URL_boot), LauncherDistribution.class);
                if (distribution == null) {
                    System.out.println("[ROBOSS Bootstrap] Unable to find update (distribution)");
                    distribution.version = VERSION;
                    return;
                }
                File LinstallDir = new File(OsCheck.getLocal(), "/programs/ROBOSS-Games");
                System.out.println("[ROBOSS Bootstrap] Checking installDir");
                File installDir = new File(OsCheck.getAppData(), "ROBOSS-Games");
                if (!installDir.exists())
                     installDir.mkdirs();
                System.out.println("[ROBOSS Bootstrap] Checking bootstrap update");
                File tempLauncherFile = new File(LinstallDir, "bootstrap.temp.exe");
                if (tempLauncherFile.exists())
                    try {
                        FileUtils.forceDelete(tempLauncherFile);
                    } catch (Exception e) {
                        File from = new File(LinstallDir + "/bootstrap.temp.exe");
                        File to = new File(LinstallDir + "/ROBOSS Games Launcher.exe");
                        try {
                            copyFile(from, to);
                            System.out.println("[ROBOSS Bootstrap] File copied successfully.");
                        } catch (IOException ex) {
                            System.out.println("[ROBOSS Bootstrap][ERROR] "+ex);
                        }
                    }

                if (distribution.version != null && !VERSION.equals(distribution.version)) {
                    System.out.println("[ROBOSS Bootstrap] Outdated bootstrap version : " + VERSION + "/" + distribution.version);
                    NotificationHelper.sendSystemNotification("Update Available", TrayIcon.MessageType.INFO);
                    if (os == DistributionOS.WINDOWS) {
                        BootstrapPanel panel = (BootstrapPanel) frame.getContentPane();
                        if (panel != null)
                            panel.setProgress(EnumProgress.UPDATING);
                        if (FileUtil.downloadFile(URL_EXE, tempLauncherFile)) {
                            Runtime.getRuntime().exec(tempLauncherFile.getAbsolutePath());
                            System.out.println("[ROBOSS ROBOSS Bootstrap] Running bootstrap update (" + tempLauncherFile.getAbsolutePath() + ")");
                            System.exit(0);
                        } else {
                            NotificationHelper.sendSystemNotification("Impossible de mettre à jour le bootstrap.", TrayIcon.MessageType.ERROR);
                        }
                    } else {
                        int response = JOptionPane.showConfirmDialog(frame, "Voulez-vous télécharger la mise jour");
                        if (response == 0) {
                            Desktop.getDesktop().browse(new URI(URL_JAR));
                            System.out.println("[ROBOSS Bootstrap] Browse update url");
                            System.exit(0);
                        }
                    }
                }
                DistributionFile launcher = distribution.launcher.get(os);
                System.out.println("[ROBOSS Bootstrap] Checking java");
                System.out.println("[ROBOSS Bootstrap] Checking launcher");
                if (launcher == null) {
                    System.out.println("[ROBOSS Bootstrap] Unable to find update (launcher)");
                    NotificationHelper.sendSystemNotification("Une erreur est survenue lors de la recherche de mise à jour (launcher).", TrayIcon.MessageType.ERROR);
                    System.exit(1);
                    return;
                }
                try {
                    testConnexion();

                    File launcherFile = new File(installDir, "launcher.jar");
                    boolean downloadLauncher;
                    if (!launcherFile.exists()) {
                        downloadLauncher = true;
                    } else {
                        downloadLauncher = !FileUtil.checkSha1(launcherFile, launcher.sha1);
                        if (downloadLauncher && !launcherFile.delete()) {
                            OsCheck.killJava();
                            NotificationHelper.sendSystemNotification("Mise jour impossible car un autre launcher est ouvert.", TrayIcon.MessageType.ERROR);
                            Bootstrap.restart(args);
                            return;
                        }
                    }

                    if (downloadLauncher) {
                        System.out.println("[ROBOSS Bootstrap] Downloading " + launcher.url + " at " + launcherFile.getAbsolutePath());
                        NotificationHelper.sendSystemNotification("Téléchargement de la mise à jour.", TrayIcon.MessageType.INFO);
                        BootstrapPanel panel = (BootstrapPanel) frame.getContentPane();
                        if (panel != null)
                            panel.setProgress(EnumProgress.UPDATING);
                        if (!FileUtil.downloadFile(launcher.url, launcherFile)) {
                            System.out.println("[ROBOSS Bootstrap] Unable to download launcher");
                            NotificationHelper.sendSystemNotification("Une erreur est survenue lors de la mise jour (launcher).", TrayIcon.MessageType.ERROR);
                            System.exit(1);
                            return;
                        }
                    }
                    long end = System.currentTimeMillis();
                    if (end - start < 1000L)
                        Thread.sleep(1000L);
                    BootstrapPanel panel = (BootstrapPanel) frame.getContentPane();
                    ProcessBuilder builder = new ProcessBuilder();
                    builder.directory(installDir);
                    List<String> commands = new ArrayList<>();
                    commands.add("java");
                    commands.add("-jar");
                    commands.add(launcherFile.getAbsolutePath());
                    StringBuilder parsedCommand = new StringBuilder();
                    for (String cmd : commands)
                        parsedCommand.append(cmd).append(" ");
                    System.out.println("[ROBOSS Bootstrap] Running " + parsedCommand);
                    try {
                        Process p = builder.command(commands).start();
                        ProcessLogManager manager = new ProcessLogManager(p);
                        manager.start();
                        panel.setProgress(EnumProgress.INITIALIZED);
                        try {
                            Thread.sleep(750);
                            System.exit(0);
                        } catch (Exception e) {
                            System.exit(0);
                        }
                    } catch (IOException e) {
                        System.out.println("[ROBOSS Bootstrap][ERROR] "+e);
                        NotificationHelper.sendSystemNotification("Une erreur est survenue lors de l'éxécution de java.", TrayIcon.MessageType.ERROR);
                        Bootstrap.restart(args);
                    } catch (Exception e) {
                        System.out.println("[ROBOSS Bootstrap][ERROR] "+e);
                        NotificationHelper.sendSystemNotification("Une erreur est survenue lors de l'éxécution du programme.", TrayIcon.MessageType.ERROR);
                        System.exit(1);
                    }
                } catch (Exception e) {
                    System.out.println("[ROBOSS Bootstrap][ERROR] "+e);
                    NotificationHelper.sendSystemNotification("Une erreur est survenue lors de l'éxécution du programme.", TrayIcon.MessageType.ERROR);
                    System.exit(1);
                }

            }catch (Exception d){
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                File installDir = new File(OsCheck.getAppData(), "ROBOSS-Games");
                File launcherFile = new File(installDir, "launcher.jar");

                if (!launcherFile.exists()) {
                    NotificationHelper.sendSystemNotification("Le Launcher n'est pas installer", TrayIcon.MessageType.ERROR);
                    System.exit(1);
                }

                BootstrapPanel panel = (BootstrapPanel) frame.getContentPane();
                ProcessBuilder builder = new ProcessBuilder();
                builder.directory(installDir);
                List<String> commands = new ArrayList<>();
                commands.add("java");
                commands.add("-jar");
                commands.add(launcherFile.getAbsolutePath());
                StringBuilder parsedCommand = new StringBuilder();
                for (String cmd : commands)
                    parsedCommand.append(cmd).append(" ");
                System.out.println("[ROBOSS Bootstrap] Running " + parsedCommand);
                try {
                    Process p = builder.command(commands).start();
                    ProcessLogManager manager = new ProcessLogManager(p);
                    manager.start();
                    panel.setProgress(EnumProgress.INITIALIZED);
                    try {
                        Thread.sleep(750);
                        System.exit(0);
                    } catch (Exception e) {
                        System.exit(0);
                    }
                } catch (IOException e) {
                    System.out.println("[ROBOSS Bootstrap][ERROR] "+e);
                    NotificationHelper.sendSystemNotification("Une erreur est survenue lors de l'éxécution de java.", TrayIcon.MessageType.ERROR);
                    Bootstrap.restart(args);
                } catch (Exception e) {
                    System.out.println("[ROBOSS Bootstrap][ERROR] "+e);
                    NotificationHelper.sendSystemNotification("Une erreur est survenue lors de l'éxécution du programme.", TrayIcon.MessageType.ERROR);
                    System.exit(1);
                }
            }

        },"ROBOSS-Games-Thread")).start();
    }

    private static void restart(String[] args) {
        if (errored) {
            System.exit(1);
            return;
        }
        errored = true;
        main(args);
    }

}
