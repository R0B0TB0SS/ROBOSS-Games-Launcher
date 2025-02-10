package be.R0B0TB0SS.launcher.utils.instancesProfiles;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Objects;

public class InstanceData {
    private static final Path launcherDir = Launcher.getInstance().getLauncherDir();
    private static final Path gameDir = Path.of(launcherDir + "/versions/modded");
    private static final String instanceList = String.valueOf(Path.of(gameDir+"/instances.json"));

    public static String getValue(String type,String name) {
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(
                    new FileInputStream(instanceList)));
            JsonParser jsonParser = new JsonParser();
            JsonObject mainJsonObject = jsonParser.parse(reader).getAsJsonObject();

            JsonArray versionArray = (JsonArray) mainJsonObject.get("instances");
            for (Object o : versionArray) {
                JsonObject versionNumber = (JsonObject) o;
                String id = String.valueOf(versionNumber.get("name")).split("\"")[1];
                if (Objects.equals(id, name)) {
                    return String.valueOf(versionNumber.get(type)).split("\"")[1];
                }
            }
        } catch (Exception ex) {
            //this.logger.err(String.valueOf(ex));
        }
        return "Error";
    }
}
