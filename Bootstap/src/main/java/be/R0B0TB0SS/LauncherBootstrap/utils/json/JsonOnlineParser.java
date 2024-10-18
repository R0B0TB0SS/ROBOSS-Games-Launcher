package be.R0B0TB0SS.LauncherBootstrap.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonOnlineParser {
        public static final Gson GSON = (new GsonBuilder()).create();

    public static JsonObject parse(String urlString) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null)
                    response.append(responseLine.trim());
                return (new JsonParser()).parse(response.toString()).getAsJsonObject();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

