package io.github.maths.shiny.extra.storage;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.extra.storage.impl.JSONProvider;
import io.github.maths.shiny.extra.storage.impl.MongoDBProvider;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.chat.CC;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StorageManager {

    private final ConfigurationFile config;
    private StorageProvider provider;
    private StorageProvider fallbackProvider;

    public StorageManager() {
        this.config = new ConfigurationFile(Shiny.getInstance(), "impl/storage.yml");
        this.fallbackProvider = new JSONProvider();
        this.setupProvider();
    }

    private void setupProvider() {
        String type = config.getString("type", "json").toLowerCase();

        switch (type) {
            case "mongodb":
            case "mongo":
                this.provider = new MongoDBProvider();
                break;
            case "json":
            default:
                this.provider = new JSONProvider();
                break;
        }
    }

    public CompletableFuture<Void> connect() {
        return provider.connect().thenCompose(v -> {
            if (!provider.isConnected() && provider != fallbackProvider) {
                CC.log("&cFalling back to JSON storage");
                this.provider = fallbackProvider;
                return fallbackProvider.connect();
            }
            return CompletableFuture.completedFuture(null);
        }).thenRun(() -> {
            CC.log("&f- &eConnected to " + provider.getType() + " storage");
        });
    }

    public CompletableFuture<Void> disconnect() {
        return provider.disconnect();
    }

    public boolean isConnected() {
        return provider.isConnected();
    }

    public String getStorageType() {
        return provider.getType();
    }

    public CompletableFuture<Boolean> savePlayerData(UUID playerId, String collection, Object data) {
        return provider.savePlayerData(playerId, collection, data);
    }

    public CompletableFuture<Object> loadPlayerData(UUID playerId, String collection, Class<?> type) {
        return provider.loadPlayerData(playerId, collection, type);
    }

    public CompletableFuture<Boolean> deletePlayerData(UUID playerId, String collection) {
        return provider.deletePlayerData(playerId, collection);
    }

    public CompletableFuture<Boolean> existsPlayerData(UUID playerId, String collection) {
        return provider.existsPlayerData(playerId, collection);
    }

    public CompletableFuture<Boolean> saveData(String collection, String key, Object data) {
        return provider.saveData(collection, key, data);
    }

    public CompletableFuture<Object> loadData(String collection, String key, Class<?> type) {
        return provider.loadData(collection, key, type);
    }

    public CompletableFuture<Boolean> deleteData(String collection, String key) {
        return provider.deleteData(collection, key);
    }

    public StorageProvider getProvider() {
        return provider;
    }
}