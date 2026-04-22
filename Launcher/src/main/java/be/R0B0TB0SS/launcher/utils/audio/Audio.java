package be.R0B0TB0SS.launcher.utils.audio;

import be.R0B0TB0SS.launcher.Main;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Audio {

    private static final Logger LOGGER = Logger.getLogger(Audio.class.getName());
    // hold reference so we can dispose previous players to avoid resource leaks
    private static MediaPlayer currentPlayer = null;

    public static void playAudio(String path) {
        // Ensure operation that touches JavaFX media APIs runs on JavaFX thread
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> playAudioInternal(path));
        } else {
            playAudioInternal(path);
        }
    }

    private static void playAudioInternal(String path) {
        try {
            Media media = createMediaFromPath(path);
            if (media == null) {
                LOGGER.warning("Could not create media for path: " + path);
                return;
            }

            // dispose previous player if needed
            if (currentPlayer != null) {
                try {
                    currentPlayer.stop();
                } catch (Exception ignored) {}
                try {
                    currentPlayer.dispose();
                } catch (Exception ignored) {}
                currentPlayer = null;
            }

            MediaPlayer mediaPlayer = new MediaPlayer(media);
            currentPlayer = mediaPlayer;

            mediaPlayer.setOnError(() -> LOGGER.log(Level.WARNING, "MediaPlayer error: {0}", mediaPlayer.getError()));
            media.setOnError(() -> LOGGER.log(Level.WARNING, "Media error: {0}", media.getError()));

            mediaPlayer.setOnEndOfMedia(() -> {
                try {
                    mediaPlayer.stop();
                } catch (Exception ignored) {}
                try {
                    mediaPlayer.dispose();
                } catch (Exception ignored) {}
                if (currentPlayer == mediaPlayer) currentPlayer = null;
            });

            mediaPlayer.play();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to play audio: " + path, e);
        }
    }

    private static Media createMediaFromPath(String path) {
        try {
            // First try loading as resource from classpath
            URL resource = Main.class.getResource(path);
            if (resource != null) {
                return new Media(resource.toExternalForm());
            }

            // If not found as resource, try as file system path
            File f = new File(path);
            if (f.exists()) {
                return new Media(f.toURI().toString());
            }

            // As last resort, try to load resource stream and copy to temp file (handles jar-packed resources with odd URIs)
            try (InputStream is = Main.class.getResourceAsStream(path)) {
                if (is != null) {
                    Path tmp = Files.createTempFile("roboss-audio-", ".tmp");
                    Files.copy(is, tmp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    tmp.toFile().deleteOnExit();
                    return new Media(tmp.toUri().toString());
                }
            } catch (IOException ioe) {
                LOGGER.log(Level.WARNING, "Failed to copy audio resource to temp file: " + path, ioe);
            }

            LOGGER.warning("Audio resource not found on classpath or filesystem: " + path);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating Media for path: " + path, e);
            return null;
        }
    }

    /**
     * Dispose any currently playing audio. Useful when shutting down the launcher.
     */
    public static void shutdown() {
        if (currentPlayer != null) {
            try { currentPlayer.stop(); } catch (Exception ignored) {}
            try { currentPlayer.dispose(); } catch (Exception ignored) {}
            currentPlayer = null;
        }
    }
}
