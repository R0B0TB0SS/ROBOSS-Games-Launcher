package be.R0B0TB0SS.launcher.utils.instancesProfiles;

import be.R0B0TB0SS.launcher.Launcher;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class InstanceData {
    private static final Path launcherDir = Launcher.getInstance().getLauncherDir();
    // Using .resolve() is the modern way to handle paths instead of string concatenation
    private static final Path gameDir = launcherDir.resolve("versions").resolve("instances");
    private static final Path instanceListPath = gameDir.resolve("instances.json");

    public static String getValue(String type, String name) {
        // Use try-with-resources to ensure the reader is closed automatically
        try (JsonReader reader = new JsonReader(Files.newBufferedReader(instanceListPath, StandardCharsets.UTF_8))) {

            // Deprecated: new JsonParser().parse(reader)
            // Modern: JsonParser.parseReader(reader)
            JsonObject mainJsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            JsonArray versionArray = mainJsonObject.getAsJsonArray("instances");

            for (JsonElement element : versionArray) {
                JsonObject instanceObj = element.getAsJsonObject();

                // Get name as a clean string without manual splitting/quotes
                String id = instanceObj.get("name").getAsString();

                if (Objects.equals(id, name)) {
                    // Returns the specific field requested (e.g., "version", "path")
                    return instanceObj.get(type).getAsString();
                }
            }
        } catch (IOException | IllegalStateException | NullPointerException e) {
            // Log the error or handle it appropriately
            System.err.println("[InstanceData] Error reading JSON: " + e.getMessage());
        }
        return "Error";
    }
}
