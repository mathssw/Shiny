package io.github.maths.shiny.storage;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageProvider {

    CompletableFuture<Void> connect();
    CompletableFuture<Void> disconnect();
    boolean isConnected();
    String getType();

    CompletableFuture<Boolean> savePlayerData(UUID playerId, String collection, Object data);
    CompletableFuture<Object> loadPlayerData(UUID playerId, String collection, Class<?> type);
    CompletableFuture<Boolean> deletePlayerData(UUID playerId, String collection);
    CompletableFuture<Boolean> existsPlayerData(UUID playerId, String collection);

    CompletableFuture<Boolean> saveData(String collection, String key, Object data);
    CompletableFuture<Object> loadData(String collection, String key, Class<?> type);
    CompletableFuture<Boolean> deleteData(String collection, String key);
}