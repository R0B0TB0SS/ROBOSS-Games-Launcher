package be.R0B0TB0SS.launcher.utils.translate;

import be.R0B0TB0SS.launcher.Launcher;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.flowarg.flowupdater.utils.IOUtils;
import fr.theshark34.openlauncherlib.util.Saver;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Translate {
    private static final Saver saver = Launcher.getInstance().getSaver();

    public static String getLanguage(){
        String file = saver.get("language");
        final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
        String language = date+"[ROBOSS Games launcher] [WARN]: Missing lang: \""+file+"\"";
        try {
            InputStream inputStream = Translate.class.getClassLoader().getResourceAsStream("lang/lang.json");
            JsonObject mainJsonObject = IOUtils.readJson(inputStream).getAsJsonObject();
            JsonArray langArray = (JsonArray) mainJsonObject.get("lang");
            for (Object o : langArray) {
                JsonObject langlist = (JsonObject) o;
                String arlang = String.valueOf(langlist.get("file")).split("\"")[1];
                if (Objects.equals(arlang,file)) {
                    language = String.valueOf(langlist.get("language")).split("\"")[1];
                }
            }
        } catch (Exception ignored) {
        }
        return language;
    }
    public static Boolean isLanguageExist(String language){
        boolean res = Boolean.FALSE;
        InputStream inputStream = Translate.class.getClassLoader().getResourceAsStream("lang/lang.json");
        JsonObject mainJsonObject = IOUtils.readJson(inputStream).getAsJsonObject();
        JsonArray langArray = (JsonArray) mainJsonObject.get("lang");
        for (Object o : langArray) {
            JsonObject langlist = (JsonObject) o;
            String arlang = String.valueOf(langlist.get("file")).split("\"")[1];
            String arfile = String.valueOf(langlist.get("language")).split("\"")[1];
            if (Objects.equals(arlang, language) | Objects.equals(arfile,language)) {
                res = Boolean.TRUE;
            }
        }
        return res;
    }
    public static String getTranslate(String id) {
        String file = saver.get("language");

        String finalTranslate ="";
        if(!isLanguageExist(file)){
            final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
            System.out.println(date+"[ROBOSS Games launcher] [WARN]: Missing Key: \""+id+"\" in lang file.");
            finalTranslate = id;
            return finalTranslate;
        }
        try {
            InputStream inputStream = Translate.class.getClassLoader().getResourceAsStream("lang/"+file+".json");
            JsonObject mainJsonObject = IOUtils.readJson(inputStream).getAsJsonObject();
            finalTranslate = String.valueOf(mainJsonObject.get(id)).split("\"")[1];
        } catch (Exception e) {
            final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
            System.out.println(date+"[ROBOSS Games launcher] [WARN]: Missing Key: \""+id+"\" in lang file.");
            finalTranslate = id;
        }
        return finalTranslate;
    }
    public static ArrayList<Object> languageList() {
        ArrayList<Object> res = new ArrayList<>();
        try (InputStream inputStream = Translate.class.getClassLoader().getResourceAsStream("lang/lang.json")) {
            // Check if the inputStream is null before attempting to read from it
            if (inputStream == null) {
                System.err.println("Error: lang/lang.json not found.");
                return res; // Return an empty list or handle the error
            }
            JsonObject mainJsonObject = IOUtils.readJson(inputStream).getAsJsonObject();
            JsonArray langArray = mainJsonObject.getAsJsonArray("lang");
            if (langArray != null) {
                for (Object o : langArray) {
                    JsonObject langlist = (JsonObject) o;
                    String arlang = String.valueOf(langlist.get("language")).split("\"")[1];
                    res.add(arlang);
                }
            }
        } catch (Exception e) {
            // Log the exception to get more details on what went wrong
            e.printStackTrace();
        }
        return res;
    }
    public static String getFileName(String language){
        String res = "en_us";
        if(!isLanguageExist(language)){
            res = "en_us";
        }else{
            InputStream inputStream = Translate.class.getClassLoader().getResourceAsStream("lang/lang.json");
            JsonObject mainJsonObject = IOUtils.readJson(inputStream).getAsJsonObject();
            JsonArray langArray = (JsonArray) mainJsonObject.get("lang");
            for (Object o : langArray) {
                JsonObject langlist = (JsonObject) o;
                String arlang = String.valueOf(langlist.get("language")).split("\"")[1];
                if (Objects.equals(arlang, language)) {
                    res = String.valueOf(langlist.get("file")).split("\"")[1];
                }
            }
        }
        return res;

    }
}
