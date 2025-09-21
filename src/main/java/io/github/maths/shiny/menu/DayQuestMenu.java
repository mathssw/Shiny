package io.github.maths.shiny.menu;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.managers.battlepass.Battlepass;
import io.github.maths.shiny.managers.battlepass.Quest;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.ItemBuilder;
import io.github.maths.shiny.utils.PlayerMenu;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.stream.*;
import java.util.*;
import org.bukkit.inventory.*;

public class DayQuestMenu
{
    public void open(final Player player, final int day) {
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)player, 18, CC.translate("&eDay " + day + " Quests"));
        final ConfigurationFile config = Shiny.getInstance().getQuestConfig();
        int i = 0;
        final Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(player);
        for (final Quest quest : Shiny.getInstance().getQuestManager().quests) {
            final int percent = (Shiny.getInstance().getBattlepassManager().battlepasses.get(player).getPendingQuests().get(quest) == null) ? 0 : ((int)(Shiny.getInstance().getBattlepassManager().battlepasses.get(player).getPendingQuests().get(quest) / (double)quest.getAmount() * 100.0));
            final String progress = battlepass.getQuests().stream().anyMatch(e1 -> e1.getName().equals(quest.getName())) ? "Completed" : ((Shiny.getInstance().getBattlepassManager().battlepasses.get(player).getPendingQuests().get(quest) == null) ? "0" : (Shiny.getInstance().getBattlepassManager().battlepasses.get(player).getPendingQuests().get(quest) + "/" + quest.getAmount() + " &8(&a" + percent + "%&8)"));
            if (quest.getDay() == day) {
                final ItemStack item = new ItemBuilder(
                        Material.valueOf(config.getString("Quests." + quest.getName() + ".Material")))
                        .setName(config.getString("Quests." + quest.getName() + ".Title"))
                        .setLore(
                                config.getStringList("Quests." + quest.getName() + ".Description").stream()
                                        .map(e -> e.replace("%progress%", progress))
                                        .collect(Collectors.toList())
                        )
                        .setGlow()
                        .build();
                inventory.setItem(i, item);
                ++i;
            }
        }
        player.openInventory(inventory);
        PlayerMenu.addMenu(player, PlayerMenu.DayQuestMenu);
    }
}
