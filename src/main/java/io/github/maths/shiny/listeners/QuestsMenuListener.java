package io.github.maths.shiny.listeners;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.menu.DailyQuestMenu;
import io.github.maths.shiny.menu.DayQuestMenu;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.PlayerMenu;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;

public class QuestsMenuListener implements Listener
{
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        if (PlayerMenu.hasMenu(player) && PlayerMenu.getMenu(player).equals(PlayerMenu.QuestMenu)) {
            event.setCancelled(true);
            final ConfigurationFile config = Shiny.getInstance().getBattlepassConfig();
            if (event.getClickedInventory() == null || event.getClickedInventory().equals((Object)player.getInventory())) {
                return;
            }
            if (event.getSlot() == config.getInt("Daily.Item.Slot") - 1) {
                PlayerMenu.removeMenu(player);
                player.closeInventory();
                new DailyQuestMenu().open(player);
                return;
            }
            if (config.getIntegerList("Quests.Item.Slots").contains(event.getSlot())) {
                if (config.getIntegerList("Quests.Item.Slots").indexOf(event.getSlot()) * 2 > Shiny.getInstance().getBattlepassManager().getConfig().getInt("Data.Max-Days")) {
                    return;
                }
                if (Shiny.getInstance().getBattlepassManager().getDay() < config.getIntegerList("Quests.Item.Slots").indexOf(event.getSlot()) * 2) {
                    return;
                }
                PlayerMenu.removeMenu(player);
                player.closeInventory();
                new DayQuestMenu().open(player, config.getIntegerList("Quests.Item.Slots").indexOf(event.getSlot()) * 2);
            }
        }
    }
    
    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        final Player player = (Player)event.getPlayer();
        if (PlayerMenu.hasMenu(player) && PlayerMenu.getMenu(player).equals(PlayerMenu.QuestMenu)) {
            PlayerMenu.removeMenu(player);
        }
    }
}
