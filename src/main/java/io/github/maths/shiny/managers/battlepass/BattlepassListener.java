package io.github.maths.shiny.managers.battlepass;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.PlayerMenu;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.plugin.*;
import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.configuration.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.*;

public class BattlepassListener implements Listener
{
    public BattlepassListener() {
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, (Plugin) Shiny.getInstance());
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (Shiny.getInstance().getBattlepassManager() == null) {
            return;
        }
        if (!Shiny.getInstance().getBattlepassManager().battlepasses.containsKey(player)) {
            Shiny.getInstance().getBattlepassManager().register((OfflinePlayer)player);
        }
        final ConfigurationSection section = Shiny.getInstance().getRewardConfig().getConfigurationSection("Rewards");
        for (final String key : section.getKeys(false)) {
            if (section.getInt(key + ".Level") <= Shiny.getInstance().getBattlepassManager().battlepasses.get(player).getLevel() && !Shiny.getInstance().getBattlepassManager().battlepasses.get(player).getRewards().contains(key)) {
                if (section.getString(key + ".Type").equals("Premium") && !player.hasPermission("Shiny.battlepass.premium")) {
                    continue;
                }
                player.sendMessage(CC.translate(Shiny.getInstance().getBattlepassConfig().getString("Announces.Pending")));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void ClickEvent(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        if (PlayerMenu.hasMenu(player)) {
            event.setCancelled(true);
        }
    }
}
