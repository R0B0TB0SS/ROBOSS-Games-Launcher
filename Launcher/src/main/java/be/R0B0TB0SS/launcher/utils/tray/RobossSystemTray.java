package be.R0B0TB0SS.launcher.utils.tray;

import be.R0B0TB0SS.launcher.utils.os.OsCheck;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

public class RobossSystemTray {

    private static final Logger log = LoggerFactory.getLogger(RobossSystemTray.class);
    private static TrayIcon trayIcon;

    private static JPopupMenu trayMenu;

    private static Image load() {
        Image img = null;

        try {
            img = Toolkit.getDefaultToolkit().createImage(RobossSystemTray.class.getClassLoader().getResource("images/icon.png"));
        } catch (Exception var4) {
            log.error("e: ", var4);
        }
        return img;
    }

    public static void create() {

        try {
            Image image = load();

            ImageIcon icon = new ImageIcon(image);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            trayMenu = new JPopupMenu();
            trayMenu.setBackground(Color.red);
            trayMenu.setBorderPainted(false);
            JMenuItem appdataItem = new JMenuItem("AppData");
            appdataItem.addActionListener(e -> {
                File appdata = OsCheck.getAppData();
                if (appdata.exists())
                    try {
                        Desktop.getDesktop().open(appdata);
                    } catch (Exception ex) {
                        log.error("e: ", ex);
                    }
            });
            trayMenu.add(appdataItem);
            JMenuItem dataItem = new JMenuItem("LauncherData");
            dataItem.addActionListener(e -> {
                File install = new File(OsCheck.getAppData(), "ROBOSS-games");
                if (install.exists())
                    try {
                        Desktop.getDesktop().open(install);
                    } catch (Exception ex) {
                        log.error("e: ", ex);
                    }
            });
            trayMenu.add(dataItem);
            trayMenu.addSeparator();
            JMenuItem siteItem = new JMenuItem(Translate.getTranslate("tray.website"));
            siteItem.addActionListener(e -> {
                try {
                    Desktop.getDesktop().browse((new URL("https://www.robotboss.org/")).toURI());
                } catch (Exception ex) {
                    log.error("e: ", ex);
                }
            });
            trayMenu.add(siteItem);
            trayMenu.addSeparator();
            JMenuItem exitItem = new JMenuItem(Translate.getTranslate("tray.exit"));
            exitItem.addActionListener(e -> System.exit(0));
            trayMenu.add(exitItem);
            if (SystemTray.isSupported()) {
                trayIcon = new TrayIcon(icon.getImage(), "ROBOSS Games Launcher");
                trayIcon.addMouseListener(new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        if (e.isPopupTrigger() && !RobossSystemTray.trayMenu.isVisible()) {
                            RobossSystemTray.trayMenu.setLocation(e.getX(), e.getY());
                            RobossSystemTray.trayMenu.setInvoker(RobossSystemTray.trayMenu);
                            RobossSystemTray.trayMenu.setVisible(true);
                        }
                    }
                });
                Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
                    if (event instanceof MouseEvent mouseEvent) {
                        if (mouseEvent.getID() == 502 && !mouseEvent.getSource().equals(trayMenu) && !mouseEvent.getSource().equals(trayIcon))
                            trayMenu.setVisible(false);
                    }
                },16L);
                trayIcon.setImageAutoSize(true);
                trayIcon.setToolTip("ROBOSS Games Launcher");
                SystemTray.getSystemTray().add(trayIcon);
            }
        } catch (Exception e) {
            log.error("e: ", e);
        }

    }
    public static TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public static JPopupMenu getTrayMenu() {
        return trayMenu;
    }
}
