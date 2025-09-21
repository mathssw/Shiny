package io.github.maths.shiny.managers;

import io.github.maths.shiny.Shiny;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HighrollerManager {

    public HighrollerManager() {
        // Cargar prefixes al iniciar
        loadPrefixes();
    }

    public CompletableFuture<Void> savePrefixes(final Map<OfflinePlayer, String> prefixes) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.allOf(prefixes.entrySet().stream()
                        .map(entry -> savePrefix(entry.getKey(), entry.getValue()))
                        .toArray(CompletableFuture[]::new))
                .thenRun(() -> future.complete(null))
                .exceptionally(ex -> {
                    future.completeExceptionally(ex);
                    return null;
                });

        return future;
    }

    public CompletableFuture<Void> loadPrefixes() {
        // Los prefijos se cargan bajo demanda cuando se necesitan
        return CompletableFuture.completedFuture(null);
    }

    public String getType(final Player player) {
        if (!Shiny.getInstance().prefixes.containsKey(player)) {
            return "Default";
        }
        return Shiny.getInstance().prefixes.get(player);
    }

    public CompletableFuture<Void> loadPrefix(final Player player) {
        return Shiny.getInstance().getStorageManager().loadPlayerData(
                player.getUniqueId(),
                "highroller",
                String.class
        ).thenAccept(prefix -> {
            if (prefix != null) {
                Shiny.getInstance().prefixes.put(player, prefix.toString());
            }
        });
    }

    public CompletableFuture<Boolean> savePrefix(final OfflinePlayer player, final String prefix) {
        if (player != null && player.getName() != null) {
            Shiny.getInstance().prefixes.put(player, prefix);
            return Shiny.getInstance().getStorageManager().savePlayerData(
                    player.getUniqueId(),
                    "highroller",
                    prefix
            );
        }
        return CompletableFuture.completedFuture(false);
    }

    public CompletableFuture<Boolean> savePrefix(final Player player) {
        return savePrefix(player, Shiny.getInstance().prefixes.get(player));
    }
}