package be.R0B0TB0SS.launcher.ui.panels.pages.content;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.ui.panels.pages.Login;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class Account extends ContentPanel {
    private final Saver saver = Launcher.getInstance().getSaver();
    GridPane contentPane = new GridPane();

    @Override
    public String getName() {
        return "account";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/account.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        // Background et Layout Principal
        this.layout.getStyleClass().add("settings-layout");
        this.layout.setPadding(new Insets(30));
        setCanTakeAllSize(this.layout);

        // Conteneur de contenu (Glass Card)
        contentPane.getStyleClass().add("box-pane");
        setCanTakeAllSize(contentPane);
        contentPane.setPadding(new Insets(30));
        this.layout.getChildren().add(contentPane);

        // 1. Titre de la page
        Label title = new Label(Translate.getTranslate("account.pagename").toUpperCase());
        title.getStyleClass().add("settings-title");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        contentPane.add(title, 0, 0);
        GridPane.setMargin(title, new Insets(0, 0, 30, 0));

        if (Launcher.getInstance().getAuthInfos() != null) {

            // 2. Panneau Utilisateur (Structure Horizontale)
            GridPane userPane = new GridPane();
            userPane.getStyleClass().add("user-pane");
            userPane.setHgap(40);
            userPane.setPadding(new Insets(25));
            setCanTakeAllWidth(userPane);

            // Colonnes : [Avatar] [Infos] [Logout]
            ColumnConstraints colAvatar = new ColumnConstraints();
            ColumnConstraints colInfos = new ColumnConstraints();
            colInfos.setHgrow(Priority.ALWAYS); // Les infos prennent l'espace
            ColumnConstraints colAction = new ColumnConstraints();
            userPane.getColumnConstraints().addAll(colAvatar, colInfos, colAction);

            // --- AVATAR (Body) ---
            String avatarUrl = "images/steve_body.png";
            if (saver.get("username") != null && saver.get("offline-username") == null) {
                avatarUrl = Launcher.launcherDir.resolve("player_body.png").toUri().toString();
            }

            ImageView avatarView = new ImageView(new Image(avatarUrl));
            avatarView.setPreserveRatio(true);
            avatarView.setFitHeight(250);
            userPane.add(avatarView, 0, 0);

            // --- INFOS (Pseudo, Type, etc.) ---
            VBox infoBox = new VBox(10);
            infoBox.setAlignment(Pos.CENTER_LEFT);

            Label labelTitle = new Label(Translate.getTranslate("account.user"));
            labelTitle.getStyleClass().add("def-label");
            labelTitle.setOpacity(0.6);

            Label usernameLabel = new Label(Launcher.getInstance().getAuthInfos().getUsername());
            usernameLabel.getStyleClass().add("use-label");
            usernameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 35));

            Label typeTitle = new Label(Translate.getTranslate("account.user.type"));
            typeTitle.getStyleClass().add("def-label");
            typeTitle.setOpacity(0.6);
            GridPane.setMargin(typeTitle, new Insets(20, 0, 0, 0));

            // Gestion de l'icône de compte (Online/Offline)
            Node typeIndicator;
            if (saver.get("username") != null) {
                try {
                    Launcher.IsOnline();
                    ImageView msLogo = new ImageView(new Image("images/microsoft.png"));
                    msLogo.setPreserveRatio(true);
                    msLogo.setFitHeight(30);
                    typeIndicator = msLogo;
                } catch (Exception e) {
                    typeIndicator = new Label(Translate.getTranslate("account.user.type.no_connection"));
                    typeIndicator.getStyleClass().add("use-label");
                }
            } else {
                typeIndicator = new Label(Translate.getTranslate("account.user.type.offline"));
                typeIndicator.getStyleClass().add("use-label");
            }

            infoBox.getChildren().addAll(labelTitle, usernameLabel, typeTitle, typeIndicator);
            userPane.add(infoBox, 1, 0);

            // --- BOUTON DECONNEXION ---
            Button logoutBtn = new Button();
            logoutBtn.getStyleClass().add("logout-btn");

            final var logoutIcon = new MaterialDesignIconView<>(MaterialDesignIcon.L.LOGOUT);
            logoutIcon.setSize("40");
            logoutIcon.setFill(javafx.scene.paint.Color.WHITE);

            logoutBtn.setGraphic(logoutIcon);
            logoutBtn.setCursor(javafx.scene.Cursor.HAND);

            logoutBtn.setOnMouseClicked(e -> {
                handleLogout();
            });

            userPane.add(logoutBtn, 2, 0);
            GridPane.setHalignment(logoutBtn, HPos.RIGHT);

            contentPane.add(userPane, 0, 1);
        }
    }

    private void handleLogout() {
        saver.remove("username");
        saver.remove("UUID");
        saver.remove("offline-username");
        saver.remove("msAccessToken");
        saver.remove("msRefreshToken");
        saver.save();

        try {
            Files.deleteIfExists(Launcher.launcherDir.resolve("player_head.png"));
            Files.deleteIfExists(Launcher.launcherDir.resolve("player_body.png"));
        } catch (IOException e) {
            logger.err("Erreur lors de la suppression des images de skin: " + e.getMessage());
        }

        Launcher.getInstance().setAuthInfos(null);
        this.panelManager.showPanel(new Login());
    }
}