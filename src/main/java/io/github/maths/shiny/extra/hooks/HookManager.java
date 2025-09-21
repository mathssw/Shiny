package io.github.maths.shiny.extra.hooks;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.extra.hooks.impl.hcf.AzuriteHook;
import io.github.maths.shiny.extra.hooks.impl.hcf.NoneHCFHook;
import io.github.maths.shiny.extra.hooks.impl.rank.AquaCoreHook;
import io.github.maths.shiny.extra.hooks.impl.rank.NoneRankHook;
import io.github.maths.shiny.extra.hooks.types.HCFHook;
import io.github.maths.shiny.extra.hooks.types.RankHook;
import io.github.maths.shiny.utils.ConfigurationFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HookManager {

    private final Map<String, List<Hook>> hooksByType;
    private final Map<String, Hook> activeHooks;
    private final ConfigurationFile config;

    public HookManager() {
        this.hooksByType = new HashMap<>();
        this.activeHooks = new HashMap<>();
        this.config = Shiny.getInstance().getHooksConfig();
        this.registerHooks();
    }

    private void registerHooks() {
        this.hooksByType.put("HCF", new ArrayList<>());
        this.hooksByType.put("RANK", new ArrayList<>());

        if (config.getBoolean("HCF.Azurite.enabled", true)) {
            this.hooksByType.get("HCF").add(new AzuriteHook());
        }
        this.hooksByType.get("HCF").add(new NoneHCFHook());

        if (config.getBoolean("Rank.AquaCore.enabled", true)) {
            this.hooksByType.get("RANK").add(new AquaCoreHook());
        }
        this.hooksByType.get("RANK").add(new NoneRankHook());
    }

    public void loadHooks() {
        this.loadHCFHooks();
        this.loadRankHooks();

        if (config.getBoolean("Settings.debug", false)) {
            logHooksStatus();
        }
    }

    private void loadHCFHooks() {
        List<String> priority = config.getStringList("HCF.priority");
        if (priority.isEmpty()) {
            priority = List.of("Azurite");
        }
        this.loadHookType("HCF", priority.toArray(new String[0]));
    }

    private void loadRankHooks() {
        List<String> priority = config.getStringList("Rank.priority");
        if (priority.isEmpty()) {
            priority = List.of("AquaCore", "LuckPerms");
        }
        this.loadHookType("RANK", priority.toArray(new String[0]));
    }

    private void loadHookType(String type, String[] priority) {
        List<Hook> hooks = hooksByType.get(type);
        if (hooks == null) return;

        for (String hookName : priority) {
            Hook hook = hooks.stream()
                    .filter(h -> h.getName().equals(hookName))
                    .findFirst().orElse(null);

            if (hook != null) {
                hook.enable();
                if (hook.isEnabled()) {
                    activeHooks.put(type, hook);
                    String message = config.getString("Messages.hook-enabled", "&a[Hooks] Loaded %hook_type% hook: &e%hook_name%")
                            .replace("%hook_type%", type)
                            .replace("%hook_name%", hookName);
                    Shiny.getInstance().getLogger().info(message.replace("&", ""));
                    return;
                }
            }
        }

        Hook fallbackHook = hooks.stream()
                .filter(h -> h.getName().contains("None"))
                .findFirst().orElse(null);

        if (fallbackHook != null) {
            fallbackHook.enable();
            activeHooks.put(type, fallbackHook);
            String message = config.getString("Messages.no-hooks-available", "&c[Hooks] No %hook_type% plugins found, using default")
                    .replace("%hook_type%", type);
            Shiny.getInstance().getLogger().info(message.replace("&", ""));
        }
    }

    private void logHooksStatus() {
        Shiny.getInstance().getLogger().info("=== Hooks Status ===");
        activeHooks.forEach((type, hook) -> {
            Shiny.getInstance().getLogger().info(type + ": " + hook.getName() + " (Enabled: " + hook.isEnabled() + ")");
        });
    }

    public HCFHook getHCFHook() {
        Hook hook = activeHooks.get("HCF");
        return hook instanceof HCFHook ? (HCFHook) hook : null;
    }

    public RankHook getRankHook() {
        Hook hook = activeHooks.get("RANK");
        return hook instanceof RankHook ? (RankHook) hook : null;
    }

    public Hook getActiveHook(String type) {
        return activeHooks.get(type);
    }

    public boolean isHookEnabled(String type, String name) {
        Hook hook = activeHooks.get(type);
        return hook != null && hook.getName().equals(name) && hook.isEnabled();
    }

    public void disableHooks() {
        activeHooks.values().forEach(Hook::disable);
        activeHooks.clear();
    }

    public Map<String, Hook> getActiveHooks() {
        return new HashMap<>(activeHooks);
    }

    public Map<String, List<Hook>> getAllHooks() {
        return new HashMap<>(hooksByType);
    }
}