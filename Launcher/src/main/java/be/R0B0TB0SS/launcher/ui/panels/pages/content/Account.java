package be.R0B0TB0SS.launcher.ui.panels.pages.content;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.ui.panels.pages.Login;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;


public class Account extends  ContentPanel{
        private final Saver saver = Launcher.getInstance().getSaver();
        GridPane contentPane = new GridPane();
    ContentPanel currentPage = null;

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

            // Background
            this.layout.getStyleClass().add("settings-layout");
            this.layout.setPadding(new Insets(40));
            setCanTakeAllSize(this.layout);

            // Content
            contentPane.getStyleClass().add("content-pane");
            setCanTakeAllSize(contentPane);
            this.layout.getChildren().add(contentPane);

            // Titre
            Label title = new Label(Translate.getTranslate("account.pagename"));
            title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
            title.getStyleClass().add("settings-title");
            setLeft(title);
            setCanTakeAllSize(title);
            setTop(title);
            title.setTextAlignment(TextAlignment.LEFT);
            title.setTranslateY(40d);
            title.setTranslateX(25d);
            contentPane.getChildren().add(title);

            // RAM
            if (Launcher.getInstance().getAuthInfos() != null) {
            // Pseudo + avatar
            GridPane userPane = new GridPane();
            userPane.setTranslateY(100d);
            setCanTakeAllWidth(userPane);
            userPane.setMaxHeight(480d);
            userPane.setMinWidth(80);
            userPane.getStyleClass().add("user-pane");
            setTop(userPane);


            if(saver.get("offline-username")!= null){
                String avatarUrl = "images/steve_body.png";
                ImageView avatarView = new ImageView();
                Image avatarImg = new Image(avatarUrl);
                avatarView.setImage(avatarImg);
                avatarView.setPreserveRatio(true);
                avatarView.setFitHeight(userPane.getMaxHeight()*0.85);
                setCenterV(avatarView);
                setCanTakeAllSize(avatarView);
                setLeft(avatarView);
                avatarView.setTranslateX(50d);
                userPane.getChildren().add(avatarView);
            }else{
                try{
                    Launcher.IsOnline();
                String avatarUrl = "https://mc-heads.net/body/" + Launcher.getInstance().getAuthInfos().getUuid() + ".png/96";
                ImageView avatarView = new ImageView();
                Image avatarImg = new Image(avatarUrl);
                avatarView.setImage(avatarImg);
                avatarView.setPreserveRatio(true);
                avatarView.setFitHeight(userPane.getMaxHeight()*0.85);
                setCenterV(avatarView);
                setCanTakeAllSize(avatarView);
                setLeft(avatarView);
                avatarView.setTranslateX(50d);
                userPane.getChildren().add(avatarView);
            }catch (IOException e) {
                    if (saver.get("username") != null) {

                        String avatarUrl = Launcher.launcherDir.resolve("player_body.png").toUri().toString().toLowerCase(Locale.ROOT);
                        ImageView avatarView = new ImageView();
                        Image avatarImg = new Image(avatarUrl);
                        avatarView.setImage(avatarImg);
                        avatarView.setPreserveRatio(true);
                        avatarView.setFitHeight(userPane.getMaxHeight()*0.85);
                        setCenterV(avatarView);
                        setCanTakeAllSize(avatarView);
                        setLeft(avatarView);
                        avatarView.setTranslateX(50d);
                        userPane.getChildren().add(avatarView);

                    } else {

                        String avatarUrl = "/images/steve_body.png";
                        ImageView avatarView = new ImageView();
                        Image avatarImg = new Image(avatarUrl);
                        avatarView.setImage(avatarImg);
                        avatarView.setPreserveRatio(true);
                        avatarView.setFitHeight(userPane.getMaxHeight()*0.85);
                        setCenterV(avatarView);
                        setCanTakeAllSize(avatarView);
                        setLeft(avatarView);
                        avatarView.setTranslateX(50d);
                        userPane.getChildren().add(avatarView);
                    }
                }
    }

            Label NameLabel = new Label(Translate.getTranslate("account.user"));
            NameLabel.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 40f));
            setCanTakeAllSize(NameLabel);
            setTop(NameLabel);
            setLeft(NameLabel);
            NameLabel.getStyleClass().add("def-label");
            NameLabel.setTranslateX(250d);
            NameLabel.setTranslateY(50d);
            setCanTakeAllWidth(NameLabel);
            userPane.getChildren().add(NameLabel);

            Label usernameLabel = new Label(Launcher.getInstance().getAuthInfos().getUsername());
            usernameLabel.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 40f));
            setCanTakeAllSize(usernameLabel);
            setTop(usernameLabel);
            setLeft(usernameLabel);
            usernameLabel.setTranslateY(100d);
            usernameLabel.getStyleClass().add("use-label");
            usernameLabel.setTranslateX(275d);
            setCanTakeAllWidth(usernameLabel);
            userPane.getChildren().add(usernameLabel);


            Label uidLabel = new Label(Translate.getTranslate("account.user.type"));
            uidLabel.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 40f));
            setCanTakeAllSize(uidLabel);
            setTop(uidLabel);
            setLeft(uidLabel);
            uidLabel.getStyleClass().add("def-label");
            uidLabel.setTranslateY(150d);
            uidLabel.setTranslateX(250d);
            setCanTakeAllWidth(uidLabel);
            userPane.getChildren().add(uidLabel);

            if(saver.get("username") != null){
                try{
                    Launcher.IsOnline();
                    String avatarUrl = "/images/microsoft.png";
                    ImageView UUIDLabel = new ImageView();
                    Image avatarImg = new Image(avatarUrl);
                    UUIDLabel.setImage(avatarImg);
                    UUIDLabel.setPreserveRatio(true);
                    UUIDLabel.setFitHeight(50d);
                    setCanTakeAllSize(UUIDLabel);
                    setTop(UUIDLabel);
                    setLeft(UUIDLabel);
                    UUIDLabel.setTranslateX(275d);
                    UUIDLabel.setTranslateY(200d);
                    userPane.getChildren().add(UUIDLabel);
                }catch (Exception e){
                    String type = Translate.getTranslate("account.user.type.no_connection");
                    Label UUIDLabel = new Label(type);
                    UUIDLabel.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 40f));
                    setCanTakeAllSize(UUIDLabel);
                    setTop(UUIDLabel);
                    setLeft(UUIDLabel);
                    UUIDLabel.setTranslateY(200d);
                    UUIDLabel.getStyleClass().add("use-label");
                    UUIDLabel.setTranslateX(275d);
                    setCanTakeAllWidth(UUIDLabel);
                    userPane.getChildren().add(UUIDLabel);
                }
            }else{
                String type = Translate.getTranslate("account.user.type.offline");
                Label UUIDLabel = new Label(type);
                UUIDLabel.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 40f));
                setCanTakeAllSize(UUIDLabel);
                setTop(UUIDLabel);
                setLeft(UUIDLabel);
                UUIDLabel.setTranslateY(200d);
                UUIDLabel.getStyleClass().add("use-label");
                UUIDLabel.setTranslateX(275d);
                setCanTakeAllWidth(UUIDLabel);
                userPane.getChildren().add(UUIDLabel);
            }


            Button logoutBtn = new Button();
            final var logoutIcon = new MaterialDesignIconView<>(MaterialDesignIcon.L.LOGOUT);
            logoutIcon.getStyleClass().add("logout-icon");
            setCanTakeAllSize(logoutBtn);
            setCenterV(logoutBtn);
            setRight(logoutBtn);
            logoutBtn.getStyleClass().add("logout-btn");
            logoutBtn.setGraphic(logoutIcon);
            logoutBtn.setTranslateX(-40d);
            logoutBtn.setOnMouseClicked(e -> {

                if (currentPage instanceof RobossFactory && ((RobossFactory) currentPage).isDownloading()) {
                    return;
                }

                if (currentPage instanceof Vanilla && ((Vanilla) currentPage).isDownloading()) {
                    return;
                }


                saver.remove("username");
                saver.remove("UUID");
                saver.remove("offline-username");
                saver.remove("msAccessToken");
                saver.remove("msRefreshToken");
                saver.save();
                Path path = Paths.get(Launcher.launcherDir.resolve("player_head.png").toString());
                Path path1 = Paths.get(Launcher.launcherDir.resolve("player_body.png").toString());
                try {

                    Files.deleteIfExists(path);
                    Files.deleteIfExists(path1);
                }
                catch (IOException ek) {
                    logger.err(ek.toString());
                }
                Launcher.getInstance().setAuthInfos(null);
                this.panelManager.showPanel(new Login());
            });
            userPane.getChildren().add(logoutBtn);

            contentPane.getChildren().add(userPane);
        }

        }

}
