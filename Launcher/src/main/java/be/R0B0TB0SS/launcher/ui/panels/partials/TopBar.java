package be.R0B0TB0SS.launcher.ui.panels.partials;

import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.ui.panel.Panel;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class TopBar extends Panel {
    @Override
    public String getStylesheetPath() {
        return null;
    }

    @Override
    public String getName() {
        return "TopBar";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);
        GridPane topBar = this.layout;
        this.layout.setStyle("-fx-background-color: rgb(35, 40, 40);");
        setCanTakeAllWidth(topBar);


        // TopBar: center
        Label title = new Label("ROBOSS Games Launcher");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 18f));
        title.setStyle("-fx-text-fill: white;");
        setLeft(title);
        title.setTranslateX(20);
        this.layout.getChildren().add(title);

        // TopBar: right side
        GridPane topBarButton = new GridPane();
        topBarButton.setMinWidth(100d);
        topBarButton.setMaxWidth(100d);
        setCanTakeAllSize(topBarButton);
        setRight(topBarButton);
        this.layout.getChildren().add(topBarButton);

        /*
         * TopBar buttons configuration
         */
        final var closeBtn = new MaterialDesignIconView<>(MaterialDesignIcon.C.CLOSE);
        final var fullscreenBtn = new MaterialDesignIconView<>(MaterialDesignIcon.F.FULLSCREEN);
        final var minimizeBtn = new MaterialDesignIconView<>(MaterialDesignIcon.M.MINUS);
        setCanTakeAllWidth(closeBtn, fullscreenBtn, minimizeBtn);

        closeBtn.setFill(Color.WHITE);
        closeBtn.setOpacity(.7f);
        closeBtn.setSize("25px");
        closeBtn.setOnMouseEntered(e -> closeBtn.setOpacity(1.f));
        closeBtn.setOnMouseExited(e -> closeBtn.setOpacity(0.7f));
        closeBtn.setOnMouseClicked(e -> System.exit(0));
        closeBtn.setTranslateX(70f);

        fullscreenBtn.setFill(Color.WHITE);
        fullscreenBtn.setOpacity(0.70f);
        fullscreenBtn.setSize("25px");
        fullscreenBtn.setOnMouseEntered(e -> fullscreenBtn.setOpacity(1.0f));
        fullscreenBtn.setOnMouseExited(e -> fullscreenBtn.setOpacity(0.7f));
        fullscreenBtn.setOnMouseClicked(e -> this.panelManager.getStage().setMaximized(!this.panelManager.getStage().isMaximized()));
        fullscreenBtn.setTranslateX(45.0d);

        minimizeBtn.setFill(Color.WHITE);
        minimizeBtn.setOpacity(0.70f);
        minimizeBtn.setSize("25px");
        minimizeBtn.setOnMouseEntered(e -> minimizeBtn.setOpacity(1.0f));
        minimizeBtn.setOnMouseExited(e -> minimizeBtn.setOpacity(0.7f));
        minimizeBtn.setOnMouseClicked(e -> this.panelManager.getStage().setIconified(true));
        minimizeBtn.setTranslateX(20.0d);

        topBarButton.getChildren().addAll(closeBtn, fullscreenBtn, minimizeBtn);
    }
}
