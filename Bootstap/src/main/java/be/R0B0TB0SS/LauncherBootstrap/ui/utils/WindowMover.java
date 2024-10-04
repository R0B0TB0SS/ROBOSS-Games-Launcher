package be.R0B0TB0SS.LauncherBootstrap.ui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WindowMover extends MouseAdapter {
    private Point click;
    private final JFrame window;

    public WindowMover(JFrame window) {
        this.window = window;
    }

    public void mouseDragged(MouseEvent e) {
        if (this.click != null) {
            Point draggedPoint = MouseInfo.getPointerInfo().getLocation();
            if (this.window.getExtendedState() == 6) {
                this.window.setExtendedState(0);
            }

            this.window.setLocation(new Point((int)draggedPoint.getX() - (int)this.click.getX(), (int)draggedPoint.getY() - (int)this.click.getY()));
        }

    }

    public void mousePressed(MouseEvent e) {
        this.click = e.getPoint();
    }
}