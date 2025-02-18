package be.R0B0TB0SS.launcher.ui.panels.pages.content;

import be.R0B0TB0SS.launcher.ui.panel.Panel;
import be.R0B0TB0SS.launcher.ui.panels.pages.App;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public abstract class ContentPanel extends Panel {
    @Override
    public void onShow() {
        FadeTransition transition = new FadeTransition(Duration.millis(250), this.layout);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.setAutoReverse(true);
        transition.play();
    }

    public void refrech(ContentPanel panel){
        App.navContent.getChildren().clear();
        if (panel != null) {
            App.navContent.getChildren().add(panel.getLayout());
            App.currentPage = panel;
            panel.init(panelManager);
            panel.onShow();
        }
    }
}
