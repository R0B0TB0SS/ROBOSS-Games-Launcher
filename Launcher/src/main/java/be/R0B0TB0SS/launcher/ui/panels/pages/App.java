package be.R0B0TB0SS.launcher.ui.panels.pages;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.ui.panel.Panel;
import be.R0B0TB0SS.launcher.ui.panels.pages.content.*;
import be.R0B0TB0SS.launcher.utils.desktop.Notification;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Priority;

import java.awt.*;
import java.util.Objects;

public class App extends Panel {
    GridPane sidemenu = new GridPane();
    public static GridPane navContent = new GridPane();

    Node activeLink = null;
    public static ContentPanel currentPage = null;

    Button homeBtn, settingsBtn, accBtn, VanillaBtn;

    Saver saver = Launcher.getInstance().getSaver();

    @Override
    public String getName() {
        return "app";
    }

    @Override
    public String getStylesheetPath() {
        return "css/app.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);
        // Configuration du layout principal
        this.layout.getStyleClass().add("app-layout");
        setCanTakeAllSize(this.layout);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(120);
        columnConstraints.setMaxWidth(120);
        this.layout.getColumnConstraints().addAll(columnConstraints, new ColumnConstraints());

        // --- Configuration du Sidemenu ---
        sidemenu.getStyleClass().add("sidemenu");
        setCanTakeAllSize(sidemenu);
        this.layout.add(sidemenu, 0, 0);

        // Définition des 3 zones (Haut, Centre, Bas)
        RowConstraints topRow = new RowConstraints();
        topRow.setVgrow(Priority.ALWAYS);
        topRow.setValignment(VPos.TOP);

        RowConstraints centerRow = new RowConstraints();
        centerRow.setVgrow(Priority.ALWAYS);
        centerRow.setValignment(VPos.CENTER);

        RowConstraints bottomRow = new RowConstraints();
        bottomRow.setVgrow(Priority.ALWAYS);
        bottomRow.setValignment(VPos.BOTTOM);

        sidemenu.getRowConstraints().addAll(topRow, centerRow, bottomRow);
        sidemenu.setPadding(new Insets(20, 0, 20, 0)); // Marges haut/bas

        // Nav content et Background
        GridPane bgImage = new GridPane();
        setCanTakeAllSize(bgImage);
        bgImage.getStyleClass().add("bg-image");
        this.layout.add(bgImage, 1, 0);

        this.layout.add(navContent, 1, 0);
        navContent.getStyleClass().add("nav-content");
        setCanTakeAllSize(navContent);

        // --- Création des Boutons ---
        double btnSize = 80.0; // Taille fixe pour tous les boutons

        // 1. Bouton COMPTE (Zone Haut)
        accBtn = createNavButton("images/steve_head.png", btnSize,60);
        // Mise à jour de l'image si l'utilisateur est connecté
        updateAccountImage();
        accBtn.setOnMouseClicked(e -> setPage(new Account(), accBtn));
        sidemenu.add(accBtn, 0, 0);
        setCenterH(accBtn);

        // 2. Boutons MODDÉ et VANILLA (Zone Centre)


        homeBtn = createNavButton("images/curseforge.png", btnSize);
        homeBtn.setOnMouseClicked(e -> { setPage(new Modded(), homeBtn); saver.set("LauncherPage", "home"); });

        VanillaBtn = createNavButton("images/vanilla.png", btnSize);
        VanillaBtn.setOnMouseClicked(e -> { setPage(new Vanilla(), VanillaBtn); saver.set("LauncherPage", "vanilla"); });

        sidemenu.add(VanillaBtn, 0, 1);
        sidemenu.add(homeBtn, 0, 1);
        homeBtn.setTranslateY(-50);
        setCenterH(homeBtn);
        VanillaBtn.setTranslateY(50);
        setCenterH(VanillaBtn);


        // 3. Bouton PARAMETRES (Zone Bas)
        settingsBtn = createNavButton("images/cog.png", btnSize);
        settingsBtn.setOnMouseClicked(e -> setPage(new Settings(), settingsBtn));
        sidemenu.add(settingsBtn, 0, 2);
        setCenterH(settingsBtn);

        VanillaBtn.setTranslateX(5);
        homeBtn.setTranslateX(5);
        settingsBtn.setTranslateX(5);
        accBtn.setTranslateX(5);

    }

    /**
     * Méthode utilitaire pour créer des boutons et des icônes uniformes
     */
    private Button createNavButton(String iconPath, double size) {
        double iconSize = 50.0;

        return createNavButton(iconPath, size, iconSize);
    }
    private Button createNavButton(String iconPath, double size,double iconSize) {

        ImageView view = new ImageView(new Image(iconPath));
        view.setPreserveRatio(true);
        view.setFitHeight(iconSize);
        view.setFitWidth(iconSize);
        view.setSmooth(true);

        Button btn = new Button();
        btn.setGraphic(view);
        btn.getStyleClass().add("sidemenu-nav-btn");

        // Taille fixe du bouton
        btn.setMinWidth(size);
        btn.setMaxWidth(size);
        btn.setMinHeight(size);
        btn.setMaxHeight(size);

        return btn;
    }

    /**
     * Gère l'affichage de la tête de skin sur le bouton compte
     */
    private void updateAccountImage() {
        String avatarUrl = "images/steve_head.png";
        if(saver.get("offline-username") != null) {
            avatarUrl = "images/steve_head.png";
        } else if(saver.get("username") != null) {
            avatarUrl = Launcher.launcherDir.resolve("player_head.png").toUri().toString();
        }

        try {
            ImageView view = (ImageView) accBtn.getGraphic();
            view.setImage(new Image(avatarUrl));
        } catch (Exception e) {
            // Repli sur steve si l'image distante échoue
        }
    }

    @Override
    public void onShow() {
        super.onShow();
        if(Objects.equals(saver.get("LauncherPage"), "vanilla")) {
            setPage(new Vanilla(), VanillaBtn);
        } else {
            setPage(new Modded(), homeBtn);
        }
    }

    public void setPage(ContentPanel panel, Node navButton) {
        if (currentPage instanceof Modded && ((Modded) currentPage).isDownloading()) return;
        if (currentPage instanceof Vanilla && ((Vanilla) currentPage).isDownloading()) return;

        if (activeLink != null) activeLink.getStyleClass().remove("active");
        activeLink = navButton;
        activeLink.getStyleClass().add("active");

        navContent.getChildren().clear();
        if (panel != null) {
            navContent.getChildren().add(panel.getLayout());
            currentPage = panel;
            if (panel.getStylesheetPath() != null) {
                this.panelManager.getStage().getScene().getStylesheets().clear();
                this.panelManager.getStage().getScene().getStylesheets().addAll(
                        this.getStylesheetPath(),
                        panel.getStylesheetPath()
                );
            }
            panel.init(this.panelManager);
            panel.onShow();
        }
    }
}