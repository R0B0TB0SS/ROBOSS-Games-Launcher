package be.R0B0TB0SS.launcher.utils.account;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.utils.desktop.Notification;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.theshark34.openlauncherlib.util.Saver;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;



public class MgAccount {
    private static final Saver saver = Launcher.getInstance().getSaver();
    private static final Path instancedir = Path.of(Launcher.getInstance().getLauncherDir().toUri());

    public static void createAccountFile(){
        try{
            // s'assure que le dossier d'instance existe
            Files.createDirectories(instancedir);

            Path local = instancedir.resolve("account.json");

            if (Files.exists(local)){
                // si le fichier existe, on peut valider qu'il contient au moins la clé accounts
                String jsonStr = Files.readString(local);
                try{
                    com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(jsonStr).getAsJsonObject();
                    if (!obj.has("accounts")){
                        // Pas de clé accounts : on crée un objet vide pour stocker les comptes par username
                        obj.add("accounts", new com.google.gson.JsonObject());
                        String pretty = new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(obj);
                        Files.writeString(local, pretty);
                    } else {
                        // Si la clé existe, mais est un tableau (ancien format), on tente une migration
                        if (obj.get("accounts").isJsonArray()){
                            com.google.gson.JsonArray oldArr = obj.getAsJsonArray("accounts");
                            com.google.gson.JsonObject newObj = new com.google.gson.JsonObject();
                            for (com.google.gson.JsonElement el : oldArr){
                                if (el.isJsonObject()){
                                    com.google.gson.JsonObject accountObj = el.getAsJsonObject();
                                    if (accountObj.has("username")){
                                        String uname = accountObj.get("username").getAsString();
                                        newObj.add(uname, accountObj);
                                    }
                                }
                            }
                            obj.add("accounts", newObj);
                            String pretty = new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(obj);
                            Files.writeString(local, pretty);
                        }
                    }
                } catch (Exception ex){
                    // contenu invalide -> on recrée une structure propre
                    com.google.gson.JsonObject root = new com.google.gson.JsonObject();
                    root.add("accounts", new com.google.gson.JsonObject());
                    String pretty = new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(root);
                    Files.writeString(local, pretty);
                }

            } else {
                // création d'un nouveau fichier account.json avec structure de base
                com.google.gson.JsonObject root = new com.google.gson.JsonObject();
                root.add("accounts", new com.google.gson.JsonObject());

                String pretty = new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(root);
                Files.writeString(local, pretty);

                // Notification de succès (texte simple en français)
                Notification.sendSystemNotification("Fichier account.json créé", TrayIcon.MessageType.INFO);
            }
        } catch (Exception e){
            Launcher.getInstance().getLogger().printStackTrace(e);
            Notification.sendSystemNotification(Translate.getTranslate("generic.unable_to_load"), TrayIcon.MessageType.ERROR);
        }
    }

    public static String[] listAccount(){
        String[] accounts = new String[0];
        try {
            Path local = instancedir.resolve("account.json");

            // Si le fichier n'existe pas, on le crée et on retourne une liste vide
            if (!Files.exists(local)) {
                createAccountFile();
                return accounts;
            }

            String jsonStr = Files.readString(local);
            com.google.gson.JsonObject object = com.google.gson.JsonParser.parseString(jsonStr).getAsJsonObject();

            if (!object.has("accounts") || !object.get("accounts").isJsonArray()) {
                if (object.get("accounts").isJsonObject()){
                    com.google.gson.JsonObject map = object.getAsJsonObject("accounts");
                    for (String key : map.keySet()){
                        JsonObject accountObj = map.getAsJsonObject(key);
                        String[] newArr = new String[accounts.length + 1];
                        System.arraycopy(accounts, 0, newArr, 0, accounts.length);
                        newArr[newArr.length - 1] = accountObj.get("username").getAsString();
                        accounts = newArr;
                    }
                }
            }

        } catch (Exception ex) {
            Launcher.getInstance().getLogger().printStackTrace(ex);
            Notification.sendSystemNotification(Translate.getTranslate("generic.unable_to_load_account"), TrayIcon.MessageType.ERROR);
        }

        return accounts;
    }

