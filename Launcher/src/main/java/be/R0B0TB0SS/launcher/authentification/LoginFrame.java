package be.R0B0TB0SS.launcher.authentification;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CompletableFuture;

public class LoginFrame extends JFrame
{
    private Image image;
    private CompletableFuture<String> future;
    private boolean completed;

    private Image load(String name) {
        Image img = null;

        try {
            img = Toolkit.getDefaultToolkit().createImage(this.getClass().getClassLoader().getResource(name));
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return img;
    }


    public LoginFrame()
    {
        this.setTitle("Microsoft Authentication");
        this.setSize(750, 750);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        this.image = this.load("images/icon.png");
        ImageIcon icon = new ImageIcon(image);
        this.setIconImage(icon.getImage());

        this.setContentPane(new JFXPanel());
    }

    public CompletableFuture<String> start(String url)
    {
        if (this.future != null) {
            return this.future;
        }

        this.future = new CompletableFuture<>();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(!completed)
                    future.complete(null);
            }
        });

        Platform.runLater(() -> this.init(url));
        return this.future;
    }

    protected void init(String url)
    {
        WebView webView = new WebView();
        JFXPanel content = (JFXPanel) this.getContentPane();

        content.setScene(new Scene(webView, this.getWidth(), this.getHeight()));

        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("access_token")) {
                this.future.complete(newValue);
                completed = true;
                this.dispose();
            }
        });
        webView.getEngine().setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        webView.getEngine().load(url);

        this.setVisible(true);
    }
}
