package be.R0B0TB0SS.launcher.ui.panels.pages.content;

import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class CreateInstance extends ContentPanel{
    ContentPanel currentPage = null;
    GridPane contentPane = new GridPane();


    @Override
    public String getName() {
        return "createInstance";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/profile.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        // Background
        this.layout.getStyleClass().add("create-profile-layout");
        this.layout.setPadding(new Insets(40));
        setCanTakeAllSize(this.layout);

        // Content
        contentPane.getStyleClass().add("content-pane");
        setCanTakeAllSize(contentPane);
        this.layout.getChildren().add(contentPane);

        // Titre
        Label title = new Label(Translate.getTranslate("setting.pagename"));
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        title.getStyleClass().add("settings-title");
        setLeft(title);
        setCanTakeAllSize(title);
        setTop(title);
        title.setTextAlignment(TextAlignment.LEFT);
        title.setTranslateY(40d);
        title.setTranslateX(25d);
        contentPane.getChildren().add(title);
    }


    public void setPage(ContentPanel panel, Node navButton) {
        if (currentPage instanceof Modded && ((Modded) currentPage).isDownloading()) {
            return;
        }
        if (currentPage instanceof CreateInstance) {
            return;
        }

        this.layout.getChildren().clear();
        if (panel != null) {
            this.layout.getChildren().add(panel.getLayout());

            panel.init(this.panelManager);
            panel.onShow();
        }
    }
}
