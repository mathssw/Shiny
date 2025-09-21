package io.github.maths.shiny;

import io.github.maths.shiny.hooks.HookManager;
import io.github.maths.shiny.managers.*;
import io.github.maths.shiny.managers.battlepass.BattlepassManager;
import io.github.maths.shiny.managers.battlepass.QuestManager;
import io.github.maths.shiny.storage.StorageManager;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class Shiny extends JavaPlugin {

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
    public BukkitTask runnable;
    public Map<OfflinePlayer, String> prefixes;

    private StorageManager storageManager;
    private HookManager hookManager;
    private FreeRankManager freeRankManager;
    private HighrollerManager highrollerManager;
    private EnderchestManager enderchestManager;
    private BattlepassManager battlepassManager;
    private QuestManager questManager;
    private ChatReactionManager chatReactionManager;

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

    public static Shiny getInstance() {
        return instance;
    }

    public ConfigurationFile getConfig() {
        return config;
    }

    public ConfigurationFile getHooksConfig() {
        return hooksConfig;
    }

    public ConfigurationFile getStorageConfig() {
        return storageConfig;
    }

    public ConfigurationFile getHighrollerConfig() {
        return highrollerConfig;
    }

    public ConfigurationFile getBattlepassConfig() {
        return battlepassConfig;
    }

    public ConfigurationFile getEnderchestConfig() {
        return enderchestConfig;
    }

    public ConfigurationFile getQuestConfig() {
        return questConfig;
    }

    public ConfigurationFile getRewardConfig() {
        return rewardConfig;
    }

    public SimpleDateFormat getFormat() {
        return format;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public FreeRankManager getFreeRankManager() {
        return freeRankManager;
    }

    public HighrollerManager getHighrollerManager() {
        return highrollerManager;
    }

    public EnderchestManager getEnderchestManager() {
        return enderchestManager;
    }

    public BattlepassManager getBattlepassManager() {
        return battlepassManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public ChatReactionManager getChatReactionManager() {
        return chatReactionManager;
    }

    public void setConfig(ConfigurationFile config) {
        this.config = config;
    }

    public void setHooksConfig(ConfigurationFile hooksConfig) {
        this.hooksConfig = hooksConfig;
    }

    public void setStorageConfig(ConfigurationFile storageConfig) {
        this.storageConfig = storageConfig;
    }

    public void setHighrollerConfig(ConfigurationFile highrollerConfig) {
        this.highrollerConfig = highrollerConfig;
    }

    public void setBattlepassConfig(ConfigurationFile battlepassConfig) {
        this.battlepassConfig = battlepassConfig;
    }

    public void setEnderchestConfig(ConfigurationFile enderchestConfig) {
        this.enderchestConfig = enderchestConfig;
    }

    public void setQuestConfig(ConfigurationFile questConfig) {
        this.questConfig = questConfig;
    }

    public void setRewardConfig(ConfigurationFile rewardConfig) {
        this.rewardConfig = rewardConfig;
    }

    public void setFormat(SimpleDateFormat format) {
        this.format = format;
    }

    public void setStorageManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    public void setHookManager(HookManager hookManager) {
        this.hookManager = hookManager;
    }

    public void setFreeRankManager(FreeRankManager freeRankManager) {
        this.freeRankManager = freeRankManager;
    }

    public void setHighrollerManager(HighrollerManager highrollerManager) {
        this.highrollerManager = highrollerManager;
    }

    public void setEnderchestManager(EnderchestManager enderchestManager) {
        this.enderchestManager = enderchestManager;
    }

    public void setBattlepassManager(BattlepassManager battlepassManager) {
        this.battlepassManager = battlepassManager;
    }

    public void setQuestManager(QuestManager questManager) {
        this.questManager = questManager;
    }

    public void setChatReactionManager(ChatReactionManager chatReactionManager) {
        this.chatReactionManager = chatReactionManager;
    }
}