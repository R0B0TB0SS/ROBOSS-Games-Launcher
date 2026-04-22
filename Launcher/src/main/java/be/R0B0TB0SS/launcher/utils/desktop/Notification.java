package be.R0B0TB0SS.launcher.utils.desktop;

import java.awt.*;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import be.R0B0TB0SS.launcher.utils.audio.Audio;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.StageStyle;
import be.R0B0TB0SS.launcher.utils.os.DistributionOS;
import be.R0B0TB0SS.launcher.utils.os.OsCheck;
import be.R0B0TB0SS.launcher.utils.tray.RobossSystemTray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Notification {
    private static final Logger logger = LoggerFactory.getLogger(Notification.class);
    public static void sendSystemNotification(String message, TrayIcon.MessageType type) {
        Audio.playAudio("/audio/notif.mp3");
        String title = "ROBOSS Games Launcher";
        sendSystemNotification(title, message, type);
    }

    /**
     * Overload allowing a custom title to be shown in the notification.
     */
    public static void sendSystemNotification(String title, String message, TrayIcon.MessageType type) {
        if (OsCheck.getOperatingSystemType() == DistributionOS.MACOS) {
            // macOS notifications via osascript are ephemeral; for errors, show a dialog fallback so user must dismiss
            if (type == TrayIcon.MessageType.ERROR) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE));
                return;
            }
            try {
                String safeMessage = message.replace("\"", "\\\"");
                String safeTitle = title.replace("\"", "\\\"");
                Runtime.getRuntime().exec(new String[] { "osascript", "-e", "display notification \"" + safeMessage + "\" with title \"" + safeTitle + "\"" });
            } catch (IOException ignored) {}
            return;
        }
        try {
            // On Windows prefer a JavaFX popup so the provided title is shown; for ERROR type make it persistent
            if (OsCheck.getOperatingSystemType() == DistributionOS.WINDOWS) {
                try {
                    showFxPopup(title, message, type == TrayIcon.MessageType.ERROR);
                    return;
                } catch (IllegalStateException ignored) {
                    // JavaFX not initialized, fall back to other methods
                }
            }

            if (SystemTray.isSupported()) {
                try {
                    TrayIcon existing = RobossSystemTray.getTrayIcon();
                    if (existing != null) {
                        existing.setToolTip(title);
                        // For errors we still attempt to show system balloon but also show a persistent dialog if needed
                        existing.displayMessage(title, message, type);
                        if (type == TrayIcon.MessageType.ERROR) {
                            // try to show persistent JavaFX popup if available, else show blocking Swing dialog
                            try {
                                showFxPopup(title, message, true);
                                return;
                            } catch (IllegalStateException ignored) {
                                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE));
                                return;
                            }
                        }
                        return;
                    } else {
                        java.awt.Image awtImg = Toolkit.getDefaultToolkit().createImage(Notification.class.getClassLoader().getResource("images/icon.png"));
                        TrayIcon temp = new TrayIcon(awtImg, title);
                        temp.setImageAutoSize(true);
                        SystemTray.getSystemTray().add(temp);
                        temp.displayMessage(title, message, type);
                        if (type == TrayIcon.MessageType.ERROR) {
                            // show persistent fallback
                            try {
                                showFxPopup(title, message, true);
                                // remove temp icon a bit later
                                new Thread(() -> {
                                    try { Thread.sleep(5000); } catch (InterruptedException ignored1) {}
                                    try { SystemTray.getSystemTray().remove(temp); } catch (Throwable ignored2) {}
                                }).start();
                                return;
                            } catch (IllegalStateException ignored) {
                                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE));
                                new Thread(() -> {
                                    try { Thread.sleep(5000); } catch (InterruptedException ignored1) {}
                                    try { SystemTray.getSystemTray().remove(temp); } catch (Throwable ignored2) {}
                                }).start();
                                return;
                            }
                        }
                        new Thread(() -> {
                            try { Thread.sleep(5000); } catch (InterruptedException ignored1) {}
                            try { SystemTray.getSystemTray().remove(temp); } catch (Throwable ignored2) {}
                        }).start();
                        return;
                    }
                } catch (Throwable t) {
                    logger.error("Failed to show system tray notification", t);
                }
            }
        } catch (Throwable t) {
            logger.error("Notification failure", t);
        }

        SwingUtilities.invokeLater(() -> {
            int msgType = JOptionPane.INFORMATION_MESSAGE;
            if (type == TrayIcon.MessageType.ERROR) msgType = JOptionPane.ERROR_MESSAGE;
            else if (type == TrayIcon.MessageType.WARNING) msgType = JOptionPane.WARNING_MESSAGE;
            JOptionPane.showMessageDialog(null, message, title, msgType);
        });
    }


    private static void showFxPopup(String title, String message, boolean persistent) {
        // Throw IllegalStateException if JavaFX runtime is not initialized
        Platform.runLater(() -> {
            // Try to find the launcher's Stage (by title) to own the popup; fallback to first showing Window
            Window owner = null;
            for (Window win : Window.getWindows()) {
                if (win instanceof Stage st) {
                    try {
                        if ("ROBOSS Games Launcher".equals(st.getTitle()) && st.isShowing()) {
                            owner = st;
                            break;
                        }
                    } catch (Exception ignored) {}
                }
            }
            if (owner == null) {
                for (Window win : Window.getWindows()) {
                    if (win.isShowing()) { owner = win; break; }
                }
            }

            Stage s = new Stage();
            if (owner != null) {
                s.initOwner(owner);
            }
            s.initStyle(StageStyle.TRANSPARENT);

            // HBox with icon | text (title + optional message) | spacer | close button
            HBox root = new HBox(10);
            root.setPadding(new Insets(6));
            root.setAlignment(Pos.CENTER_LEFT);
            root.setStyle("-fx-background-color: rgba(20,20,20,0.95); -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: rgba(255,255,255,0.04);");

            // Icon (load safely, resource stream may be null)
            ImageView iv = null;
            try {
                java.io.InputStream is = Notification.class.getClassLoader().getResourceAsStream("images/icon.png");
                if (is != null) {
                    Image img = new Image(is);
                    iv = new ImageView(img);
                    iv.setFitWidth(20);
                    iv.setFitHeight(20);
                    iv.setPreserveRatio(true);
                }
            } catch (Exception ex) {
                // ignore, icon optional
            }

            // Text container
            // Text container: most notifications only need a single-line title like the screenshot
            VBox textBox = new VBox(0);
            textBox.setAlignment(Pos.CENTER_LEFT);
            Label t = new Label(title);
            t.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
            // optional subtitle (smaller); hide if empty
            Label m = new Label(message == null ? "" : message);
            m.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 11px;");
            if (message == null || message.isBlank()) textBox.getChildren().add(t);
            else textBox.getChildren().addAll(t, m);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Close button (trash icon to match screenshot)
            Button closeBtn = new Button("🗑");
            closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6; -fx-cursor: hand;");
            closeBtn.setOnAction(e -> s.close());
            closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6; -fx-cursor: hand;"));
            closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6; -fx-cursor: hand;"));

            if (iv != null) root.getChildren().add(iv);
            root.getChildren().add(textBox);
            root.getChildren().add(spacer);
            if (persistent) root.getChildren().add(closeBtn);

            Scene sc = new Scene(root);
            sc.setFill(Color.TRANSPARENT);
            s.setScene(sc);
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double notifW = 260;
            double notifH = persistent ? 56 : 44;
            s.setWidth(notifW);
            s.setHeight(notifH);

            // Position relative to owner if present (bottom-right inside the launcher window), else use screen corner
            double margin = 10;
            if (owner instanceof Stage stOwner) {
                double ox = stOwner.getX();
                double oy = stOwner.getY();
                double ow = stOwner.getWidth();
                double oh = stOwner.getHeight();
                // place near bottom-right of owner with small margin
                s.setX(ox + Math.max(0, ow - notifW - margin));
                s.setY(oy + Math.max(0, oh - notifH - margin));
                // follow owner movements/resizes
                stOwner.xProperty().addListener((obs, oldV, newV) -> s.setX(newV.doubleValue() + Math.max(0, stOwner.getWidth() - notifW - margin)));
                stOwner.yProperty().addListener((obs, oldV, newV) -> s.setY(newV.doubleValue() + Math.max(0, stOwner.getHeight() - notifH - margin)));
                stOwner.widthProperty().addListener((obs, oldV, newV) -> s.setX(stOwner.getX() + Math.max(0, newV.doubleValue() - notifW - margin)));
                stOwner.heightProperty().addListener((obs, oldV, newV) -> s.setY(stOwner.getY() + Math.max(0, newV.doubleValue() - notifH - margin)));
                // close popup if owner is hidden/closed
                stOwner.showingProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) {
                        Platform.runLater(() -> { if (s.isShowing()) s.close(); });
                    }
                });
            } else {
                s.setX(screenBounds.getMaxX() - notifW - 20);
                s.setY(screenBounds.getMaxY() - notifH - 40);
                // If owner is present but not a Stage, still close when not showing
                if (owner != null) {
                    owner.showingProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal) Platform.runLater(() -> { if (s.isShowing()) s.close(); });
                    });
                }
            }

            s.show();
            if (!persistent) {
                // Auto-close after 4s
                new Thread(() -> {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException ignored) {}
                    Platform.runLater(s::close);
                }).start();
            }
        });
    }
}
