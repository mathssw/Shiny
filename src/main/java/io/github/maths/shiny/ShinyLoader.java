package io.github.maths.shiny;

import io.github.maths.shiny.commands.CommandManager;
import io.github.maths.shiny.hooks.HookManager;
import io.github.maths.shiny.listeners.*;
import io.github.maths.shiny.managers.*;
import io.github.maths.shiny.managers.battlepass.BattlepassManager;
import io.github.maths.shiny.managers.battlepass.QuestManager;
import io.github.maths.shiny.managers.battlepass.QuestsChecker;
import io.github.maths.shiny.storage.StorageManager;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;

public class ShinyLoader {

    private final Shiny plugin;

    public ShinyLoader(Shiny plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.setupConfigs();
        this.setupStorage();
        this.setupHooks();
        this.setupManagers();
        this.registerCommands();
        this.registerListeners();
        this.startAutoSave();
    }

    public void unload() {
        final long startTime = System.currentTimeMillis();

        this.disableHooks();
        this.saveData();

        CC.log("&eDisabled plugin after " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void reload() {
        this.reloadConfigs();
        this.reloadHooks();
        this.reloadManagers();
        this.reloadChatReaction();
    }

    private void setupConfigs() {
        plugin.setFormat(new SimpleDateFormat("MM/dd/yy HH:mm:ss"));
        plugin.setConfig(new ConfigurationFile(plugin, "config.yml"));
        plugin.setHooksConfig(new ConfigurationFile(plugin, "hooks.yml"));
        plugin.setStorageConfig(new ConfigurationFile(plugin, "storage.yml"));
        plugin.setHighrollerConfig(new ConfigurationFile(plugin, "highroller.yml"));
        plugin.setBattlepassConfig(new ConfigurationFile(plugin, "battlepass/battlepass.yml"));
        plugin.setRewardConfig(new ConfigurationFile(plugin, "battlepass/rewards.yml"));
        plugin.setQuestConfig(new ConfigurationFile(plugin, "battlepass/quests.yml"));
        plugin.setEnderchestConfig(new ConfigurationFile(plugin, "storage/enderchest.yml"));
        CC.log("&f- &eCreated all config files");
    }

    private void setupStorage() {
        StorageManager storageManager = new StorageManager();
        storageManager.connect().thenRun(() -> {
            CC.log("&f- &eStorage system connected");
        });
        plugin.setStorageManager(storageManager);
    }

    private void setupHooks() {
        HookManager hookManager = new HookManager();
        hookManager.loadHooks();
        plugin.setHookManager(hookManager);
        CC.log("&f- &eLoaded all hooks");
    }

    private void setupManagers() {
        plugin.prefixes = new ConcurrentHashMap<OfflinePlayer, String>();
        plugin.setFreeRankManager(new FreeRankManager());
        plugin.setEnderchestManager(new EnderchestManager());

        if (plugin.getBattlepassConfig().getBoolean("Main.Enabled")) {
            plugin.setBattlepassManager(new BattlepassManager());
            plugin.setQuestManager(new QuestManager());
            new QuestsChecker();
        }

        HighrollerManager highrollerManager = new HighrollerManager();
        plugin.setHighrollerManager(highrollerManager);

        new PlaceholderManager();
        new LaunchpadManager();

        if (plugin.getConfig().getBoolean("Clean.Enabled")) {
            new CleanManager();
        }

        plugin.setChatReactionManager(new ChatReactionManager());
        CC.log("&f- &eSetup all managers");
    }

    private void registerCommands() {
        CommandManager.registerCommands(plugin);
    }

    private void registerListeners() {
        this.registerListener(new HighrollerMenuListener());
        this.registerListener(new HighrollerPlayerListener());
        this.registerListener(new BattlepassMenuListener());
        this.registerListener(new QuestsMenuListener());
        this.registerListener(new DayQuestMenuListener());
        this.registerListener(new RewardsMenuListener());
        this.registerListener(new RewardsSelectorMenuListener());

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "Shiny");
        CC.log("&f- &eRegistered all listeners");
    }

    private void registerListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    private void startAutoSave() {
        if (!plugin.getConfig().getBoolean("Auto-Save")) {
            return;
        }

        plugin.runnable = new BukkitRunnable() {
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
            }
        }.runTaskTimer(plugin, 0L, 6000L);

        CC.log("&f- &eStarted auto-save task");
    }

    private void disableHooks() {
        if (plugin.getHookManager() != null) {
            plugin.getHookManager().disableHooks();
        }
    }

    private void saveData() {
        final long startTime = System.currentTimeMillis();

        if (plugin.getHighrollerManager() != null) {
            plugin.getHighrollerManager().savePrefixes(plugin.prefixes);
            CC.log("&eSaved Highroller Prefixes after " + (System.currentTimeMillis() - startTime) + "ms");
        }

        if (plugin.getBattlepassConfig() != null && plugin.getBattlepassConfig().getBoolean("Main.Enabled")) {
            if (plugin.getBattlepassManager() != null) {
                plugin.getBattlepassManager().save();
            }
        }

        if (plugin.getEnderchestManager() != null) {
            plugin.getEnderchestManager().getStorage().save();
        }
    }

    private void reloadConfigs() {
        plugin.setConfig(new ConfigurationFile(plugin, "config.yml"));
        plugin.setHooksConfig(new ConfigurationFile(plugin, "hooks.yml"));
        plugin.setStorageConfig(new ConfigurationFile(plugin, "storage.yml"));
        plugin.setHighrollerConfig(new ConfigurationFile(plugin, "highroller.yml"));
        new PlaceholderManager();
        plugin.setFreeRankManager(new FreeRankManager());
    }

    private void reloadHooks() {
        plugin.getHookManager().disableHooks();
        HookManager newHookManager = new HookManager();
        newHookManager.loadHooks();
        plugin.setHookManager(newHookManager);
    }

    private void reloadManagers() {
        if (plugin.getBattlepassConfig().getBoolean("Main.Enabled")) {
            if (plugin.getBattlepassManager() != null) {
                plugin.getBattlepassManager().save();
            }
            plugin.setQuestConfig(new ConfigurationFile(plugin, "battlepass/quests.yml"));
            plugin.setRewardConfig(new ConfigurationFile(plugin, "battlepass/rewards.yml"));
            plugin.setBattlepassConfig(new ConfigurationFile(plugin, "battlepass/battlepass.yml"));
            plugin.setBattlepassManager(new BattlepassManager());
            plugin.setQuestManager(new QuestManager());
        }
    }

    private void reloadChatReaction() {
        if (plugin.getChatReactionManager() != null &&
                plugin.getChatReactionManager().getConfig().getBoolean("Enabled")) {
            plugin.getChatReactionManager().reload();
        }
    }
}