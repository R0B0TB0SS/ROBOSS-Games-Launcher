package be.R0B0TB0SS.launcher.ui.panels.pages;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.ui.panel.Panel;
import be.R0B0TB0SS.launcher.ui.panels.pages.content.*;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import java.util.Locale;
import java.util.Objects;


public class App extends Panel {
    GridPane sidemenu = new GridPane();
    public static GridPane navContent = new GridPane();

    Node activeLink = null;
    public static ContentPanel currentPage = null;

    Button homeBtn, settingsBtn , accBtn, VanillaBtn,CurseBtn;

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

        // Background
        this.layout.getStyleClass().add("app-layout");
        setCanTakeAllSize(this.layout);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(120);
        columnConstraints.setMaxWidth(120);
        this.layout.getColumnConstraints().addAll(columnConstraints, new ColumnConstraints());

        // Side menu
        this.layout.add(sidemenu, 0, 0);
        sidemenu.getStyleClass().add("sidemenu");
        setLeft(sidemenu);
        setCenterH(sidemenu);
        setCenterV(sidemenu);

        // Background Image
        GridPane bgImage = new GridPane();
        setCanTakeAllSize(bgImage);
        bgImage.getStyleClass().add("bg-image");
        this.layout.add(bgImage, 1, 0);

        // Nav content
        this.layout.add(navContent, 1, 0);
        navContent.getStyleClass().add("nav-content");
        setLeft(navContent);
        setCenterH(navContent);
        setCenterV(navContent);

        // Navigation
        String homeimurl = "images/icon.png";
        ImageView homeimView = new ImageView();
        Image homeimImg = new Image(homeimurl);
        homeimView.setImage(homeimImg);
        homeimView.setPreserveRatio(true);
        homeimView.setFitHeight(60d);
        homeimView.isSmooth();
        setCenterV(homeimView);
        setCanTakeAllSize(homeimView);
        setCenterH(homeimView);


        homeBtn = new Button("");
        homeBtn.getStyleClass().add("sidemenu-nav-btn");
        homeBtn.setGraphic(homeimView);
        setCanTakeAllSize(homeBtn);
        setCenterV(homeBtn);
        homeBtn.setTranslateX(20d);
        homeBtn.setTranslateY(0);
        homeBtn.setOnMouseClicked(e -> {setPage(new RobossFactory(), homeBtn); saver.set("LauncherPage", "home");});

        String VanillaUrl = "images/vanilla.png";
        ImageView VanillaView = new ImageView();
        Image VanillaImg = new Image(VanillaUrl);
        VanillaView.setImage(VanillaImg);
        VanillaView.setPreserveRatio(true);
        VanillaView.setFitHeight(50d);
        VanillaView.isSmooth();
        setCenterV(VanillaView);
        setCanTakeAllSize(VanillaView);
        setCenterH(VanillaView);

        VanillaBtn = new Button("");
        VanillaBtn.getStyleClass().add("sidemenu-nav-btn");
        VanillaBtn.setGraphic(VanillaView);
        setCanTakeAllSize(VanillaBtn);
        setCenterV(VanillaBtn);
        VanillaBtn.setTranslateY(-100d);
        VanillaBtn.setTranslateX(20d);
        VanillaBtn.setOnMouseClicked(e -> {setPage(new Vanilla(), VanillaBtn); saver.set("LauncherPage", "vanilla");});

       String cursimgurl = "images/curseforge.png";
        ImageView curseimvieaw = new ImageView();
        Image curseimg = new Image(cursimgurl);
        curseimvieaw.setImage(curseimg);
        curseimvieaw.setPreserveRatio(true);
        curseimvieaw.setFitHeight(60d);
        curseimvieaw.isSmooth();
        setCenterV(curseimvieaw);
        setCanTakeAllSize(curseimvieaw);
        setCenterH(curseimvieaw);

        CurseBtn = new Button("");
        CurseBtn.getStyleClass().add("sidemenu-nav-btn");
        CurseBtn.setGraphic(curseimvieaw);
        setCanTakeAllSize(CurseBtn);
        setCenterV(CurseBtn);
        CurseBtn.setTranslateX(20d);
        CurseBtn.setTranslateY(100d);
        CurseBtn.setOnMouseClicked(e -> {setPage(new Instances(), CurseBtn); saver.set("LauncherPage", "modded");});

