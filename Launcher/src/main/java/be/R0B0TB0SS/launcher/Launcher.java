package be.R0B0TB0SS.launcher;

import be.R0B0TB0SS.launcher.authentification.MicrosoftAuthResult;
import be.R0B0TB0SS.launcher.authentification.MicrosoftAuthenticationException;
import be.R0B0TB0SS.launcher.authentification.MicrosoftAuthenticator;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.ui.panels.pages.App;
import be.R0B0TB0SS.launcher.ui.panels.pages.Login;
import be.R0B0TB0SS.launcher.utils.debug.Debugger;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.Saver;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;


public class Launcher extends Application {

    public static String VERSION = "1.1.1";
    private static Launcher instance;
    private final ILogger logger;
    public static final Path launcherDir = GameDirGenerator.createGameDir("robossgames", true);
    private final Saver saver;
    private AuthInfos authInfos = null;

    public static void downloadFile(String imageUrl, String destinationFile){
        URL url;
        try {
            url = new URL(imageUrl);
            Launcher.getInstance().getLogger().info("Download: " + url);
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream(destinationFile);

            byte[] b = new byte[2048];
            int length;

            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();
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

        URL url = new URL("https://www.robotboss.org");
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
                this.saver.save();

                Path path = Paths.get(launcherDir.resolve("player_head.png").toString());
                Path path1 = Paths.get(launcherDir.resolve("player_body.png").toString());
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

}