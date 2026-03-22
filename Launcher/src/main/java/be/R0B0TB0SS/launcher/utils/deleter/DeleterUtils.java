package be.R0B0TB0SS.launcher.utils.deleter;

import fr.flowarg.flowlogger.ILogger;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class DeleterUtils {

    private static final Path BACKUP_DIR = Path.of(System.getProperty("java.io.tmpdir"), "roboss_launcher_backup");

    /**
     * Sauvegarde les fichiers de la whitelist dans un dossier temporaire
     */
    public static void backupWhitelist(Path baseDir, List<String> whitelist, ILogger logger) {
        if (whitelist == null || whitelist.isEmpty()) return;

        try {
            if (Files.exists(BACKUP_DIR)) {
                deleteDirectoryRecursively(BACKUP_DIR); // Nettoyage ancien backup
            }
            Files.createDirectories(BACKUP_DIR);

            for (String pathStr : whitelist) {
                Path source = baseDir.resolve(pathStr);
                if (Files.exists(source)) {
                    Path destination = BACKUP_DIR.resolve(pathStr);
                    Files.createDirectories(destination.getParent());
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    logger.info("[Whitelist] Sauvegarde de : " + pathStr);
                }
            }
        } catch (IOException e) {
            logger.err("[Whitelist] Erreur backup : " + e.getMessage());
        }
    }

    /**
     * Restaure les fichiers sauvegardés vers le dossier du jeu
     */
    public static void restoreWhitelist(Path baseDir, List<String> whitelist, ILogger logger) {
        if (whitelist == null || whitelist.isEmpty()) return;

        for (String pathStr : whitelist) {
            Path source = BACKUP_DIR.resolve(pathStr);
            if (Files.exists(source)) {
                try {
                    Path destination = baseDir.resolve(pathStr);
                    Files.createDirectories(destination.getParent());
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    logger.info("[Whitelist] Restauration de : " + pathStr);
                } catch (IOException e) {
                    logger.err("[Whitelist] Erreur restauration " + pathStr + " : " + e.getMessage());
                }
            }
        }
    }

    public static void cleanupBlacklistedFiles(Path baseDir, List<String> blacklist, ILogger logger) {
        if (blacklist == null || blacklist.isEmpty()) return;
        for (String pathStr : blacklist) {
            try {
                Path path = baseDir.resolve(pathStr).toAbsolutePath().normalize();
                if (Files.exists(path)) {
                    Files.delete(path);
                    logger.info("[Blacklist] Supprimé : " + pathStr);
                }
            } catch (IOException e) {
                logger.err("[Blacklist] Erreur suppression " + pathStr + " : " + e.getMessage());
            }
        }
    }

    private static void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursively(entry);
                }
            }
        }
        Files.deleteIfExists(path);
    }
}