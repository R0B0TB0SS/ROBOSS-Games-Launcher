package be.R0B0TB0SS.launcher.ui.panels.pages.content;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import com.google.gson.JsonObject;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;
import fr.flowarg.flowupdater.download.json.CurseModPackInfo;
import fr.flowarg.flowupdater.utils.IOUtils;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.flowupdater.versions.forge.ForgeVersion;
import fr.flowarg.flowupdater.versions.forge.ForgeVersionBuilder;
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

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Objects;



public class RobossFactory extends ContentPanel {
    private final Saver saver = Launcher.getInstance().getSaver();
    public static final String ROBOSS_FACTORY_DATA_URL = "https://robossfactory.alwaysdata.net/launcher_files/roboss_factory.json";
    public static String GAME_VERSION = null;
    public static String FORGE_VERSION = null;
    private static final Path instancedir = Path.of(Launcher.getInstance().getLauncherDir() + "/versions/robossfactory1");

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
        NameLabem();
        try {
            JsonObject object = IOUtils.readJson(new URL(ROBOSS_FACTORY_DATA_URL)).getAsJsonObject();
            JsonObject version = (JsonObject) object.get("version");
            saver.set("rf1-mc-version", String.valueOf(version.get("minecraft")).split("\"")[1]);
            saver.set("rf1-forge-version", String.valueOf(version.get("forge")).split("\"")[1]);
        }catch (Exception ee){
            logger.err("No internet");
        }

        GAME_VERSION = saver.get("rf1-mc-version");
        FORGE_VERSION = saver.get("rf1-forge-version");
        this.showPlayButton();
    }
    private void NameLabem(){
        Label roboss = new Label("ROBOSS Factory I");
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
        NameLabem();
    }

    private void play() {
        try {
            Launcher.IsOnline();
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
                    RobossFactory.this.setStatus(String.format("%s (%s)", this.stepTxt, this.percentTxt));
                });
            }

            public void update(DownloadList.DownloadInfo info) {
                Platform.runLater(() -> {
                    this.percentTxt = this.decimalFormat.format((double)info.getDownloadedBytes() * 100.0 / (double)info.getTotalToDownloadBytes()) + "%";
                    RobossFactory.this.setStatus(String.format("%s (%s)", this.stepTxt, this.percentTxt));
                    RobossFactory.this.setProgress(info.getDownloadedBytes(), info.getTotalToDownloadBytes());
                });
            }

            public void onFileDownloaded(Path path) {
                Platform.runLater(() -> {
                    String p = path.toString();
                    RobossFactory.this.fileLabel.setText("..." + p.replace(instancedir.toFile().getAbsolutePath(), ""));
                });
            }
        };
        try {
                VanillaVersion vanillaVersion = new
                        VanillaVersion.VanillaVersionBuilder()
                        .withName(GAME_VERSION)
                        .build();

            CurseModPackInfo cursemodpack = new CurseModPackInfo(775507,5604438,true);

                ForgeVersion forge = new ForgeVersionBuilder()
                        .withForgeVersion(FORGE_VERSION)
                        .withFileDeleter(new ModFileDeleter(true))
                        .withCurseModPack(cursemodpack)
                        .build();

                FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                        .withVanillaVersion(vanillaVersion)
                        .withModLoaderVersion(forge)
                        .withLogger(Launcher.getInstance().getLogger())
                        .withProgressCallback(callback)
                        .build();

                updater.update(instancedir);
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
            NoFramework noFramework = new NoFramework(instancedir, Launcher.getInstance().getAuthInfos(), GameFolder.FLOW_UPDATER);
            noFramework.getAdditionalVmArgs().add(this.getRamArgsFromSaver());
            Process p = noFramework.launch(gameVersion, FORGE_VERSION.split("-")[1], NoFramework.ModLoader.FORGE);

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
            this.logger.err("Failed to launch minecraft");
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

    public enum StepInfo {
        READ(Translate.getTranslate("step.read")),
        DL_LIBS(Translate.getTranslate("step.dl_libs")),
        DL_ASSETS(Translate.getTranslate("step.dl_assets")),
        EXTRACT_NATIVES(Translate.getTranslate("step.extract_natives")),
        FORGE(Translate.getTranslate("step.forge")),
        FABRIC(Translate.getTranslate("step.fabric")),
        MODS(Translate.getTranslate("step.mods")),
        EXTERNAL_FILES(Translate.getTranslate("step.external_files")),
        POST_EXECUTIONS(Translate.getTranslate("step.post_executions")),
        MOD_LOADER(Translate.getTranslate("step.mod_loader")),
        INTEGRATION(Translate.getTranslate("step.mods.integration")),
        MOD_PACK(Translate.getTranslate("step.mod_pack")),
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