        String cogimurl = "images/cog.png";
        ImageView cogimView = new ImageView();
        Image cogimImg = new Image(cogimurl);
        cogimView.setImage(cogimImg);
        cogimView.setPreserveRatio(true);
        cogimView.setFitHeight(50d);
        cogimView.isSmooth();
        setCenterV(cogimView);
        setCanTakeAllSize(cogimView);
        setCenterH(cogimView);

        settingsBtn = new Button("");
        settingsBtn.getStyleClass().add("sidemenu-nav-btn");
        settingsBtn.setGraphic(cogimView);
        setCanTakeAllSize(settingsBtn);
        setBottom(settingsBtn);
        settingsBtn.setTranslateY(-40d);
        settingsBtn.setTranslateX(20d);
        settingsBtn.setOnMouseClicked(e -> setPage(new Settings(), settingsBtn));




        if(saver.get("offline-username")!= null){
            String avatarUrl = "images/steve_head.png";
            ImageView avatarView = new ImageView();
            Image avatarImg = new Image(avatarUrl);
            avatarView.setImage(avatarImg);
            avatarView.setPreserveRatio(true);
            avatarView.setFitHeight(70d);
            setCenterV(avatarView);
            setCanTakeAllSize(avatarView);
            setCenterH(avatarView);
            accBtn = new Button("");
            accBtn.setGraphic(avatarView);
        }else{
            if(saver.get("username")!= null){

                String avatarUrl = Launcher.launcherDir.resolve("player_head.png").toUri().toString().toLowerCase(Locale.ROOT);
                ImageView avatarView = new ImageView();
                Image avatarImg = new Image(avatarUrl);
                avatarView.setImage(avatarImg);
                avatarView.setPreserveRatio(true);
                avatarView.setFitHeight(70d);
                setCenterV(avatarView);
                setCanTakeAllSize(avatarView);
                setCenterH(avatarView);
                accBtn = new Button("");
                accBtn.setGraphic(avatarView);
            }else{
            String avatarUrl = "images/steve_head.png";
            ImageView avatarView = new ImageView();
            Image avatarImg = new Image(avatarUrl);
            avatarView.setImage(avatarImg);
            avatarView.setPreserveRatio(true);
            avatarView.setFitHeight(70d);
            setCenterV(avatarView);
            setCanTakeAllSize(avatarView);
            setCenterH(avatarView);
            accBtn = new Button("");
            accBtn.setGraphic(avatarView);}

        }

        accBtn.getStyleClass().add("sidemenu-nav-btn");
        setCanTakeAllSize(accBtn);
        setTop(accBtn);
        accBtn.setTranslateX(20d);
        accBtn.setTranslateY(40d);
        accBtn.setOnMouseClicked(e -> setPage(new Account(), accBtn));

        sidemenu.getChildren().addAll( settingsBtn,accBtn,homeBtn,CurseBtn,VanillaBtn);


    }

    @Override
    public void onShow() {
        super.onShow();
        if(Objects.equals(saver.get("LauncherPage"), "home")) {
            setPage(new RobossFactory(), homeBtn);
        } else if (Objects.equals(saver.get("LauncherPage"), "modded")) {
            setPage(new Instances(), CurseBtn);
        }else if(Objects.equals(saver.get("LauncherPage"), "vanilla")) {
            setPage(new Vanilla(), VanillaBtn);
        }else{
            setPage(new RobossFactory(), homeBtn);
        }
    }


    public void setPage(ContentPanel panel, Node navButton) {
        if (currentPage instanceof RobossFactory && ((RobossFactory) currentPage).isDownloading()) {
            return;
        }
        if (currentPage instanceof Vanilla && ((Vanilla) currentPage).isDownloading()) {
            return;
        }
        if (currentPage instanceof Instances && ((Instances) currentPage).isDownloading()) {
            return;
        }
        if (activeLink != null)
            activeLink.getStyleClass().remove("active");
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