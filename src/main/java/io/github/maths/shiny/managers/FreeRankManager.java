package io.github.maths.shiny.managers;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.entity.*;
import java.net.*;
import java.io.*;
import java.text.*;
import org.bukkit.*;
import org.bukkit.command.*;
import java.util.*;

public class FreeRankManager
{
    private Shiny instance;
    private ConfigurationFile config;
    private ConfigurationFile data;
    
    public FreeRankManager() {
        this.config = Shiny.getInstance().getConfig();
        this.instance = Shiny.getInstance();
        this.data = new ConfigurationFile(this.instance, "storage/data.yml");
    }
    
    public boolean getNameMCVote(final Player player) {
        try {
            final HttpURLConnection connection = (HttpURLConnection)new URL("https://api.namemc.com/server/koramc.us/likes?profile=" + player.getUniqueId()).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            if (connection.getResponseCode() == 200 || connection.getResponseCode() == 304) {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                final String response = reader.readLine();
                this.data.set("data." + player.getUniqueId() + ".voted", (Object)Boolean.parseBoolean(response));
                this.data.save();
                return Boolean.parseBoolean(response);
            }
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isEnabled() {
        final SimpleDateFormat loadFormat = Shiny.getInstance().getFormat();
        loadFormat.setTimeZone(TimeZone.getTimeZone(this.config.getString("FreeRank.Timezone")));
        try {
            final long endDate = loadFormat.parse(this.config.getConfig().getString("FreeRank.End-Date")).getTime();
            final long diff = endDate - new Date().getTime();
            return diff > 0L;
        }
        catch (ParseException ignored) {
            CC.log("&cFailed to load time. Is it in the correct format?");
            return false;
        }
    }
    
    public boolean alreadyClaimed(final Player player) {
        return this.data.contains("data." + player.getUniqueId().toString()) && this.data.getBoolean("data." + player.getUniqueId() + ".claimed");
    }
    
    public boolean alreadyClaimed(final OfflinePlayer player) {
        return this.data.contains("data." + player.getUniqueId().toString()) && this.data.getBoolean("data." + player.getUniqueId() + ".claimed");
    }
    
    public void giveRank(final Player player) {
        final SimpleDateFormat loadFormat = Shiny.getInstance().getFormat();
        loadFormat.setTimeZone(TimeZone.getTimeZone(this.config.getString("FreeRank.Timezone")));
        try {
            final long endDate = loadFormat.parse(this.config.getString("FreeRank.End-Date")).getTime();
            final long diff = endDate - System.currentTimeMillis();
            if (diff <= 0L) {
                player.sendMessage(CC.translate(this.config.getString("FreeRank.Disabled-Message").replace("%time%", this.config.getString("FreeRank.End-Date").split(" ")[0])));
            }
            else {
                final Long sec = diff / 1000L;
                this.data.set("data." + player.getUniqueId() + ".claimed", (Object)true);
                this.data.save();
                Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), this.config.getString("FreeRank.Command").replace("%player%", player.getName()).replace("%duration%", sec + "s"));
                player.sendMessage(CC.translate(this.config.getString("FreeRank.Claim-Message")));
            }
        }
        catch (ParseException ignored) {
            Bukkit.getConsoleSender().sendMessage(CC.translate("&cFailed to load time. Is it in the correct format?"));
        }
    }
    
    public void resetall() {
        for (final String key : this.data.getConfigurationSection("data").getKeys(false)) {
            this.data.set("data." + key + ".claimed", (Object)false);
            this.data.save();
        }
    }
    
    public void reset(final OfflinePlayer player) {
        this.data.set("data." + player.getUniqueId() + ".claimed", (Object)false);
        this.data.save();
    }
    
    public boolean hasRegisteredVote(final Player player) {
        return this.data.contains("data." + player.getUniqueId() + ".voted") && this.data.getBoolean("data." + player.getUniqueId() + ".voted");
    }
}
