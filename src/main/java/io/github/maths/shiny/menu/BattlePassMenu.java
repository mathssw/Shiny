package io.github.maths.shiny.menu;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.managers.battlepass.Battlepass;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.ItemBuilder;
import io.github.maths.shiny.utils.PlayerMenu;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.stream.*;

import org.bukkit.inventory.*;
import java.util.*;

public class BattlePassMenu
{
    public void open(final Player player) {
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)player, 54, CC.translate("&eKora Battlepass"));
        final ConfigurationFile config = Shiny.getInstance().getBattlepassConfig();
        final Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(player);
        final ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE).setName("&a").setData(config.getInt("Main.Glass.Data")).build();

        for (final int i : config.getIntegerList("Main.Glass.Slots")) {
            inventory.setItem(i, glass);
        }

        for (final String key : config.getConfigurationSection("Main").getKeys(false)) {
            if (key.equalsIgnoreCase("Glass") || key.equalsIgnoreCase("Enabled")) {
                continue;
            }

            final ItemStack itemStack = new ItemBuilder(Material.valueOf(config.getString("Main." + key + ".Material")))
                    .setLore(config.getStringList("Main." + key + ".Description")
                            .stream()
                            .map(e -> e.replace("%level%", String.valueOf(battlepass.getLevel()))
                                    .replace("%xp%", String.valueOf(battlepass.getXp()))
                                    .replace("%day%", String.valueOf(Shiny.getInstance().getBattlepassManager().getDay())))
                            .collect(Collectors.toList()))
                    .setName(config.getString("Main." + key + ".Title"))
                    .setData(config.getInt("Main." + key + ".Data"))
                    .setGlow(config.getBoolean("Main." + key + ".Glow"))
                    .build();

            inventory.setItem(config.getInt("Main." + key + ".Slot") - 1, itemStack);
        }

        player.openInventory(inventory);
        PlayerMenu.addMenu(player, PlayerMenu.BattlepassMenu);
    }
}