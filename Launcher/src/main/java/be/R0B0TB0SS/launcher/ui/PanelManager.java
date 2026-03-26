package be.R0B0TB0SS.launcher.ui;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.panel.IPanel;
import be.R0B0TB0SS.launcher.utils.tray.RobossSystemTray;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import fr.flowarg.flowcompat.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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

    // Conteneur pour grouper les éléments du chargement
    private final VBox loadingContainer = new VBox(20);
    private final ImageView loadingText = new ImageView(new Image("images/loading.png"));
    private final ImageView icon = new ImageView(new Image("images/icon.png"));
    private final ImageView logo = new ImageView(new Image("images/logo.png"));

    public PanelManager(Launcher launcher, Stage stage) {
        this.launcher = launcher;
        this.stage = stage;
    }

    public void start() {
        RobossSystemTray.create();

        // Configuration des images
        icon.setPreserveRatio(true);
        icon.setFitHeight(getLauncherHeight() * 0.30);

        logo.setPreserveRatio(true);
        logo.setFitWidth(getLauncherWidth() * 0.25);

        loadingText.setPreserveRatio(true);
        loadingText.setFitHeight(20); // Taille fixe pour le texte "Loading"

        // On groupe tout dans un VBox pour un centrage parfait sans Translate
        loadingContainer.setAlignment(Pos.CENTER);
        loadingContainer.getChildren().addAll(icon, logo, loadingText);

        // On ajoute le conteneur au centre de la grille principale
        layout.add(loadingContainer, 0, 1);
        GridPane.setHalignment(loadingContainer, HPos.CENTER);
        GridPane.setValignment(loadingContainer, VPos.CENTER);
    }

    public void init() {
        int height = (int) getLauncherHeight();
        int width = (int) getLauncherWidth();

        this.stage.setTitle("ROBOSS Games Launcher");
        stage.getIcons().add(new Image("/images/icon.png"));

        this.stage.setMinWidth(width);
        this.stage.setMinHeight(height);
        this.stage.setWidth(width);
        this.stage.setHeight(height);
        this.stage.centerOnScreen();

        layout = new GridPane();
        // Fond sombre dès le départ pour éviter le flash blanc
        layout.setStyle("-fx-background-color: #0d0f10;");

        if (Platform.isOnLinux()) {
            Scene scene = new Scene(layout);
            this.stage.setScene(scene);
        } else {
            // Note: StageStyle.TRANSPARENT permet d'avoir des bords arrondis via CSS si BorderlessScene le supporte
            this.stage.initStyle(StageStyle.DECORATED);

            BorderlessScene scene = new BorderlessScene(this.stage, StageStyle.DECORATED, layout);
            scene.setResizable(false);
            scene.removeDefaultCSS();

            this.stage.setScene(scene);
            scene.setFill(Color.web("#0d0f10"));
        }

        // Zone où s'afficheront les pages (Account, Home, etc.)
        layout.add(this.contentPane, 0, 1);
        GridPane.setVgrow(this.contentPane, Priority.ALWAYS);
        GridPane.setHgrow(this.contentPane, Priority.ALWAYS);

        this.stage.show();
    }

    public void showPanel(IPanel panel) {
        // Supprime l'écran de chargement s'il est présent
        layout.getChildren().remove(loadingContainer);

        this.contentPane.getChildren().clear();
        this.contentPane.getChildren().add(panel.getLayout());

        // Gestion des feuilles de style
        this.stage.getScene().getStylesheets().clear();
        if (panel.getStylesheetPath() != null) {
            this.stage.getScene().getStylesheets().add(panel.getStylesheetPath());
        }

        panel.init(this);
        panel.onShow();
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