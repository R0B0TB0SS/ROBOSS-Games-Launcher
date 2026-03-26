package be.R0B0TB0SS.launcher;

import be.R0B0TB0SS.launcher.utils.account.MgAccount;
import be.R0B0TB0SS.launcher.utils.authentification.MicrosoftAuthResult;
import be.R0B0TB0SS.launcher.utils.authentification.MicrosoftAuthenticationException;
import be.R0B0TB0SS.launcher.utils.authentification.MicrosoftAuthenticator;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.ui.panels.pages.App;
import be.R0B0TB0SS.launcher.ui.panels.pages.Login;
import be.R0B0TB0SS.launcher.utils.debug.Debugger;
import be.R0B0TB0SS.launcher.utils.desktop.Notification;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.Saver;

import java.awt.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;


public class Launcher extends Application {

    public static String VERSION = "1.1.6";
    private static Launcher instance;
    private static ILogger logger = null;
    public static final Path launcherDir = GameDirGenerator.createGameDir("robossgames", true);
    private final Saver saver;
    private AuthInfos authInfos = null;

    public static void downloadFile(String imageUrl, String destinationFile) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

            int responseCode = httpConn.getResponseCode();

            // Check for 200 OK response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedInputStream bis = new BufferedInputStream(httpConn.getInputStream());
                     FileOutputStream fos = new FileOutputStream(destinationFile)) {

                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = bis.read(buffer, 0, 1024)) != -1) {
                        fos.write(buffer, 0, count);
                    }
                }
            } else {
                // Handle HTTP error codes
                Launcher.getInstance().getLogger().err("HTTP Error: " + responseCode + " - " + httpConn.getResponseMessage());
            }
            httpConn.disconnect();
        } catch (IOException e) {
            Launcher.getInstance().getLogger().err(e.toString());
        }
    }
    public Launcher() {
        instance = this;
        this.logger = new Logger("[ROBOSS Games Launcher]", launcherDir.resolve("launcher.log"));
        if (Files.notExists(launcherDir)) {
            try {
                Files.createDirectory(launcherDir);
            }
            catch (IOException e) {
                this.logger.err("Unable to create launcher folder");
                this.logger.printStackTrace(e);
            }
        }
        this.saver = new Saver(launcherDir.resolve("config.properties"));
        this.saver.load();
    }

    public static Launcher getInstance() {
        return instance;
    }


    @Override
    public void start(Stage stage) {
        Debugger.debugData();
        this.logger.info("Starting");
        this.logger.info("Version: V"+VERSION);
       Translate.languageList();
        MgAccount.createAccountFile();
        if(!MgAccount.hasAccount(saver.get("username")) && saver.get("username") != null) {
            MgAccount.addAccount(saver.get("username"),saver.get("UUID"),saver.get("msAccessToken"),saver.get("msRefreshToken"));
        }else if (!MgAccount.hasAccount(saver.get("offline-username")) && saver.get("offline-username") != null) {
            MgAccount.addAccount(saver.get("offline-username"));
        }
        PanelManager panelManager = new PanelManager(this, stage);
        panelManager.init();
        panelManager.start();
        if(Objects.equals(saver.get("language"), null)){
            saver.set("language","en_us");
        }

       Platform.runLater(() -> {if (this.isUserAlreadyLoggedIn()) {
            this.logger.info("Hello " + this.authInfos.getUsername());
            panelManager.showPanel(new App());
        } else {
            panelManager.showPanel(new Login());
        }});
    }


    public static void IsOnline() throws IOException{

        URL url = new URL("https://www.google.com");
        HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
        urlConn.connect();

    }

    public boolean isUserAlreadyLoggedIn() {
        if (saver.get("msAccessToken") != null && saver.get("msRefreshToken") != null) {
            try {
                IsOnline();
                try {
                    MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                    MicrosoftAuthResult response = authenticator.loginWithRefreshToken(saver.get("msRefreshToken"));

                    saver.set("msAccessToken", response.getAccessToken());
                    saver.set("msRefreshToken", response.getRefreshToken());
                    saver.set("acc_type","online");
                    saver.save();
                    this.setAuthInfos(new AuthInfos(
                            response.getProfile().getName(),
                            response.getAccessToken(),
                            response.getProfile().getId()
                    ));
                    String avatarUrl = "https://mc-heads.net/head/" + Launcher.getInstance().getAuthInfos().getUuid() + ".png/64";
                    downloadFile(avatarUrl, launcherDir.resolve("player_head.png").toString());
                    String bodyUrl = "https://mc-heads.net/body/" + Launcher.getInstance().getAuthInfos().getUuid() + ".png/96";
                    downloadFile(bodyUrl, launcherDir.resolve("player_body.png").toString());

                    return true;

            }
            catch (MicrosoftAuthenticationException e) {
                this.saver.remove("msAccessToken");
                this.saver.remove("msRefreshToken");
                this.saver.remove("username");
                saver.remove("acc_type");
                this.saver.save();

                Path path = Paths.get(launcherDir.resolve("player_head.png").toString());
                Path path1 = Paths.get(launcherDir.resolve("player_body.png").toString());
                Notification.sendSystemNotification(Translate.getTranslate("error.login"), TrayIcon.MessageType.ERROR);
                try {

                    Files.deleteIfExists(path);
                    Files.deleteIfExists(path1);
                }
                catch (IOException ek) {
                    Launcher.getInstance().getLogger().err(ek.toString());

                }

                return false;

            } }catch (IOException e){
                if (this.saver.get("username") != null) {
                    this.authInfos = new AuthInfos(this.saver.get("username"), this.saver.get("msAccessToken"), this.saver.get("UUID"));
                    return true;
                }
            }
        } else if (this.saver.get("offline-username") != null) {
            this.authInfos = new AuthInfos(this.saver.get("offline-username"), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            return true;
        }
        return false;
    }

    public AuthInfos getAuthInfos() {
        return authInfos;
    }

    public void setAuthInfos(AuthInfos authInfos) {
        this.authInfos = authInfos;
    }

    public ILogger getLogger() {
        return logger;
    }

    public Saver getSaver() {
        return saver;
    }

    public Path getLauncherDir() {
        return launcherDir;
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    public static void restart() {
        try {
            // 1. Obtenir le chemin du JAR en cours d'exécution
            String jarPath = Main.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();

            // 2. Préparer la commande
            List<String> command = new ArrayList<>();
            command.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");

            // Ajouter les arguments de la JVM (mémoire, etc.)
            command.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());

            // 3. Distinguer le mode IDE du mode JAR
            if (jarPath.endsWith(".jar")) {
                // Mode JAR : on utilise -jar
                command.add("-jar");
                command.add(new File(jarPath).getPath());
            } else {
                // Mode IDE : on utilise le classpath classique
                command.add("-cp");
                command.add(System.getProperty("java.class.path"));
                command.add(System.getProperty("sun.java.command").split(" ")[0]);
            }

            // 4. Lancer le nouveau processus
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();

            // 5. Quitter proprement l'ancien
            Platform.exit();
            System.exit(0);

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}