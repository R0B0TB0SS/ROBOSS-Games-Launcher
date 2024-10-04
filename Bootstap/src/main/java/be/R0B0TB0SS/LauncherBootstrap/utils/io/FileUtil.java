package be.R0B0TB0SS.LauncherBootstrap.utils.io;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.URL;

public class FileUtil {
    public static boolean checkSha1(File file, String sha1) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        String fileSha1 = DigestUtils.sha1Hex(fileInputStream);
        fileInputStream.close();
        return fileSha1.equals(sha1);
    }

    public static boolean downloadFile(String url, File file) {
        try {
            org.apache.commons.io.FileUtils.copyURLToFile(new URL(url), file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}