    public static void setAccount(String username){
        saver.remove("username");
        saver.remove("offline-username");
        saver.remove("UUID");
        saver.remove("msAccessToken");
        saver.remove("msRefreshToken");
        saver.remove("acc_type");
        saver.save();

        try {
            Path local = instancedir.resolve("account.json");
            if (Files.exists(local)) {
                String jsonStr = Files.readString(local);
                com.google.gson.JsonObject object = com.google.gson.JsonParser.parseString(jsonStr).getAsJsonObject();
                if (!object.has("accounts") || !object.get("accounts").isJsonObject()){
                    Notification.sendSystemNotification(Translate.getTranslate("generic.unable_to_load_account"), TrayIcon.MessageType.ERROR);
                    return;
                }

                com.google.gson.JsonObject accountsObj = object.getAsJsonObject("accounts");
                if (!accountsObj.has(username)){
                    Notification.sendSystemNotification(Translate.getTranslate("account.not_found"), TrayIcon.MessageType.ERROR);
                    return;
                }

                com.google.gson.JsonObject sel = accountsObj.getAsJsonObject(username);

                String accType = sel.has("acc_type") ? sel.get("acc_type").getAsString() : sel.has("account_type") ? sel.get("account_type").getAsString() : "offline";

                if ("online".equalsIgnoreCase(accType)){
                    saver.set("acc_type", "online");
                    if (sel.has("username")) saver.set("username", sel.get("username").getAsString());
                    if (sel.has("UUID")) saver.set("UUID", sel.get("UUID").getAsString());
                    if (sel.has("msAccessToken")) saver.set("msAccessToken", sel.get("msAccessToken").getAsString());
                    if (sel.has("msRefreshToken")) saver.set("msRefreshToken", sel.get("msRefreshToken").getAsString());
                    saver.save();
                    Launcher.restart();
                } else {
                    // offline
                    saver.set("acc_type", "offline");
                    if (sel.has("username")) saver.set("offline-username", sel.get("username").getAsString());
                    if (sel.has("UUID")) saver.set("UUID", sel.get("UUID").getAsString());
                    saver.save();
                    Launcher.restart();
                }

            } else {
                Notification.sendSystemNotification(Translate.getTranslate("generic.unable_to_load"), TrayIcon.MessageType.ERROR);
            }
        } catch (Exception ex) {
            Launcher.getInstance().getLogger().printStackTrace(ex);
            Notification.sendSystemNotification(Translate.getTranslate("generic.unable_to_load"), TrayIcon.MessageType.ERROR);
        }

    }

