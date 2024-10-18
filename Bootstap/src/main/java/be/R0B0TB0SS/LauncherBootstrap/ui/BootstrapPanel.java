package be.R0B0TB0SS.LauncherBootstrap.ui;

import be.R0B0TB0SS.LauncherBootstrap.utils.launcher.EnumProgress;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BootstrapPanel extends JPanel {

    private final List<EnumProgress> progressList = new LinkedList<>();
    private Image image;
    private Map<EnumProgress, Image> images;

    public BootstrapPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.darkGray);
        this.loadImages();
    }

    private void loadImages() {
        this.image = this.load("background.png");
        this.images = new HashMap<>();
        EnumProgress[] var4;
        int var3 = (var4 = EnumProgress.values()).length;

        for(int var2 = 0; var2 < var3; ++var2) {
            EnumProgress enumProgress = var4[var2];
            this.images.put(enumProgress, this.load(enumProgress.name().toLowerCase() + ".png"));
        }

    }

    public void paint(Graphics g) {
        super.paint(g);
        if (this.image != null) {
            g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), this);
        }

        for (EnumProgress progress : this.progressList) {
            if (progress != null && this.images != null && this.images.get(progress) != null) {
                g.drawImage(this.images.get(progress), 0, 0, this.getWidth(), this.getHeight(), this);
            }
        }

    }

    private Image load(String name) {
        Image img = null;

        try {
            img = Toolkit.getDefaultToolkit().createImage(this.getClass().getClassLoader().getResource(name));
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return img;
    }

    public void setProgress(EnumProgress progress) {
        this.progressList.add(progress);
        this.repaint();
    }
}


