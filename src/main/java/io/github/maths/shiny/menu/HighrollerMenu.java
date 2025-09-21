package io.github.maths.shiny.menu;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.ItemBuilder;
import io.github.maths.shiny.utils.PlayerMenu;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.stream.*;

import org.bukkit.inventory.*;
import java.util.*;

public class HighrollerMenu
{
    public void open(final Player player) {
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)player, 9, CC.translate("&eHighroller Prefix Changer"));
        final ConfigurationFile config = new ConfigurationFile(Shiny.getInstance(), "highroller.yml");
        for (final String key : config.getConfigurationSection("Prefixes").getKeys(false)) {
            final ItemStack itemStack = new ItemBuilder(
                    Material.valueOf(config.getString("Item.Material")))
                    .setGlow(config.getBoolean("Item.Glow"))
                    .setName(config.getString("Item.Title").replace("%name%", key))
                    .setLore(
                            config.getStringList("Item.Description").stream()
                                    .map(e -> e.replace("%format%", CC.translate(config.getString("Prefixes." + key))))
                                    .collect(Collectors.toList())
                    )
                    .build();

            if (key.equals(Shiny.getInstance().getHighrollerManager().getType(player))) {
                itemStack.setDurability((short)config.getInt("Item.Selected-Data"));
            }
            else {
                itemStack.setDurability((short)config.getInt("Item.Unselected-Data"));
            }
            inventory.addItem(new ItemStack[] { itemStack });
        }
        player.openInventory(inventory);
        PlayerMenu.addMenu(player, PlayerMenu.HighrollerMenu);
    }
}
