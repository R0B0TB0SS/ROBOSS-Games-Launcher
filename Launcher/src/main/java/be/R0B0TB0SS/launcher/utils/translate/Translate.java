package be.R0B0TB0SS.launcher.utils.translate;

import be.R0B0TB0SS.launcher.Launcher;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.flowarg.flowupdater.utils.IOUtils;
import fr.theshark34.openlauncherlib.util.Saver;
import java.io.InputStream;
import java.util.Objects;

public class Translate {
    private static final Saver saver = Launcher.getInstance().getSaver();
    public static String getTranslate(String id) {
        String finalTranslate ="";
        try {
            InputStream inputStream = Translate.class.getClassLoader().getResourceAsStream("lang/lang.json");
            JsonObject mainJsonObject = IOUtils.readJson(inputStream).getAsJsonObject();
            JsonArray langArray = (JsonArray) mainJsonObject.get("translation");

            for (Object o : langArray) {
                JsonObject versionNumber = (JsonObject) o;
                String lang = String.valueOf(versionNumber.get("language")).split("\"")[1];
                if (Objects.equals(lang, saver.get("language"))) {
                   finalTranslate = String.valueOf(versionNumber.get(id)).split("\"")[1];
                }

            }
        } catch (Exception e) {
            System.out.println(e);
            finalTranslate = id;
        }
        return finalTranslate;
    }
}
