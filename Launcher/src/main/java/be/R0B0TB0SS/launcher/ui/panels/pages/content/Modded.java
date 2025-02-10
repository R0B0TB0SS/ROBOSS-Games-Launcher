package be.R0B0TB0SS.launcher.ui.panels.pages.content;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.ui.panels.pages.App;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class Modded extends ContentPanel{
    final ComboBox<String> instance = new ComboBox<>();
    private final Saver saver = Launcher.getInstance().getSaver();
    private static final Path launcherDir = Launcher.getInstance().getLauncherDir();
    private static final Path gameDir = Path.of(launcherDir + "/versions/modded");
    private static final String instanceList = String.valueOf(Path.of(gameDir+"/instances.json"));
    private static String instanceSelected =null;
    boolean isDownloading = false;
    GridPane boxPane = new GridPane();
    ProgressBar progressBar = new ProgressBar();
    Label stepLabel = new Label();
    Label fileLabel = new Label();
    private static String instanceType = "";
    private static String instanceInstallDir = "";
    private static String fileID = "";
    private static String projectID  = "";
    private static String gameVersion = "";
    Node activeLink = null;
    ContentPanel currentPage = null;
    GridPane navContent = new GridPane();

    @Override
    public String getName() {
        return "modded";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/modded.css";
    }


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
        InstanceField();
        this.showPlayButton();

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
        //playBtn.setOnMouseClicked(e -> this.play());
        this.boxPane.getChildren().add(playBtn);
        this.boxPane.getChildren().add(instance);
        NameLabem();
        createInstanceBtn();
    }

    private void createInstanceBtn(){
        Button cisntbtn = new Button(Translate.getTranslate("btn.createInstance"));
        cisntbtn.setVisible(true);
        setCanTakeAllSize(cisntbtn);
        setCenterV(cisntbtn);
        cisntbtn.setTranslateX(160);
        setCenterH(cisntbtn);
        cisntbtn.setMaxWidth(180);
        cisntbtn.setTranslateY(0d);
        cisntbtn.getStyleClass().add("cisntbtn-btn");
        this.boxPane.getChildren().add(cisntbtn);
        cisntbtn.setOnMouseClicked(e -> this.setPage(new CreateInstance()));
    }

    private void setPage(ContentPanel panel) {
        if (App.currentPage instanceof Modded && ((Modded) App.currentPage).isDownloading()) {
            return;
        }

        App.navContent.getChildren().clear();
        if (panel != null) {
            App.navContent.getChildren().add(panel.getLayout());
            App.currentPage = panel;
            if (panel.getStylesheetPath() != null) {
                panelManager.getStage().getScene().getStylesheets().addAll(
                        this.getStylesheetPath(),
                        panel.getStylesheetPath()
                );
            }
            panel.init(panelManager);
            panel.onShow();
        }
    }

    public boolean isDownloading() {
        return this.isDownloading;
    }

    private void NameLabem(){
        Label roboss = new Label(Translate.getTranslate("modded.name"));
        roboss.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 18f));
        roboss.setStyle("-fx-text-fill: white;");
        setLeft(roboss);
        roboss.setTranslateX(20);
        this.boxPane.getChildren().add(roboss);
    }


    private void InstanceField() {

        String latID = null;
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(
                    new FileInputStream(instanceList)));
            JsonParser jsonParser = new JsonParser();
            JsonObject mainJsonObject = jsonParser.parse(reader).getAsJsonObject();

            JsonObject latest = (JsonObject) mainJsonObject.get("latest");
            latID = String.valueOf(latest.get("name")).split("\"")[1];

            JsonArray versionArray = (JsonArray) mainJsonObject.get("instances");
            for (Object o : versionArray) {
                JsonObject versionnumber = (JsonObject) o;
                String id = String.valueOf(versionnumber.get("name")).split("\"")[1];
                instance.getItems().add(id);

            }
        } catch (Exception ex) {
            //this.logger.err(String.valueOf(ex));
        }

        instance.setVisible(true);
        setCanTakeAllSize(instance);
        setCenterV(instance);
        instance.setTranslateX(-160);
        setCenterH(instance);
        instance.setValue(latID);
        instance.setMaxWidth(180);
        instance.setTranslateY(0d);
        instance.getStyleClass().add("version-combobox");

    }
}
