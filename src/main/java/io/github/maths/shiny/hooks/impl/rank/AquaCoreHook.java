package io.github.maths.shiny.hooks.impl.rank;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.hooks.types.RankHook;
import me.activated.core.plugin.AquaCoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class AquaCoreHook implements RankHook {

    private Plugin aquaCore;
    private boolean enabled = false;

    @Override
    public String getName() {
        return "AquaCore";
    }

    @Override
    public boolean isEnabled() {
        return enabled && aquaCore != null;
    }

    @Override
    public void enable() {
        try {
            final Plugin plugin = Bukkit.getPluginManager().getPlugin("AquaCore");
            if (plugin != null && plugin.isEnabled()) {
                this.aquaCore = plugin;
                this.enabled = true;
            }
        } catch (Exception e) {
            this.enabled = false;
        }
    }

    @Override
    public void disable() {
        this.aquaCore = null;
        this.enabled = false;
    }

    @Override
    public boolean hasRank(UUID playerId, String rankName) {
        if (!isEnabled()) return false;
        try {
            return AquaCoreAPI.INSTANCE.getPlayerData(playerId).hasRank(AquaCoreAPI.INSTANCE.getRankByName(rankName));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getPlayerRank(UUID playerId) {
        if (!isEnabled()) return "Default";
        try {
            return AquaCoreAPI.INSTANCE.getPlayerData(playerId).getHighestRank().getName();
        } catch (Exception e) {
            return "Default";
        }
    }

    @Override
    public String getPrefix(UUID playerId) {
        if (!isEnabled()) return "";
        try {
            return AquaCoreAPI.INSTANCE.getPlayerData(playerId).getHighestRank().getPrefix();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String getSuffix(UUID playerId) {
        if (!isEnabled()) return "";
        try {
            return AquaCoreAPI.INSTANCE.getPlayerData(playerId).getHighestRank().getSuffix();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public boolean isLoaded() {
        return isEnabled();
    }
}