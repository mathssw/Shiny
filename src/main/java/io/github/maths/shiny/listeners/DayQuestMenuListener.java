package io.github.maths.shiny.listeners;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.menu.QuestMenu;
import io.github.maths.shiny.utils.PlayerMenu;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.scheduler.*;
import org.bukkit.plugin.*;
import org.bukkit.event.*;

public class DayQuestMenuListener implements Listener
{
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        if (PlayerMenu.hasMenu(player) && (PlayerMenu.getMenu(player).equals(PlayerMenu.DayQuestMenu) || PlayerMenu.getMenu(player).equals(PlayerMenu.DailyQuestMenu))) {
            event.setCancelled(true);
            if (event.getClickedInventory() == null || event.getClickedInventory().equals((Object)player.getInventory())) {
                return;
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onClose(final InventoryCloseEvent event) {
        final Player player = (Player)event.getPlayer();
        if (PlayerMenu.hasMenu(player) && (PlayerMenu.getMenu(player).equals(PlayerMenu.DayQuestMenu) || PlayerMenu.getMenu(player).equals(PlayerMenu.DailyQuestMenu))) {
            PlayerMenu.removeMenu(player);
            new BukkitRunnable() {
                public void run() {
                    new QuestMenu().open(player);
                }
            }.runTaskLater((Plugin) Shiny.getInstance(), 1L);
        }
    }
}
