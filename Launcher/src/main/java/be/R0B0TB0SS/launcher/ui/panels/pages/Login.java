package be.R0B0TB0SS.launcher.ui.panels.pages;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.ui.panel.Panel;
import be.R0B0TB0SS.launcher.authentification.MicrosoftAuthenticator;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Login extends Panel {
    GridPane loginCard = new GridPane();

    Saver saver = Launcher.getInstance().getSaver();
    AtomicBoolean offlineAuth = new AtomicBoolean(false);

    TextField userField = new TextField();
    PasswordField passwordField = new PasswordField();
    Label userErrorLabel = new Label();
    Button btnLogin = new Button(Translate.getTranslate("btn.login"));
    CheckBox authModeChk = new CheckBox(Translate.getTranslate("account.user_type.offline"));
    Button msLoginBtn = new Button();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/login.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        // Background
        this.layout.getStyleClass().add("login-layout");

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(350);
        columnConstraints.setMaxWidth(350);
        this.layout.getColumnConstraints().addAll(columnConstraints, new ColumnConstraints());
        this.layout.add(loginCard, 0, 0);

        // Background image
        GridPane bgImage = new GridPane();
        setCanTakeAllSize(bgImage);
        bgImage.getStyleClass().add("bg-image");
        this.layout.add(bgImage, 1, 0);

        // Login card
        setCanTakeAllSize(this.layout);
        loginCard.getStyleClass().add("login-card");
        setLeft(loginCard);
        setCenterH(loginCard);
        setCenterV(loginCard);

        /*
         * Login sidebar
         */
        String icoimurl = "images/icon.png";
        ImageView icoimView = new ImageView();
        Image icoimImg = new Image(icoimurl);
        icoimView.setImage(icoimImg);
        icoimView.setPreserveRatio(true);
        icoimView.setFitHeight(250d);
        setCanTakeAllSize(icoimView);
        setLeft(icoimView);
        setTop(icoimView);
        icoimView.setTranslateY(20d);
        setCenterH(icoimView);
        loginCard.getChildren().add(icoimView);


        Label logtext = new Label("Version: V"+ Launcher.VERSION);
        logtext.getStyleClass().add("login-text-labels");
        setRight(logtext);
        setCanTakeAllSize(logtext);
        setBottom(logtext);
        logtext.setTextAlignment(TextAlignment.RIGHT);
        logtext.setTranslateX(25d);
        logtext.setTranslateY(25d);

        logtext.setVisible(false);
        loginCard.getChildren().add(logtext);




        String logoimurl = "images/logo.png";
        ImageView logoimView = new ImageView();
        Image logoimImg = new Image(logoimurl);
        logoimView.setImage(logoimImg);
        logoimView.setPreserveRatio(true);
        logoimView.setFitWidth(250d);
        setCanTakeAllSize(logoimView);
        setLeft(logoimView);
        setTop(logoimView);
        logoimView.setTranslateY(260d);
        setCenterH(logoimView);
        loginCard.getChildren().add(logoimView);

        // Username
        userField.setVisible(false);
        setCanTakeAllSize(userField);
        setCenterV(userField);
        setCenterH(userField);
        userField.setMaxWidth(300);
        userField.setTranslateY(0d);
        userField.getStyleClass().add("login-input");
        userField.textProperty().addListener((_a, oldValue, newValue) -> this.updateLoginBtnState(userField, userErrorLabel));

        // User error
        setCanTakeAllSize(userErrorLabel);
        setCenterV(userErrorLabel);
        setCenterH(userErrorLabel);
        userErrorLabel.getStyleClass().add("login-error");
        userErrorLabel.setTranslateY(-30d);
        userErrorLabel.setMaxWidth(280);
        userErrorLabel.setTextAlignment(TextAlignment.LEFT);


        // Login button
        setCanTakeAllSize(btnLogin);
        setCenterV(btnLogin);
        setCenterH(btnLogin);
        btnLogin.setVisible(false);
        btnLogin.setDisable(true);
        btnLogin.setTranslateY(50d);
        btnLogin.setMaxWidth(300);
        btnLogin.getStyleClass().add("login-log-btn");
        btnLogin.setOnMouseClicked(e -> this.authenticate(userField.getText()));

        setCanTakeAllSize(authModeChk);
        setCenterV(authModeChk);
        setCenterH(authModeChk);
        authModeChk.getStyleClass().add("login-mode-chk");
        authModeChk.setMaxWidth(300);
        authModeChk.setTranslateY(90d);
        authModeChk.selectedProperty().addListener((e, old, newValue) -> {
            offlineAuth.set(newValue);
            passwordField.setDisable(newValue);
            if (newValue) {
                userField.setPromptText(Translate.getTranslate("login.username"));
                userField.setVisible(true);
                msLoginBtn.setVisible(false);
                btnLogin.setVisible(true);
                userErrorLabel.setVisible(true);
            } else {
                userField.setVisible(false);
                msLoginBtn.setVisible(true);
                btnLogin.setVisible(false);
                userErrorLabel.setVisible(false);
            }

            btnLogin.setDisable(!(userField.getText().length() >= 3 && (offlineAuth.get())));
        });

        Separator separator = new Separator();
        setCanTakeAllSize(separator);
        setCenterH(separator);
        setCenterV(separator);
        separator.getStyleClass().add("login-separator");
        separator.setMaxWidth(300);
        separator.setTranslateY(110d);

        // Microsoft login button
        ImageView view = new ImageView(new Image("images/microsoft.png"));
        view.setPreserveRatio(true);
        view.setFitHeight(30d);
        setCanTakeAllSize(msLoginBtn);
        setCenterH(msLoginBtn);
        setCenterV(msLoginBtn);
        msLoginBtn.getStyleClass().add("ms-login-btn");
        msLoginBtn.setMaxWidth(300);
        msLoginBtn.setTranslateY(50d);
        msLoginBtn.setGraphic(view);
        msLoginBtn.setOnMouseClicked(e -> {
            this.authenticateMS();
            logtext.setVisible(true);
        });

        loginCard.getChildren().addAll(userField, userErrorLabel, authModeChk, btnLogin, msLoginBtn);
    }

    public void updateLoginBtnState(TextField textField, Label errorLabel) {

        if (textField.getText().isEmpty()|| userField.getText().length() < 3) {
            errorLabel.setText(Translate.getTranslate("login.too_short_username"));
        } else {
            errorLabel.setText("");
        }

        btnLogin.setDisable(!(userField.getText().length() >= 3));
    }

    public void authenticate(String user) {

            AuthInfos infos = new AuthInfos(
                    userField.getText(),
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString()
            );
            saver.set("offline-username", infos.getUsername());
            saver.save();
        Launcher.getInstance().setAuthInfos(infos);

            this.logger.info("Hello " + infos.getUsername());

            panelManager.showPanel(new App());

    }

    public void authenticateMS() {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        authenticator.loginWithAsyncWebview().whenComplete((response, error) -> {
            if (error != null) {
                Launcher.getInstance().getLogger().err(error.toString());
                Platform.runLater(()-> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(Translate.getTranslate("login.error"));
                    alert.setContentText(error.getMessage());
                    alert.show();
                });

                return;
            }

            saver.set("msAccessToken", response.getAccessToken());
            saver.set("msRefreshToken", response.getRefreshToken());
            saver.set("username", response.getProfile().getName());
            saver.set("UUID", response.getProfile().getId());
            saver.save();
            Launcher.getInstance().setAuthInfos(new AuthInfos(
                    response.getProfile().getName(),
                    response.getAccessToken(),
                    response.getProfile().getId(),
                    response.getXuid(),
                    response.getClientId()
            ));

            try {
                String avatarUrl = "https://mc-heads.net/head/" + Launcher.getInstance().getAuthInfos().getUuid() + ".png";
                Launcher.downloadFile(avatarUrl, Launcher.launcherDir.resolve("player_head.png").toString());
                String bodyUrl = "https://mc-heads.net/body/" + Launcher.getInstance().getAuthInfos().getUuid() + ".png";
                Launcher.downloadFile(bodyUrl, Launcher.launcherDir.resolve("player_body.png").toString());
            }catch (Exception ed){
                Launcher.getInstance().getLogger().info(ed.toString());
            }

            Launcher.getInstance().getLogger().info("Hello " + response.getProfile().getName());

            Platform.runLater(() -> panelManager.showPanel(new App()));
        });
    }
}