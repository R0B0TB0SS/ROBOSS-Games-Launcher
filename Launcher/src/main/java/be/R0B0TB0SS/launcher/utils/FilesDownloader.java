package be.R0B0TB0SS.launcher.utils;

import be.R0B0TB0SS.launcher.Launcher;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class FilesDownloader {

    public static void downloadFile(String source, String destinationFile)  {
        try {
            if(getFileMD5(destinationFile).equals(getURLMD5(source))){
                Launcher.getInstance().getLogger().info("File is up to date, skipping download.");
                return;
            }
            URL url = new URL(source);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

            int responseCode = httpConn.getResponseCode();

            // Check for 200 OK response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedInputStream bis = new BufferedInputStream(httpConn.getInputStream());
                     FileOutputStream fos = new FileOutputStream(destinationFile)) {

                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = bis.read(buffer, 0, 1024)) != -1) {
                        fos.write(buffer, 0, count);
                    }
                }
            } else {
                // Handle HTTP error codes
                Launcher.getInstance().getLogger().err("HTTP Error: " + responseCode + " - " + httpConn.getResponseMessage());
            }
            httpConn.disconnect();
        } catch (Exception e) {
            Launcher.getInstance().getLogger().err(e.toString());
        }
    }


    public static String getFileMD5(String filePath) throws Exception {
        // 1. Initialiser l'instance MessageDigest pour le MD5
        MessageDigest md = MessageDigest.getInstance("MD5");
        Path path = Paths.get(filePath);

        if (!Files.exists(path) || !Files.isRegularFile(path) || !Files.isReadable(path)) {
            return "";
        }

        // 2. Lire le fichier par morceaux (buffer)
        try (InputStream is = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192]; // Tampon de 8 Ko
            int read;
            while ((read = is.read(buffer)) > 0) {
                md.update(buffer, 0, read);
            }
        }

        // 3. Convertir les octets du hash en format Hexadécimal
        byte[] hashBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static String getURLMD5(String urlString) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");

        URL url = new URL(urlString);
        // 1. On utilise HttpURLConnection pour pouvoir modifier les propriétés
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 2. On définit un User-Agent de navigateur moderne
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

        // Optionnel : gérer les redirections (301/302)
        connection.setInstanceFollowRedirects(true);

        // 3. Vérifier le code de réponse avant de lire
        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            throw new Exception("Le serveur a répondu avec le code : " + status);
        }

        try (InputStream is = connection.getInputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
        } finally {
            connection.disconnect();
        }

        byte[] hashBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
