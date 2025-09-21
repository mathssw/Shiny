package io.github.maths.shiny.menu;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.ItemBuilder;
import io.github.maths.shiny.utils.JavaUtil;
import io.github.maths.shiny.utils.PlayerMenu;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.*;
import java.util.stream.*;

import org.bukkit.inventory.*;

public class QuestMenu
{
    public void open(final Player player) {
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)player, 27, CC.translate("&eBattlepass Quests"));
        final ConfigurationFile config = Shiny.getInstance().getBattlepassConfig();
        final ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE).setName("&a").setData(config.getInt("Main.Glass")).build();
        for (int i = 0; i < 27; ++i) {
            inventory.setItem(i, glass);
        }

        int k = 0;
        for (int j = 0; j <= config.getInt("Data.Max-Days"); ++j) {
            final int slot = config.getIntegerList("Quests.Item.Slots").get(k);
            final int currentDay = j;

            if (currentDay > Shiny.getInstance().getBattlepassManager().getDay()) {
                // Día futuro - deshabilitado
                final long remainingTime = Shiny.getInstance().getBattlepassManager().getRemainingTo(currentDay);
                final String disabledText = config.getString("Quests.Item.Disabled").replace("%time%", JavaUtil.formatDurationLong(remainingTime));

                final ItemStack itemStack = new ItemBuilder(Material.BOOK)
                        .setData(config.getInt("Quests.Item.Data"))
                        .setName(config.getString("Quests.Item.Title").replace("%day%", String.valueOf(currentDay)))
                        .setLore(config.getStringList("Quests.Item.Description").stream()
                                .map(line -> line.replace("%status%", disabledText)
                                        .replace("%information%", getQuestInformation()))
                                .collect(Collectors.toList()))
                        .build();
                inventory.setItem(slot, itemStack);
                ++k;
            }
            else {
                // Día actual o pasado - habilitado
                final long completedQuests = Shiny.getInstance().getBattlepassManager().battlepasses.get(player).getQuests().stream()
                        .filter(quest -> quest.getDay() == currentDay)
                        .count();
                final long totalQuests = Shiny.getInstance().getQuestManager().quests.stream()
                        .filter(quest -> quest.getDay() == currentDay)
                        .count();
                final String enabledText = config.getString("Quests.Item.Enabled").replace("%completed%", completedQuests + "/" + totalQuests);

                final ItemStack itemStack = new ItemBuilder(Material.valueOf(config.getString("Quests.Item.Material")))
                        .setData(config.getInt("Quests.Item.Data"))
                        .setName(config.getString("Quests.Item.Title").replace("%day%", String.valueOf(currentDay)))
                        .setLore(config.getStringList("Quests.Item.Description").stream()
                                .map(line -> line.replace("%status%", enabledText)
                                        .replace("%information%", getQuestInformation()))
                                .collect(Collectors.toList()))
                        .setGlow(config.getBoolean("Quests.Item.Glow"))
                        .build();
                inventory.setItem(slot, itemStack);
                ++k;
            }
        }

        // Item de misiones diarias
        final long completedDailys = Shiny.getInstance().getBattlepassManager().getBattlepasses().get(player).getDailys().stream()
                .filter(daily -> daily.getDay() == Shiny.getInstance().getBattlepassManager().getDay())
                .count();
        final long totalDailys = Shiny.getInstance().getQuestManager().daily.stream()
                .filter(daily -> daily.getDay() == Shiny.getInstance().getBattlepassManager().getDay())
                .count();
        final long remainingTime = Shiny.getInstance().getBattlepassManager().getRemaining();

        final ItemStack dailyItem = new ItemBuilder(Material.valueOf(config.getString("Daily.Item.Material")))
                .setData(config.getInt("Daily.Item.Data"))
                .setName(config.getString("Daily.Item.Title"))
                .setLore(config.getStringList("Daily.Item.Description").stream()
                        .map(line -> line.replace("%completed%", completedDailys + "/" + totalDailys)
                                .replace("%time%", JavaUtil.formatDurationLong(remainingTime)))
                        .collect(Collectors.toList()))
                .setGlow(config.getBoolean("Daily.Item.Glow"))
                .build();

        inventory.setItem(config.getInt("Daily.Item.Slot") - 1, dailyItem);
        player.openInventory(inventory);
        PlayerMenu.addMenu(player, PlayerMenu.QuestMenu);
    }

    private String getQuestInformation() {
        return "&fClick to view available quests for this day";
    }
}