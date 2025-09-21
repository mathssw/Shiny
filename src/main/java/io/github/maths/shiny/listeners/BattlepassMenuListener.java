package io.github.maths.shiny.listeners;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.menu.QuestMenu;
import io.github.maths.shiny.menu.RewardsMenu;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.PlayerMenu;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;

public class BattlepassMenuListener implements Listener
{
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        if (PlayerMenu.hasMenu(player) && PlayerMenu.getMenu(player) == PlayerMenu.BattlepassMenu) {
            event.setCancelled(true);
            if (event.getClickedInventory() == null || event.getClickedInventory().equals((Object)player.getInventory())) {
                return;
            }
            final ConfigurationFile config = Shiny.getInstance().getBattlepassConfig();
            if (event.getSlot() == config.getInt("Main.Quests.Slot") - 1) {
                player.closeInventory();
                new QuestMenu().open(player);
            }
            if (event.getSlot() == config.getInt("Main.Rewards.Slot") - 1) {
                player.closeInventory();
                new RewardsMenu().open(player, 1);
            }
        }
    }
    
    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        final Player player = (Player)event.getPlayer();
        if (PlayerMenu.hasMenu(player) && PlayerMenu.getMenu(player) == PlayerMenu.BattlepassMenu) {
            PlayerMenu.removeMenu(player);
        }
    }
}
