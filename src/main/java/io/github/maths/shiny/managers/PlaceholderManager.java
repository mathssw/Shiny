package io.github.maths.shiny.managers;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.ConfigurationFile;
import me.clip.placeholderapi.expansion.*;
import org.bukkit.entity.*;
import me.activated.core.plugin.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class PlaceholderManager extends PlaceholderExpansion
{
    private ConfigurationFile config;
    
    public PlaceholderManager() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.register();
        }
        this.config = new ConfigurationFile(Shiny.getInstance(), "highroller.yml");
    }
    
    @NotNull
    public String getIdentifier() {
        return "Shiny";
    }

    @NotNull
    public String getAuthor() {
        return "maths";
    }

    @NotNull
    public String getVersion() {
        return "1.0";
    }
    
    @Nullable
    public String onPlaceholderRequest(final Player player, @NotNull final String params) {
        if (!params.equalsIgnoreCase("highroller")) {
            return null;
        }
        if (!AquaCoreAPI.INSTANCE.getPlayerData(player.getUniqueId()).hasRank(AquaCoreAPI.INSTANCE.getRankByName("highroller"))) {
            return "";
        }
        if (!Shiny.getInstance().prefixes.containsKey(player)) {
            Shiny.getInstance().prefixes.put((OfflinePlayer)player, "Default");
            return this.config.getString("Prefixes.Default");
        }
        return this.config.getString("Prefixes." + Shiny.getInstance().prefixes.get(player));
    }
}
