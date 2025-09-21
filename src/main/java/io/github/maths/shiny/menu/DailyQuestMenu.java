package io.github.maths.shiny.menu;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.managers.battlepass.Battlepass;
import io.github.maths.shiny.managers.battlepass.Daily;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.ItemBuilder;
import io.github.maths.shiny.utils.PlayerMenu;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.stream.*;
import java.util.*;
import org.bukkit.inventory.*;

public class DailyQuestMenu
{
    public void open(final Player player) {
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)player, 18, CC.translate("&eDaily Quests"));
        final ConfigurationFile config = Shiny.getInstance().getQuestConfig();
        int i = 0;
        final Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(player);
        for (final Daily quest : Shiny.getInstance().getQuestManager().daily) {
            final int percent = (Shiny.getInstance().getBattlepassManager().getBattlepasses().get(player).getPendingDailys().get(quest) == null) ? 0 : ((int)(Shiny.getInstance().getBattlepassManager().getBattlepasses().get(player).getPendingDailys().get(quest) / (double)quest.getAmount() * 100.0));
            final String progress = battlepass.getDailys().stream().anyMatch(e1 -> e1.getName().equals(quest.getName())) ? "Completed" : (battlepass.getPendingDailys().keySet().stream().noneMatch(e2 -> e2.getName().equals(quest.getName())) ? "0" : (battlepass.getPendingDailys().get(battlepass.getPendingDailys().keySet().stream().filter(e2 -> e2.getName().equals(quest.getName())).findFirst().orElse(null)) + "/" + quest.getAmount() + " &8(&a" + percent + "%&8)"));
            if (quest.getDay() == Shiny.getInstance().getBattlepassManager().getDay()) {
                final ItemStack item = new ItemBuilder(Material.valueOf(config.getString("Daily." + quest.getName() + ".Material")))
                        .setName(config.getString("Daily." + quest.getName() + ".Title"))
                        .setLore(config.getStringList("Daily." + quest.getName() + ".Description")
                                .stream()
                                .map(e -> e.replace("%progress%", progress))
                                .collect(Collectors.toList()))
                        .setGlow()
                        .build();                inventory.setItem(i, item);
                ++i;
            }
        }
        player.openInventory(inventory);
        PlayerMenu.addMenu(player, PlayerMenu.DailyQuestMenu);
    }
}
