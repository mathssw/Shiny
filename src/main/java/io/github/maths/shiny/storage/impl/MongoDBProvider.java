package io.github.maths.shiny.storage.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.storage.StorageProvider;
import io.github.maths.shiny.utils.chat.CC;
import org.bson.Document;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoDBProvider implements StorageProvider {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private boolean connected = false;

    @Override
    public CompletableFuture<Void> connect() {
        return CompletableFuture.runAsync(() -> {
            try {
                var config = Shiny.getInstance().getStorageConfig();

                String host = config.getString("mongodb.host", "localhost");
                int port = config.getInt("mongodb.port", 27017);
                String databaseName = config.getString("mongodb.database", "Shiny");
                String username = config.getString("mongodb.username", "");
                String password = config.getString("mongodb.password", "");

                String connectionString;
                if (!username.isEmpty() && !password.isEmpty()) {
                    connectionString = String.format("mongodb://%s:%s@%s:%d/%s",
                            username, password, host, port, databaseName);
                } else {
                    connectionString = String.format("mongodb://%s:%d", host, port);
                }

                this.mongoClient = MongoClients.create(connectionString);
                this.database = mongoClient.getDatabase(databaseName);

                this.mongoClient.listDatabaseNames().first();
                this.connected = true;

                CC.log("&6Shiny &7- &eMongoDB connected to " + databaseName);

            } catch (Exception ex) {
                this.connected = false;
                ex.printStackTrace();
                CC.log("&cMongoDB connection failed, falling back to JSON");
            }
        });
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        return CompletableFuture.runAsync(() -> {
            if (mongoClient != null) {
                try {
                    mongoClient.close();
                    this.connected = false;
                    CC.log("&eClosed MongoDB connection");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean isConnected() {
        return connected && mongoClient != null;
    }

    @Override
    public String getType() {
        return "MongoDB";
    }

    @Override
    public CompletableFuture<Boolean> savePlayerData(UUID playerId, String collection, Object data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Document> coll = database.getCollection(collection);
                Document doc = Document.parse(data.toString());
                doc.put("_id", playerId.toString());

                coll.replaceOne(new Document("_id", playerId.toString()), doc,
                        new com.mongodb.client.model.ReplaceOptions().upsert(true));
                return true;
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
                MongoCollection<Document> coll = database.getCollection(collection);
                Document doc = coll.find(new Document("_id", playerId.toString())).first();

                if (doc != null) {
                    doc.remove("_id");
                    return doc.toJson();
                }
                return null;
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
                MongoCollection<Document> coll = database.getCollection(collection);
                return coll.deleteOne(new Document("_id", playerId.toString())).getDeletedCount() > 0;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> existsPlayerData(UUID playerId, String collection) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Document> coll = database.getCollection(collection);
                return coll.find(new Document("_id", playerId.toString())).first() != null;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> saveData(String collection, String key, Object data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<Document> coll = database.getCollection(collection);
                Document doc = Document.parse(data.toString());
                doc.put("_id", key);

                coll.replaceOne(new Document("_id", key), doc,
                        new com.mongodb.client.model.ReplaceOptions().upsert(true));
                return true;
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
                MongoCollection<Document> coll = database.getCollection(collection);
                Document doc = coll.find(new Document("_id", key)).first();

                if (doc != null) {
                    doc.remove("_id");
                    return doc.toJson();
                }
                return null;
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
                MongoCollection<Document> coll = database.getCollection(collection);
                return coll.deleteOne(new Document("_id", key)).getDeletedCount() > 0;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        });
    }
}