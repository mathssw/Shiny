package io.github.maths.shiny.storage.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.storage.StorageProvider;
import io.github.maths.shiny.utils.chat.CC;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JSONProvider implements StorageProvider {

    private final File dataFolder;
    private final Gson gson;
    private boolean connected = false;

    public JSONProvider() {
        this.dataFolder = new File(Shiny.getInstance().getDataFolder(), "data");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public CompletableFuture<Void> connect() {
        return CompletableFuture.runAsync(() -> {
            try {
                if (!dataFolder.exists()) {
                    dataFolder.mkdirs();
                }

                createDirectories();
                this.connected = true;
                CC.log("&6Shiny &7- &eJSON storage initialized");

            } catch (Exception ex) {
                ex.printStackTrace();
                CC.log("&cJSON storage initialization failed");
            }
        });
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        return CompletableFuture.runAsync(() -> {
            this.connected = false;
            CC.log("&eJSON storage disconnected");
        });
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public String getType() {
        return "JSON";
    }

    private void createDirectories() {
        new File(dataFolder, "highroller").mkdirs();
        new File(dataFolder, "battlepass").mkdirs();
        new File(dataFolder, "enderchest").mkdirs();
    }

    @Override
    public CompletableFuture<Boolean> savePlayerData(UUID playerId, String collection, Object data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File collectionDir = new File(dataFolder, collection);
                if (!collectionDir.exists()) {
                    collectionDir.mkdirs();
                }

                File file = new File(collectionDir, playerId.toString() + ".json");
                try (FileWriter writer = new FileWriter(file)) {
                    gson.toJson(data, writer);
                    return true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Object> loadPlayerData(UUID playerId, String collection, Class<?> type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File file = new File(new File(dataFolder, collection), playerId.toString() + ".json");
                if (!file.exists()) {
                    return null;
                }

                try (FileReader reader = new FileReader(file)) {
                    return gson.fromJson(reader, type);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deletePlayerData(UUID playerId, String collection) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File file = new File(new File(dataFolder, collection), playerId.toString() + ".json");
                return file.exists() && file.delete();
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> existsPlayerData(UUID playerId, String collection) {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(new File(dataFolder, collection), playerId.toString() + ".json");
            return file.exists();
        });
    }

    @Override
    public CompletableFuture<Boolean> saveData(String collection, String key, Object data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File collectionDir = new File(dataFolder, collection);
                if (!collectionDir.exists()) {
                    collectionDir.mkdirs();
                }

                File file = new File(collectionDir, key + ".json");
                try (FileWriter writer = new FileWriter(file)) {
                    gson.toJson(data, writer);
                    return true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Object> loadData(String collection, String key, Class<?> type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File file = new File(new File(dataFolder, collection), key + ".json");
                if (!file.exists()) {
                    return null;
                }

                try (FileReader reader = new FileReader(file)) {
                    return gson.fromJson(reader, type);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteData(String collection, String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File file = new File(new File(dataFolder, collection), key + ".json");
                return file.exists() && file.delete();
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        });
    }
}
