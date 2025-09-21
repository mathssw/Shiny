package io.github.maths.shiny;

import io.github.maths.shiny.extra.hooks.HookManager;
import io.github.maths.shiny.managers.*;
import io.github.maths.shiny.managers.battlepass.BattlepassManager;
import io.github.maths.shiny.managers.battlepass.QuestManager;
import io.github.maths.shiny.extra.storage.StorageManager;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.chat.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public final class Shiny extends JavaPlugin {

    @Getter
    private static Shiny instance;
    private ShinyLoader loader;

    private ConfigurationFile config;
    private ConfigurationFile hooksConfig;
    private ConfigurationFile storageConfig;
    private ConfigurationFile highrollerConfig;
    private ConfigurationFile battlepassConfig;
    private ConfigurationFile enderchestConfig;
    private ConfigurationFile questConfig;
    private ConfigurationFile rewardConfig;

    private SimpleDateFormat format;
    private StorageManager storageManager;
    private HookManager hookManager;
    private FreeRankManager freeRankManager;
    private HighrollerManager highrollerManager;
    private EnderchestManager enderchestManager;
    private BattlepassManager battlepassManager;
    private QuestManager questManager;
    private ChatReactionManager chatReactionManager;

    public BukkitTask runnable;
    public Map<OfflinePlayer, String> prefixes;

    @Override
    public void onEnable() {
        instance = this;
        this.loader = new ShinyLoader(this);
        this.loader.load();
        CC.log("&f- &eShiny has been enabled");
    }

    @Override
    public void onDisable() {
        if (loader != null) {
            loader.unload();
        }
        instance = null;
    }

    public void reloadConfig() {
        new BukkitRunnable() {
            public void run() {
                if (loader != null) {
                    loader.reload();
                }
                CC.log("&eReloaded Shiny");
            }
        }.runTaskAsynchronously(this);
    }

    public boolean isSotwActive() {
        return hookManager.getHCFHook().hasSotwActive();
    }

    public int getCleanTime() {
        return isSotwActive() ?
                hookManager.getHCFHook().getSotwTime() :
                hookManager.getHCFHook().getNormalTime();
    }

    public boolean hasRank(UUID playerId, String rankName) {
        return hookManager.getRankHook().hasRank(playerId, rankName);
    }

    public String getPlayerRank(UUID playerId) {
        return hookManager.getRankHook().getPlayerRank(playerId);
    }

    public String getPlayerPrefix(UUID playerId) {
        return hookManager.getRankHook().getPrefix(playerId);
    }

    public String getPlayerSuffix(UUID playerId) {
        return hookManager.getRankHook().getSuffix(playerId);
    }

    public CompletableFuture<Boolean> savePlayerData(UUID playerId, String collection, Object data) {
        return storageManager.savePlayerData(playerId, collection, data);
    }

    public CompletableFuture<Object> loadPlayerData(UUID playerId, String collection, Class<?> type) {
        return storageManager.loadPlayerData(playerId, collection, type);
    }
}