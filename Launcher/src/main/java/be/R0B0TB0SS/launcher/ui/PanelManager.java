package be.R0B0TB0SS.launcher.ui;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.panel.IPanel;
import be.R0B0TB0SS.launcher.utils.tray.RobossSystemTray;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import fr.flowarg.flowcompat.Platform;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public class PanelManager {
    private final Launcher launcher;
    public final Stage stage;
    private static GridPane layout;
    static Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    private final GridPane contentPane = new GridPane();
    private  final ImageView loadingtext= new ImageView(new Image("images/loading.png"));
    private  final ImageView icon= new ImageView(new Image("images/icon.png"));
    private  final ImageView logo= new ImageView(new Image("images/logo.png"));

    public PanelManager(Launcher launcher, Stage stage) {
        this.launcher = launcher;
        this.stage = stage;
    }
    public void start() {
        RobossSystemTray.create();
        loadingtext.setPreserveRatio(true);
        loadingtext.setFitHeight(stage.getHeight()*0.05);
        loadingtext.setTranslateY(stage.getHeight()*0.55);
        GridPane.setHalignment(loadingtext , HPos.CENTER);
        layout.getChildren().add(loadingtext);

        icon.setPreserveRatio(true);
        icon.setFitHeight(stage.getHeight()*0.55);
        GridPane.setHalignment(icon , HPos.CENTER);
        icon.setTranslateY(stage.getHeight()*0.05);
        layout.getChildren().add(icon);

        logo.setPreserveRatio(true);
        logo.setFitWidth(stage.getHeight()*0.55);
        GridPane.setHalignment(logo , HPos.CENTER);
        logo.setTranslateY(stage.getHeight()*0.35);
        layout.getChildren().add(logo);
    }



    public void init() {
        int height = (int)(screenDimension.getHeight() * 0.66666);
        int width = (int)(screenDimension.getWidth() * 0.66666);
        this.stage.setTitle("ROBOSS Games Launcher");
        stage.getIcons().add(new Image("/images/icon.png"));
        this.stage.setMinWidth(width);
        this.stage.setMinHeight(height);
        this.stage.setWidth(width);
        this.stage.setHeight(height);
        this.stage.centerOnScreen();


        layout = new GridPane();


        if (Platform.isOnLinux()) {
            Scene scene = new Scene(layout);
            this.stage.setScene(scene);
        } else {
            this.stage.initStyle(StageStyle.DECORATED);
            //TopBar topBar = new TopBar();
            BorderlessScene scene = new BorderlessScene(this.stage, StageStyle.DECORATED, layout);
            scene.setResizable(true);
            //scene.setMoveControl(topBar.getLayout());
            scene.removeDefaultCSS();


            this.stage.setScene(scene);

            /*RowConstraints topPaneContraints = new RowConstraints();
            topPaneContraints.setValignment(VPos.TOP);
            topPaneContraints.setMinHeight(30);
            topPaneContraints.setMaxHeight(30);
            layout.getRowConstraints().addAll(topPaneContraints, new RowConstraints());
            layout.add(topBar.getLayout(), 0, 0);
            topBar.init(this);*/



            scene.setFill(Color.rgb(50,50,50));

        }

        layout.add(this.contentPane, 0, 1);
        GridPane.setVgrow(this.contentPane, Priority.ALWAYS);
        GridPane.setHgrow(this.contentPane, Priority.ALWAYS);

            this.stage.show();
    }


    public void showPanel(IPanel panel) {
        this.contentPane.getChildren().clear();
        this.contentPane.getChildren().add(panel.getLayout());
        if (panel.getStylesheetPath() != null) {
            this.stage.getScene().getStylesheets().clear();
            this.stage.getScene().getStylesheets().add(panel.getStylesheetPath());
        }
        panel.init(this);
        panel.onShow();
        layout.getChildren().remove(loadingtext);
        layout.getChildren().remove(icon);
        layout.getChildren().remove(logo);
    }

    public Stage getStage() {
        return stage;
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public static double getLauncherWidth(){
        return screenDimension.getWidth() * 0.66666;
    }
    public static double getLauncherHeight(){
        return screenDimension.getHeight() * 0.66666;
    }
}