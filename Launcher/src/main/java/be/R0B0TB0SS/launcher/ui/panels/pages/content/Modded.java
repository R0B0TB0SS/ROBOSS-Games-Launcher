package be.R0B0TB0SS.launcher.ui.panels.pages.content;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Modded extends ContentPanel{
    GridPane boxPane = new GridPane();
    GridPane content = new GridPane();
    //ProgressBar progressBar = new ProgressBar();
    //Label stepLabel = new Label();
    Label fileLabel = new Label();
    GridPane sideMenu = new GridPane();
    Button createInstance =new Button("Create a profile");
    boolean isDownloading = false;
    private final Saver saver = Launcher.getInstance().getSaver();

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
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(200);
        columnConstraints.setMaxWidth(300);
        layout.getColumnConstraints().addAll(columnConstraints, new ColumnConstraints());
        this.boxPane.getStyleClass().add("box-pane");
        this.setCanTakeAllSize(this.boxPane);
        this.boxPane.setPadding(new Insets(20.0));
        this.layout.add(this.boxPane, 1, 0);
        //this.layout.getStyleClass().add("home-layout");
        //this.progressBar.getStyleClass().add("download-progress");
       // this.stepLabel.getStyleClass().add("download-status");
        //this.fileLabel.getStyleClass().add("download-status");
        //this.progressBar.setTranslateY(-15.0);
        //this.setCenterH(this.progressBar);
        //this.setCanTakeAllWidth(this.progressBar);
        //this.stepLabel.setTranslateY(5.0);
        //this.setCenterH(this.stepLabel);
       // this.setCanTakeAllSize(this.stepLabel);
        this.fileLabel.setTranslateY(20.0);
        this.setCenterH(this.fileLabel);
        this.setCanTakeAllSize(this.fileLabel);
        sideMenu();
    }

    private void sideMenu(){
        this.layout.add(sideMenu, 0, 1);
        sideMenu.getStyleClass().add("side-menu");
        setLeft(sideMenu);
        setCanTakeAllSize(sideMenu);

        createInstance.getStyleClass().add("side-menu-nav-btn");
        setCanTakeAllSize(createInstance);
        createInstance.setMinHeight(40d);
        createInstance.setMinWidth(300d);
         sideMenu.getChildren().add(createInstance);
    }

    public boolean isDownloading() {
        return this.isDownloading;
    }



}
