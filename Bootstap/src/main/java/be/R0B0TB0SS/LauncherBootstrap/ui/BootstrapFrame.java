package be.R0B0TB0SS.LauncherBootstrap.ui;

import be.R0B0TB0SS.LauncherBootstrap.ui.utils.WindowMover;

import javax.swing.*;
import java.awt.*;

public class BootstrapFrame extends JFrame {
    private Image image;
    public BootstrapFrame() {
        this.init();
        this.start();
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

    private void init() {

        this.image = this.load("icon.png");
        ImageIcon icon = new ImageIcon(image);
        this.setIconImage(icon.getImage());


        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        double defaultWidth = 616.0;
        double defaultHeight = 840.0;
        double defaultRatio = defaultWidth / defaultHeight;
        int height = (int)(screenDimension.getHeight() * (defaultHeight / 1080.0));
        int width = (int)((double)height * defaultRatio);
        this.setTitle("ROBOSS Games Launcher");
        this.setSize(width, height);
        this.setMinimumSize(this.getSize());
        this.setLocationRelativeTo(null);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        WindowMover mover = new WindowMover(this);
        this.addMouseListener(mover);
        this.addMouseMotionListener(mover);
        this.setContentPane(new BootstrapPanel());
    }

    private void start() {
        this.setVisible(true);
    }
}
