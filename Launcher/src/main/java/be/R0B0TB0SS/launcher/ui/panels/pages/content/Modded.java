package be.R0B0TB0SS.launcher.ui.panels.pages.content;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.utils.FilesDownloader;
import be.R0B0TB0SS.launcher.utils.StepInfo;
import be.R0B0TB0SS.launcher.utils.debug.LogWindow;
import be.R0B0TB0SS.launcher.utils.deleter.DeleterUtils;
import be.R0B0TB0SS.launcher.utils.desktop.Notification;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import com.google.gson.JsonObject;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;
import fr.flowarg.flowupdater.download.json.CurseModPackInfo;
import fr.flowarg.flowupdater.download.json.ExternalFile;
import fr.flowarg.flowupdater.utils.IOUtils;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.flowupdater.versions.neoforge.NeoForgeVersion;
import fr.flowarg.flowupdater.versions.neoforge.NeoForgeVersionBuilder;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import static be.R0B0TB0SS.launcher.Launcher.IsOnline;


public class Modded extends ContentPanel {
    private static final List<String> WHITELIST_FILES = new java.util.ArrayList<>();
    private static final List<String> BLACKLIST_FILES = new java.util.ArrayList<>();
    private final Saver saver = Launcher.getInstance().getSaver();
    private static final String DATA_URL = "https://cdn.robotboss.org/launcher_files/modded.json";
    private static String GAME_VERSION = null;
    private static String MODDED_VER = null;
    private static String PROJECT_ID = null;
    private static String FILE_ID = null;
    private static Boolean EXT_REQ = true;
    private static Boolean MAINTENANCE_MOD;
    private static NoFramework.ModLoader MODLOADER;
    private static final Path instancedir = Path.of(Launcher.getInstance().getLauncherDir() + "/modded");
    private static String NAME = Translate.getTranslate("modded.name");