    public static void deleteAccount(String username){

        Path local = instancedir.resolve("account.json");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            // Lire le fichier JSON
            String content = Files.readString(local);
            JsonObject root = gson.fromJson(content, JsonObject.class);

            // Vérifier que "accounts" existe
            if (root.has("accounts") && root.get("accounts").isJsonObject()) {
                JsonObject accounts = root.getAsJsonObject("accounts");

                // Supprimer l'utilisateur
                if (accounts.has(username)) {
                    accounts.remove(username);
                    System.out.println("Compte supprimé : " + username);
                } else {
                    System.out.println("Utilisateur introuvable : " + username);
                }
            } else {
                System.out.println("Structure JSON invalide (accounts manquant)");
            }

            // Réécrire le fichier
            Files.writeString(local, gson.toJson(root));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addAccount(String username){

        try{
            if (username == null || username.isBlank()){
                Notification.sendSystemNotification("Nom d'utilisateur invalide", TrayIcon.MessageType.ERROR);
                return;
            }

            Path local = instancedir.resolve("account.json");
            if (!Files.exists(local)){
                createAccountFile();
            }

            String jsonStr = Files.readString(local);
            com.google.gson.JsonObject root = com.google.gson.JsonParser.parseString(jsonStr).getAsJsonObject();

            if (!root.has("accounts") || !root.get("accounts").isJsonObject()){
                // migrate if necessary
                createAccountFile();
                jsonStr = Files.readString(local);
                root = com.google.gson.JsonParser.parseString(jsonStr).getAsJsonObject();
            }

            com.google.gson.JsonObject accounts = root.getAsJsonObject("accounts");

            com.google.gson.JsonObject accountObj = new com.google.gson.JsonObject();
            accountObj.addProperty("username", username);
            accountObj.addProperty("UUID", java.util.UUID.randomUUID().toString());
            accountObj.addProperty("acc_type", "offline");

            accounts.add(username, accountObj);
            root.add("accounts", accounts);

            String pretty = new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(root);
            Files.writeString(local, pretty);

            Notification.sendSystemNotification(Translate.getTranslate("account.added"), TrayIcon.MessageType.INFO);

        } catch (Exception e){
            Launcher.getInstance().getLogger().printStackTrace(e);
            Notification.sendSystemNotification(Translate.getTranslate("generic.unable_to_modify"), TrayIcon.MessageType.ERROR);
        }

    }

    public static void addAccount(String username,String UUID,String msAccessToken, String msRefreshToken){

        try{
            if (username == null || username.isBlank()){
                Notification.sendSystemNotification("Nom d'utilisateur invalide", TrayIcon.MessageType.ERROR);
                return;
            }

            Path local = instancedir.resolve("account.json");
            if (!Files.exists(local)){
                createAccountFile();
            }

            String jsonStr = Files.readString(local);
            com.google.gson.JsonObject root = com.google.gson.JsonParser.parseString(jsonStr).getAsJsonObject();

            if (!root.has("accounts") || !root.get("accounts").isJsonObject()){
                // migrate if necessary
                createAccountFile();
                jsonStr = Files.readString(local);
                root = com.google.gson.JsonParser.parseString(jsonStr).getAsJsonObject();
            }

            com.google.gson.JsonObject accounts = root.getAsJsonObject("accounts");

            com.google.gson.JsonObject accountObj = new com.google.gson.JsonObject();
            accountObj.addProperty("username", username);
            if (UUID != null) accountObj.addProperty("UUID", UUID);
            accountObj.addProperty("acc_type", "online");
            if (msAccessToken != null) accountObj.addProperty("msAccessToken", msAccessToken);
            if (msRefreshToken != null) accountObj.addProperty("msRefreshToken", msRefreshToken);

            accounts.add(username, accountObj);
            root.add("accounts", accounts);

            String pretty = new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(root);
            Files.writeString(local, pretty);

            Notification.sendSystemNotification(Translate.getTranslate("account.added"), TrayIcon.MessageType.INFO);

        } catch (Exception e){
            Launcher.getInstance().getLogger().printStackTrace(e);
            Notification.sendSystemNotification(Translate.getTranslate("generic.unable_to_modify"), TrayIcon.MessageType.ERROR);
        }

    }

    public static String getCurrentAccount(){
        if (Objects.equals(saver.get("acc_type"), "online")){
            return saver.get("username");
        }else{
            return saver.get("offline-username");
        }
    }

    public static String nextAccount() {
        try{
            Path local = instancedir.resolve("account.json");

            if (!Files.exists(local)){
                createAccountFile();
                return null;
            }

            String jsonStr = Files.readString(local);
            com.google.gson.JsonObject root = com.google.gson.JsonParser.parseString(jsonStr).getAsJsonObject();

            if (!root.has("accounts")){
                return null;
            }

            com.google.gson.JsonElement accountsEl = root.get("accounts");

            // Nouveau format : accounts est un objet mappant username -> accountObj
            if (accountsEl.isJsonObject()){
                com.google.gson.JsonObject map = accountsEl.getAsJsonObject();
                for (String key : map.keySet()){
                    // retourne la première clé (username)
                    return key;
                }
                return null;
            }

            // Ancien format : accounts est un tableau d'objets
            if (accountsEl.isJsonArray()){
                com.google.gson.JsonArray arr = accountsEl.getAsJsonArray();
                if (!arr.isEmpty()){
                    com.google.gson.JsonElement first = arr.get(0);
                    if (first.isJsonObject()){
                        com.google.gson.JsonObject obj = first.getAsJsonObject();
                        if (obj.has("username")){
                            return obj.get("username").getAsString();
                        }
                    }
                }
                return null;
            }

        } catch (Exception e){
            Launcher.getInstance().getLogger().printStackTrace(e);
            Notification.sendSystemNotification(Translate.getTranslate("generic.unable_to_load_account"), TrayIcon.MessageType.ERROR);
        }

        return null;
    }

    public static boolean hasAccount(String username){
        try{
            Path local = instancedir.resolve("account.json");
            if (!Files.exists(local)){
                createAccountFile();
                return false;
            }

            String jsonStr = Files.readString(local);
            com.google.gson.JsonObject root = com.google.gson.JsonParser.parseString(jsonStr).getAsJsonObject();

            if (!root.has("accounts")){
                return false;
            }

            com.google.gson.JsonElement accountsEl = root.get("accounts");

            if (accountsEl.isJsonObject()){
                com.google.gson.JsonObject map = accountsEl.getAsJsonObject();
                return map.has(username);
            }

            if (accountsEl.isJsonArray()){
                com.google.gson.JsonArray arr = accountsEl.getAsJsonArray();
                for (com.google.gson.JsonElement el : arr){
                    if (el.isJsonObject()){
                        com.google.gson.JsonObject obj = el.getAsJsonObject();
                        if (obj.has("username") && username.equals(obj.get("username").getAsString())){
                            return true;
                        }
                    }
                }
                return false;
            }

        }catch (Exception e){
            Launcher.getInstance().getLogger().printStackTrace(e);
            Notification.sendSystemNotification(Translate.getTranslate("generic.unable_to_load_account"), TrayIcon.MessageType.ERROR);
            return false;
        }

        return false;
    }
}
