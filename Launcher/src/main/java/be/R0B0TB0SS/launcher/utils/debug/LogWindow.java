package be.R0B0TB0SS.launcher.utils.debug;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LogWindow {

    private TextFlow textFlow = null;
    private ScrollPane scrollPane = null;
    private final StringBuilder logs = new StringBuilder();
    private Stage stage = null;
    private final String title;
    private final List<Thread> threads = Collections.synchronizedList(new ArrayList<>());
    private final List<Closeable> readers = Collections.synchronizedList(new ArrayList<>());
    private volatile boolean running = true;

        public LogWindow(String title) {
        this.title = title;
    }

    public void show() {
        Platform.runLater(() -> {
            try {
                // ensure showing the log window won't allow the JVM to exit when it becomes the
                // only visible window (we handle re-enabling implicit exit elsewhere)
                Platform.setImplicitExit(false);
                if (stage == null) initUI();
                stage.show();
            } catch (Exception ignored) {}
        });

    }

    private void initUI() {
        // must be called on FX thread
        if (stage != null) return;

        stage = new Stage();
        stage.setTitle(this.title);

        // When the user clicks the window close button, do not exit the whole application.
        // Instead consume the close request, stop log readers/threads and hide the window.
        stage.setOnCloseRequest(event -> {
            try {
                event.consume();
                // ensure implicit exit is disabled so hiding this window won't exit the app
                Platform.setImplicitExit(false);
                // stop background readers/threads and close their streams
                cleanupResources();
                // only hide the stage to avoid implicit JVM exit when this is the last window
                stage.hide();
            } catch (Exception ignored) {}
        });

        try {
            stage.getIcons().add(new javafx.scene.image.Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.png"))
            ));
        } catch (Exception ignored) {}

        textFlow = new TextFlow();
        textFlow.setStyle("-fx-background-color: #111;");

        scrollPane = new ScrollPane(textFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #111;");

        Button exportBtn = new Button("📤 Export mclo.gs");
        exportBtn.setOnAction(e -> exportLogs());

        HBox topBar = new HBox(exportBtn);
        topBar.setPadding(new Insets(5));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(scrollPane);

        stage.setScene(new Scene(root, 900, 550));

        // flush stored logs into UI
        synchronized (logs) {
            if (!logs.isEmpty()) {
                String[] lines = logs.toString().split("\n");
                for (String l : lines) {
                    if (l == null || l.isEmpty()) continue;
                    Text t = new Text(l + "\n");
                    t.setStyle("-fx-font-family: Consolas; -fx-font-size: 12;");
                    if (l.contains("ERROR") || l.contains("Exception")) {
                        t.setFill(Color.RED);
                    } else if (l.contains("WARN")) {
                        t.setFill(Color.ORANGE);
                    } else if (l.contains("INFO")) {
                        t.setFill(Color.LIMEGREEN);
                    } else {
                        t.setFill(Color.WHITE);
                    }
                    textFlow.getChildren().add(t);
                }
                scrollPane.setVvalue(1.0);
            }
        }
    }
    // 🎨 Ajout de log avec couleurs
    public void log(String line) {
        synchronized (logs) {
            logs.append(line).append("\n");
        }

        // If UI is initialized, post to FX thread to display
        if (textFlow != null && scrollPane != null) {
            Platform.runLater(() -> {
                try {
                    Text text = new Text(line + "\n");
                    text.setStyle("-fx-font-family: Consolas; -fx-font-size: 12;");

                    if (line.contains("ERROR") || line.contains("Exception")) {
                        text.setFill(Color.RED);
                    } else if (line.contains("WARN")) {
                        text.setFill(Color.ORANGE);
                    } else if (line.contains("INFO")) {
                        text.setFill(Color.LIMEGREEN);
                    } else {
                        text.setFill(Color.WHITE);
                    }

                    textFlow.getChildren().add(text);
                    scrollPane.setVvalue(1.0);
                } catch (Exception ignored) {}
            });
        }
    }

    public void attachToProcess(Process process) {
        try {
            BufferedReader inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            readers.add(inReader);
            readers.add(errReader);

            Thread t1 = new Thread(() -> read(inReader));
            t1.setDaemon(true);
            threads.add(t1);
            t1.start();

            Thread t2 = new Thread(() -> read(errReader));
            t2.setDaemon(true);
            threads.add(t2);
            t2.start();
        } catch (Exception e) {
            log("[ERROR] attachToProcess: " + e.getMessage());
        }
    }

    public void close(){
        // cleanup resources and close the UI
        cleanupResources();
        Platform.runLater(() -> {
            try { stage.close(); } catch (Exception ignored) {}
        });
    }

    /**
     * Stop readers/threads but do not call Stage.close() so the application doesn't exit
     * when the user closes the log window via titlebar close button.
     */
    private void cleanupResources() {
        // mark stop
        running = false;

        // close registered readers (will unblock readLine)
        for (Closeable r : readers) {
            try { r.close(); } catch (IOException ignored) {}
        }

        // interrupt threads
        for (Thread t : threads) {
            try { t.interrupt(); } catch (Exception ignored) {}
        }
    }

    public void attachToLogFile(File logFile) {
        new Thread(() -> {
            try {
                long lastSize = 0;

                while (running) {
                    if (logFile.exists()) {
                        try (RandomAccessFile file = new RandomAccessFile(logFile, "r")) {
                            file.seek(lastSize);

                            String line;
                            while ((line = file.readLine()) != null) {
                                log(new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                            }

                            lastSize = file.length();
                        }
                    }

                    try { Thread.sleep(500); } catch (InterruptedException ie) { break; }
                }
            } catch (Exception e) {
                log("[ERROR] Log reader: " + e.getMessage());
            }
        }).start();
    }

    private void read(BufferedReader reader) {
        try (reader) {
            try {
                String line;
                while (running && (line = reader.readLine()) != null) {
                    log(line);
                }
            } catch (Exception e) {
                if (running) log("[ERROR] " + e.getMessage());
            }
        } catch (IOException ignored) {
        }
    }

    // 🌐 Export vers mclo.gs
    private void exportLogs() {
        new Thread(() -> {
            try {
                URL url = new URI("https://api.mclo.gs/1/log").toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String data = "content=" + java.net.URLEncoder.encode(logs.toString(), StandardCharsets.UTF_8);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(data.getBytes());
                }

                int responseCode = conn.getResponseCode();

                BufferedReader reader;
                if (responseCode >= 200 && responseCode < 300) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                String response = reader.readLine();

                if (response == null || !response.contains("url")) {
                    log("[ERROR] Invalid response: " + response);
                    return;
                }

                String link = response.split("\"url\":\"")[1].split("\"")[0];

                Desktop.getDesktop().browse(new URI(link));

                log("[INFO] Logs uploaded: " + link);

            } catch (Exception e) {
                log("[ERROR] Export failed: " + e.getMessage());
            }
        }).start();
    }
}