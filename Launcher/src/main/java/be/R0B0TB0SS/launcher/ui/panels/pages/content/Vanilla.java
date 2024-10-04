package be.R0B0TB0SS.launcher.ui.panels.pages.content;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;
import fr.flowarg.flowupdater.utils.IOUtils;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Objects;

import static be.R0B0TB0SS.launcher.Launcher.IsOnline;

public class Vanilla extends ContentPanel {
    private static final String VER_LIST = "https://piston-meta.mojang.com/mc/game/version_manifest.json";
    final ComboBox<String> version = new ComboBox<>();
    private final Saver saver = Launcher.getInstance().getSaver();
    private static final Path launcherdir = Launcher.getInstance().getLauncherDir();
    private static final Path GameDir = Path.of(launcherdir + "/versions/vanilla");
    private static String GAME_VERSION =null;


    GridPane boxPane = new GridPane();
    ProgressBar progressBar = new ProgressBar();
    Label stepLabel = new Label();
    Label fileLabel = new Label();
    CheckBox authModeChk = new CheckBox("Snapshot / Beta / Alpha");
    boolean isDownloading = false;

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/vanilla.css";
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
        FieldVersion();
        NameLabem();
        this.showPlayButton();
    }
    private void FieldVersion(){

        setCanTakeAllSize(authModeChk);
        setCenterV(authModeChk);
        setCenterH(authModeChk);
        authModeChk.getStyleClass().add("login-mode-chk");
        authModeChk.setMaxWidth(300);
        authModeChk.setTranslateX(210d);
        authModeChk.setSelected(Objects.equals(saver.get("ShowSnapshot"), "true"));
        authModeChk.selectedProperty().addListener((e, old, newValue) -> {
                if(Objects.equals(saver.get("ShowSnapshot"), "false")) {
                    saver.set("ShowSnapshot", "true");
                }else{
                    saver.set("ShowSnapshot", "false");
                }
            try {
                IsOnline();
               JsonObject mainJsonObject = IOUtils.readJson(new URL(VER_LIST)).getAsJsonObject();
                if(Objects.equals(saver.get("ShowSnapshot"), "true")) {
                    version.getItems().clear();
                    version.getItems().addAll(Translate.getTranslate("vanilla.latest_release"), Translate.getTranslate("vanilla.latest_snapshot"));
                }else{
                    version.getItems().clear();
                    version.getItems().add(Translate.getTranslate("vanilla.latest_release"));
                }
                JsonObject latest = (JsonObject) mainJsonObject.get("latest");
                saver.set("latest-version", String.valueOf(latest.get("release")).split("\"")[1]);
                saver.set("latest-snapshot", String.valueOf(latest.get("snapshot")).split("\"")[1]);

                JsonArray versionArray = (JsonArray) mainJsonObject.get("versions");
                for (Object o : versionArray) {
                    JsonObject versionnumber = (JsonObject) o;
                    String id = String.valueOf(versionnumber.get("id")).split("\"")[1];
                    String type = String.valueOf(versionnumber.get("type")).split("\"")[1];
                    if (Objects.equals(saver.get("ShowSnapshot"), "true")) {
                        version.getItems().add(id);
                    } else {
                        if (Objects.equals(type, "release")) {
                            version.getItems().add(id);
                        }
                    }
                }
                version.setValue(saver.get("curVer"));
            } catch (Exception ex) {
                this.logger.err("no internet");

            }
        });

        version.setVisible(true);
        setCanTakeAllSize(version);
        setCenterV(version);
        version.setTranslateX(-160);
        setCenterH(version);
        if(this.saver.get("curVer") != null){version.setValue(this.saver.get("curVer"));}else{
        version.setValue(Translate.getTranslate("vanilla.latest_release"));}
        version.setMaxWidth(180);
        version.setTranslateY(0d);
        version.getStyleClass().add("version-combobox");

        try {
            IsOnline();
            JsonObject mainJsonObject = IOUtils.readJson(new URL(VER_LIST)).getAsJsonObject();
            if(Objects.equals(saver.get("ShowSnapshot"), "true")) {
                version.getItems().clear();
                version.getItems().addAll(Translate.getTranslate("vanilla.latest_release"), Translate.getTranslate("vanilla.latest_snapshot"));
            }else{
                version.getItems().clear();
                version.getItems().add(Translate.getTranslate("vanilla.latest_release"));
            }
            JsonObject latest = (JsonObject) mainJsonObject.get("latest");
            saver.set("latest-version", String.valueOf(latest.get("release")).split("\"")[1]);
            saver.set("latest-snapshot", String.valueOf(latest.get("snapshot")).split("\"")[1]);

            JsonArray versionArray = (JsonArray) mainJsonObject.get("versions");
            for (Object o : versionArray) {
                JsonObject versionNumber = (JsonObject) o;
                String id = String.valueOf(versionNumber.get("id")).split("\"")[1];
                String type = String.valueOf(versionNumber.get("type")).split("\"")[1];
                if (Objects.equals(saver.get("ShowSnapshot"), "true")) {
                    version.getItems().add(id);
                } else {
                    if (Objects.equals(type, "release")) {
                        version.getItems().add(id);
                    }
                }
            }

                version.setValue(saver.get("curVer"));


        } catch (Exception ex) {
            this.logger.err("no internet");

        }
    }

    private void NameLabem(){
        Label roboss = new Label("Vanilla");
        roboss.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 18f));
        roboss.setStyle("-fx-text-fill: white;");
        setLeft(roboss);
        roboss.setTranslateX(20);
        this.boxPane.getChildren().add(roboss);
    }

    private void showPlayButton() {
        this.boxPane.getChildren().clear();
        Button playBtn = new Button(Translate.getTranslate("btn.play"));
        final var playIcon = new MaterialDesignIconView<>(MaterialDesignIcon.C.CONTROLLER);
        playIcon.getStyleClass().add("play-icon");
        this.setCanTakeAllSize(playBtn);
        this.setCenterH(playBtn);
        this.setCenterV(playBtn);
        playBtn.getStyleClass().add("play-btn");
        playBtn.setGraphic(playIcon);
        playBtn.setOnMouseClicked(e -> this.play());
        this.boxPane.getChildren().add(playBtn);
        this.boxPane.getChildren().add(version);
        this.boxPane.getChildren().add(authModeChk);
        NameLabem();
    }

    private void play() {
        try {
            IsOnline();
            this.isDownloading = true;
            this.boxPane.getChildren().clear();
            this.setProgress(0.0, 0.0);
            this.boxPane.getChildren().addAll(this.progressBar, this.stepLabel, this.fileLabel);
            this.saver.set("curVer",version.getValue());
            GAME_VERSION = version.getValue();
            if (Objects.equals(GAME_VERSION, Translate.getTranslate("vanilla.latest_release"))){
                GAME_VERSION = saver.get("latest-version");
            }
            if (Objects.equals(GAME_VERSION, Translate.getTranslate("vanilla.latest_snapshot"))){
                GAME_VERSION = saver.get("latest-snapshot");
            }
            new Thread(this::update).start();
        }catch (IOException ex) {
            this.isDownloading = true;
            this.boxPane.getChildren().clear();
            this.setProgress(0.0, 0.0);
            this.boxPane.getChildren().addAll(this.progressBar, this.stepLabel, this.fileLabel);
            this.saver.set("curVer",version.getValue());
            GAME_VERSION = version.getValue();
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
                    this.stepTxt = RobossFactory.StepInfo.valueOf(step.name()).getDetails();
                    Vanilla.this.setStatus(String.format("%s (%s)", this.stepTxt, this.percentTxt));
                });
            }

            public void update(DownloadList.DownloadInfo info) {
                Platform.runLater(() -> {
                    this.percentTxt = this.decimalFormat.format((double)info.getDownloadedBytes() * 100.0 / (double)info.getTotalToDownloadBytes()) + "%";
                    Vanilla.this.setStatus(String.format("%s (%s)", this.stepTxt, this.percentTxt));
                    Vanilla.this.setProgress(info.getDownloadedBytes(), info.getTotalToDownloadBytes());
                });
            }

            public void onFileDownloaded(Path path) {
                Platform.runLater(() -> {
                    String p = path.toString();
                    Vanilla.this.fileLabel.setText("..." + p.replace(GameDir.toFile().getAbsolutePath(), ""));
                });
            }
        };
        try {
            VanillaVersion vanillaVersion = new
                    VanillaVersion.VanillaVersionBuilder()
                    .withSnapshot(true)
                    .withName(GAME_VERSION)
                    .build();

            FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                    .withVanillaVersion(vanillaVersion)
                    .withLogger(Launcher.getInstance().getLogger())
                    .withProgressCallback(callback)
                    .build();

            updater.update(Paths.get(GameDir.toUri()));
            this.startGame(updater.getVanillaVersion().getName());

        }
        catch (Exception e) {
            Launcher.getInstance().getLogger().printStackTrace(e);

            Platform.runLater(() -> this.panelManager.getStage().show());
        }
    }


    public void startGame(String gameVersion) {
        try {
            this.logger.info("Launching Minecraft");

            NoFramework noFramework = new NoFramework(Paths.get(GameDir.toUri()), Launcher.getInstance().getAuthInfos(),GameFolder.FLOW_UPDATER);
            noFramework.getAdditionalVmArgs().add(this.getRamArgsFromSaver());
            Process p = noFramework.launch(gameVersion,"", NoFramework.ModLoader.VANILLA);

            if (Objects.equals(saver.get("closeAfterLaunch"), "true")) {
                System.exit(0);
            }else{
                Platform.runLater(() -> this.panelManager.getStage().hide());
                Platform.runLater(() -> {
                    try {
                        p.waitFor();
                        Platform.runLater(() -> this.panelManager.getStage().show());
                        this.logger.info("Here am I !!");
                        this.showPlayButton();
                        this.isDownloading = false;
                    }
                    catch (InterruptedException e) {
                        Launcher.getInstance().getLogger().printStackTrace(e);
                        this.logger.err("Failed to show the launcher");
                    }
                });}

        }
        catch (Exception e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            this.logger.err("Failed to show the launcher");
        }
    }

    public String getRamArgsFromSaver() {
        int val = 2048;
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

    public static enum StepInfo {
        READ(Translate.getTranslate("step.read")),
        DL_LIBS(Translate.getTranslate("step.dl_libs")),
        DL_ASSETS(Translate.getTranslate("step.dl_assets")),
        EXTRACT_NATIVES(Translate.getTranslate("step.extract_natives")),
        EXTERNAL_FILES(Translate.getTranslate("step.external_files")),
        POST_EXECUTIONS(Translate.getTranslate("step.post_executions")),
        END(Translate.getTranslate("step.end"));

        final String details;

        StepInfo(String details) {
            this.details = details;
        }

        public String getDetails() {
            return this.details;
        }
    }
}