    GridPane boxPane = new GridPane();
    ProgressBar progressBar = new ProgressBar();
    Label stepLabel = new Label();
    Label fileLabel = new Label();
    boolean isDownloading = false;

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/home.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        rowConstraints.setMinHeight(75.0);
        rowConstraints.setMaxHeight(75.0);
        this.layout.getRowConstraints().addAll(rowConstraints, new RowConstraints());
        this.boxPane.getStyleClass().add("box-pane");
        this.setCanTakeAllSize(this.boxPane);
        this.boxPane.setPadding(new Insets(20.0));
        this.layout.add(this.boxPane, 0, 0);
        this.layout.getStyleClass().add("home-layout");
        this.progressBar.getStyleClass().add("download-progress");
        this.stepLabel.getStyleClass().add("download-status");
        this.fileLabel.getStyleClass().add("download-status");
        this.progressBar.setTranslateY(-15.0);
        this.setCenterH(this.progressBar);
        this.setCanTakeAllWidth(this.progressBar);
        this.stepLabel.setTranslateY(5.0);
        this.setCenterH(this.stepLabel);
        this.setCanTakeAllSize(this.stepLabel);
        this.fileLabel.setTranslateY(20.0);
        this.setCenterH(this.fileLabel);
        this.setCanTakeAllSize(this.fileLabel);
        try {
            IsOnline();
            FilesDownloader.downloadFile(DATA_URL, instancedir + "/modded.json");
            JsonObject object = IOUtils.readJson(new URI(DATA_URL).toURL()).getAsJsonObject();
            getModdedJsonData(object);
            if (!MAINTENANCE_MOD) {
                Platform.runLater(this::showPlayButton);
            }else{
                NameLabem(Translate.getTranslate("modded.maintenance"));
            }
        } catch (Exception ee) {
            logger.err("No internet.... Trying to read local modded.json");
            try {
                Path local = instancedir.resolve("modded.json");
                if (Files.exists(local)) {
                    String jsonStr = Files.readString(local);
                    com.google.gson.JsonObject object = com.google.gson.JsonParser.parseString(jsonStr).getAsJsonObject();
                    getModdedJsonData(object);
                    if (!MAINTENANCE_MOD) {
                        Platform.runLater(this::showPlayButton);
                    }else{
                        NameLabem(Translate.getTranslate("modded.maintenance"));
                    }
                } else {
                    logger.err("Local modded.json not found: " + local.toAbsolutePath());
                    Notification.sendSystemNotification(Translate.getTranslate("generic.unable_to_load"), TrayIcon.MessageType.ERROR);
                }
            } catch (Exception ex) {
                Launcher.getInstance().getLogger().printStackTrace(ex);
                Notification.sendSystemNotification(Translate.getTranslate("generic.unable_to_load"), TrayIcon.MessageType.ERROR);
            }
        }

    }
    private void NameLabem(){
        Label name = new Label(NAME);
        name.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 18f));
        name.setStyle("-fx-text-fill: white;");
        setLeft(name);
        name.setTranslateX(20);
        name.setTranslateY(5);
        this.boxPane.getChildren().add(name);
    }

    private void NameLabem(String text){
        Label name = new Label(text);
        name.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 18f));
        name.setStyle("-fx-text-fill: white;");
        setLeft(name);
        name.setTranslateX(20);
        name.setTranslateY(5);
        this.boxPane.getChildren().add(name);
    }



    private void showPlayButton() {
        this.boxPane.getChildren().clear();
        Button playBtn = new Button(Translate.getTranslate("btn.play"));
        final var playIcon = new MaterialDesignIconView<>(MaterialDesignIcon.C.CONTROLLER);
        playIcon.getStyleClass().add("play-icon");
        this.setCanTakeAllSize(playBtn);
        this.setCenterH(playBtn);
        this.setCenterV(playBtn);
        playBtn.setTranslateY(10);
        playBtn.getStyleClass().add("play-btn");
        playBtn.setGraphic(playIcon);
        playBtn.setOnMouseClicked(e -> this.play());
        this.boxPane.getChildren().add(playBtn);
        NameLabem();
    }

    private void play() {
        try {
            IsOnline();
            this.isDownloading = true;
            this.boxPane.getChildren().clear();
            this.setProgress(0.0, 0.0);
            this.boxPane.getChildren().addAll(this.progressBar, this.stepLabel, this.fileLabel);
            new Thread(this::update).start();
        }catch (IOException ex) {
            this.isDownloading = true;
            this.boxPane.getChildren().clear();
            this.setProgress(0.0, 0.0);
            this.boxPane.getChildren().addAll(this.progressBar, this.stepLabel, this.fileLabel);
            this.startGame(GAME_VERSION);
        }
    }


    public void update() {
        IProgressCallback callback = new IProgressCallback(){
            private final DecimalFormat decimalFormat = new DecimalFormat("#.#");
            private String stepTxt = "";
            private String percentTxt = "0.0%";

            public void step(Step step) {
                Platform.runLater(() -> {
                    this.stepTxt = StepInfo.valueOf(step.name()).getDetails();
                    Modded.this.setStatus(String.format("%s (%s)", this.stepTxt, this.percentTxt));
                });
            }

            public void update(DownloadList.DownloadInfo info) {
                Platform.runLater(() -> {
                    this.percentTxt = this.decimalFormat.format((double)info.getDownloadedBytes() * 100.0 / (double)info.getTotalToDownloadBytes()) + "%";
                    Modded.this.setStatus(String.format("%s (%s)", this.stepTxt, this.percentTxt));
                    Modded.this.setProgress(info.getDownloadedBytes(), info.getTotalToDownloadBytes());
                });
            }

            public void onFileDownloaded(Path path) {
                Platform.runLater(() -> {
                    String p = path.toString();
                    Modded.this.fileLabel.setText("..." + p.replace(instancedir.toFile().getAbsolutePath(), ""));
                });
            }
        };
        try {

                DeleterUtils.backupWhitelist(instancedir, WHITELIST_FILES, this.logger);

                VanillaVersion vanillaVersion = new
                        VanillaVersion.VanillaVersionBuilder()
                        .withName(GAME_VERSION)
                        .build();

            CurseModPackInfo cursemodpack = new CurseModPackInfo(Integer.parseInt(PROJECT_ID), Integer.parseInt(FILE_ID), EXT_REQ);
            List<ExternalFile> exFiles = ExternalFile.getExternalFilesFromJson("https://cdn.robotboss.org/launcher_files/modded/exFiles.php");
                NeoForgeVersion neoforge = new NeoForgeVersionBuilder()
                        .withNeoForgeVersion(MODDED_VER)
                        .withCurseModPack(cursemodpack)
                        .withFileDeleter(new ModFileDeleter())
                        .build();

                FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                        .withVanillaVersion(vanillaVersion)
                        .withModLoaderVersion(neoforge)
                        .withLogger(Launcher.getInstance().getLogger())
                        .withProgressCallback(callback)
                        .withExternalFiles(exFiles)
                        .build();

                updater.update(instancedir);


                DeleterUtils.restoreWhitelist(instancedir, WHITELIST_FILES, this.logger);
                DeleterUtils.cleanupBlacklistedFiles(instancedir,BLACKLIST_FILES,this.logger);
                
                
                this.startGame(updater.getVanillaVersion().getName());


        }
        catch (Exception e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Notification.sendSystemNotification(Translate.getTranslate("generic.update_failed"), TrayIcon.MessageType.ERROR);
            this.logger.info("Here am I !");
            this.showPlayButton();
            this.isDownloading = false;
            Platform.runLater(() -> this.panelManager.getStage().show());
        }
    }


    public void startGame(String gameVersion) {
        LogWindow logWindow = new LogWindow("Minecraft Logs");
        logWindow.attachToLogFile(new File(instancedir + "/logs/latest.log"));
        try {
            this.logger.info("Launching Minecraft");
            NoFramework noFramework = new NoFramework(instancedir, Launcher.getInstance().getAuthInfos(), GameFolder.FLOW_UPDATER);
            noFramework.getAdditionalVmArgs().add(this.getRamArgsFromSaver());
            Process p = noFramework.launch(gameVersion, MODDED_VER, MODLOADER);

            if(Objects.equals(saver.get("logwindow"), "true")){
                logWindow.show();
            }

            if (Objects.equals(saver.get("closeAfterLaunch"), "true")) {
                System.exit(0);
            } else {
                // Prevent the JVM from exiting if the log window is the last visible window
                // and hide the launcher UI. Do both on the FX thread to ensure correctness.
                Platform.runLater(() -> {
                    Platform.setImplicitExit(false);
                    this.panelManager.getStage().hide();
                });

                // Wait for the game process off the FX thread to avoid freezing the UI
                Thread waiter = new Thread(() -> {
                    try {
                        p.waitFor();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // Restore UI on FX thread and re-enable implicit JVM exit
                    Platform.runLater(() -> {
                        // Allow the application to exit normally when windows are closed
                        Platform.setImplicitExit(true);
                        this.panelManager.getStage().show();
                        this.logger.info("Here am I !");
                        this.showPlayButton();
                        this.isDownloading = false;
                    });

                    // Close the log window (its UI-close is also dispatched to FX thread inside close())
                    logWindow.close();
                });
                waiter.setDaemon(true);
                waiter.start();
            }
        }
        catch (Exception e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            this.logger.err("Failed to launch minecraft");
        }
    }

    public String getRamArgsFromSaver() {
        int val = 8192;
        try {
            if (this.saver.get("maxRam") == null) {
                throw new NumberFormatException();
            }
            val = Integer.parseInt(this.saver.get("maxRam"));
        }
        catch (NumberFormatException error) {
            this.saver.set("maxRam", String.valueOf(val));
            this.saver.save();
        }
        return "-Xmx" + val + "M";
    }

    public void setStatus(String status) {
        this.stepLabel.setText(status);
    }

    public void setProgress(double current, double max) {
        this.progressBar.setProgress(current / max);
    }

    public boolean isDownloading() {
        return this.isDownloading;
    }

    private void getModdedJsonData(JsonObject moddedJson) {
        if (moddedJson.has("version")) {
            JsonObject version = (JsonObject) moddedJson.get("version");
            GAME_VERSION = String.valueOf(version.get("minecraft")).split("\"")[1];
            MODDED_VER = String.valueOf(version.get("modded_ver")).split("\"")[1];
            PROJECT_ID = String.valueOf(version.get("project_id")).split("\"")[1];
            FILE_ID = String.valueOf(version.get("file_id")).split("\"")[1];
            EXT_REQ = Boolean.valueOf(String.valueOf(version.get("ext_req")));
            MODLOADER = NoFramework.ModLoader.valueOf(String.valueOf(version.get("modloader")).split("\"")[1]);
            try { NAME = String.valueOf(version.get("name")).split("\"")[1]; } catch (Exception ignored) {}
        }
        if (moddedJson.has("whitelist")) {
            moddedJson.getAsJsonArray("whitelist").forEach(element -> WHITELIST_FILES.add(element.getAsString()));
        }
        if (moddedJson.has("blacklist")) {
            moddedJson.getAsJsonArray("blacklist").forEach(element -> BLACKLIST_FILES.add(element.getAsString()));
        }
        if(moddedJson.has("maintenance")) {
            MAINTENANCE_MOD = Boolean.valueOf(String.valueOf(moddedJson.get("maintenance")));
        }

        System.out.println(Boolean.valueOf(String.valueOf(moddedJson.get("maintenance"))));
        System.out.println(moddedJson.get("maintenance"));
    }

}
