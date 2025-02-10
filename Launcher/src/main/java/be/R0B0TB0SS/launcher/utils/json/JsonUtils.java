package be.R0B0TB0SS.launcher.utils.json;

import be.R0B0TB0SS.launcher.Launcher;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    private static final Path launcherDir = Launcher.getInstance().getLauncherDir();
    private static final Path gameDir = Path.of(launcherDir + "/versions/modded");
    private static final String instanceList = String.valueOf(Path.of(gameDir+"/instances.json"));

    public static void modifyLatest(String instance){
        // Créer un objet Gson
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Lire le fichier JSON existant
        Map<String, Object> data = null;
        try (FileReader reader = new FileReader(instanceList)) {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            data = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data != null) {
            // Mettre à jour les données de la partie "latest"
            Map<String, String> latest = (Map<String, String>) data.get("latest");
            latest.put("name", instance);

            // Écrire les données mises à jour dans le fichier JSON
            try (FileWriter writer = new FileWriter(instanceList)) {
                gson.toJson(data, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Impossible de lire les données du fichier JSON.");
        }

    }
    public static void addInstance(String name,String type,String gameVersion,String projectID,String fileID){

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Lire le fichier JSON existant
        Map<String, Object> data = null;
        try (FileReader reader = new FileReader(instanceList)) {
            Type typetoken = new TypeToken<Map<String, Object>>(){}.getType();
            data = gson.fromJson(reader, typetoken);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data != null) {
            // Ajouter une nouvelle instance à l'array "instances"
            Map<String, String> nouvelleInstance = new HashMap<>();
            nouvelleInstance.put("name", name);
            nouvelleInstance.put("type", type);
            nouvelleInstance.put("gameVersion", gameVersion);
            nouvelleInstance.put("projectId", String.valueOf(projectID));
            nouvelleInstance.put("fileId", String.valueOf(fileID));

            List<Map<String, String>> instances = (List<Map<String, String>>) data.get("instances");
            instances.add(nouvelleInstance);

            // Écrire les données mises à jour dans le fichier JSON
            try (FileWriter writer = new FileWriter(instanceList)) {
                gson.toJson(data, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Impossible de lire les données du fichier JSON.");
        }

    }
    public static void addInstance(String name,String type,String gameVersion,String modloaderVersion){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Lire le fichier JSON existant
        Map<String, Object> data = null;
        try (FileReader reader = new FileReader(instanceList)) {
            Type typetoken = new TypeToken<Map<String, Object>>(){}.getType();
            data = gson.fromJson(reader, typetoken);
        } catch (IOException e) {e.printStackTrace();}

        if (data != null) {
            // Ajouter une nouvelle instance à l'array "instances"
            Map<String, String> nouvelleInstance = new HashMap<>();
            nouvelleInstance.put("name", name);
            nouvelleInstance.put("type", type);
            nouvelleInstance.put("gameVersion", gameVersion);
            nouvelleInstance.put("modloaderVersion", modloaderVersion);

            List<Map<String, String>> instances = (List<Map<String, String>>) data.get("instances");
            instances.add(nouvelleInstance);

            // Écrire les données mises à jour dans le fichier JSON
            try (FileWriter writer = new FileWriter(instanceList)) {
                gson.toJson(data, writer);
            } catch (IOException e) {e.printStackTrace();}
        } else {System.out.println("Impossible de lire les données du fichier JSON.");}
    }
    public static void removeInstance(String instance){
        // Créer un objet Gson
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Lire le fichier JSON existant
        Map<String, Object> data = null;
        try (FileReader reader = new FileReader(instanceList)) {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            data = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data != null) {
            // Nom de l'instance à supprimer
            String nomASupprimer = instance;

            // Supprimer l'instance de la liste "instances"
            List<Map<String, String>> instances = (List<Map<String, String>>) data.get("instances");
            Iterator<Map<String, String>> iterator = instances.iterator();
            while (iterator.hasNext()) {
                Map<String, String> selectInstance = iterator.next();
                if (nomASupprimer.equals(selectInstance.get("name"))) {
                    iterator.remove();
                }
            }

            // Écrire les données mises à jour dans le fichier JSON
            try (FileWriter writer = new FileWriter(instanceList)) {
                gson.toJson(data, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Impossible de lire les données du fichier JSON.");
        }
    }
    public static void modifyInstance(String name,String category,String data){
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            try (FileReader reader = new FileReader(instanceList)) {
                // Parse the JSON file
                JsonArray instances = JsonParser.parseReader(reader).getAsJsonArray();

                // Find and modify the matching instance
                for (JsonElement element : instances) {
                    JsonObject instance = element.getAsJsonObject();
                    if (instance.get("name").getAsString().equals(name)) {
                        instance.addProperty(category, data); // Modify the category with the provided data
                        break;
                    }
                }

                // Write the updated JSON array back to the file
                try (FileWriter writer = new FileWriter("path/to/your/file.json")) {
                    gson.toJson(instances, writer);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static boolean isNameUsed(String name) {
        // Créer un objet Gson
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Lire le fichier JSON existant
        Map<String, Object> data = null;
        try (FileReader reader = new FileReader(instanceList)) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            data = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data != null) {
            // Nom de l'instance à vérifier
            String nomAVerifier = name;

            // Vérifier si l'instance avec ce nom existe
            List<Map<String, String>> instances = (List<Map<String, String>>) data.get("instances");
            for (Map<String, String> instance : instances) {
                if (nomAVerifier.equals(instance.get("name"))) {
                    return true;
                }
            }
            return false;

        }
        return false;
    }
}