package io.github.maths.shiny.listeners;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.PlayerMenu;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.configuration.*;
import java.util.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;

public class HighrollerMenuListener implements Listener
{
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        if (PlayerMenu.hasMenu(player) && PlayerMenu.getMenu(player).equals(PlayerMenu.HighrollerMenu)) {
            event.setCancelled(true);
            if (event.getClickedInventory() == null || event.getClickedInventory().equals((Object)player.getInventory())) {
                return;
            }
            final ConfigurationSection section = Shiny.getInstance().getHighrollerConfig().getConfigurationSection("Prefixes");
            final List<String> keys = new ArrayList<String>(section.getKeys(false));
            if (event.getSlot() >= keys.size()) {
                return;
            }
            if (keys.get(event.getSlot()).equals(Shiny.getInstance().prefixes.get(player))) {
                player.closeInventory();
                player.sendMessage(CC.translate("&cYou already have that prefix selected"));
                return;
            }
            Shiny.getInstance().prefixes.put((OfflinePlayer)player, keys.get(event.getSlot()));
            player.closeInventory();
            player.sendMessage(CC.translate("&aSuccessfully changed your prefix"));
        }
    }
    
    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        final Player player = (Player)event.getPlayer();
        if (PlayerMenu.hasMenu(player) && PlayerMenu.getMenu(player).equals(PlayerMenu.HighrollerMenu)) {
            PlayerMenu.removeMenu(player);
        }
    }
}
