package io.github.maths.shiny.menu;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.BukkitUtils;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.ItemBuilder;
import io.github.maths.shiny.utils.PlayerMenu;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.configuration.*;
import org.bukkit.inventory.*;

public class RewardSelector
{
    public void open(final Player player, final String reward) {
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)player, 27, CC.translate("&e&l" + reward + " Reward Selector"));
        final ConfigurationFile config = Shiny.getInstance().getRewardConfig();
        final ConfigurationFile bp = Shiny.getInstance().getBattlepassConfig();
        final ConfigurationSection section = config.getConfigurationSection("Rewards." + reward + ".Command");
        int i = 0;
        for (final String key : section.getKeys(false)) {
            if (config.getString("Rewards." + reward + ".RewardType").contains("Command")) {
                final ItemStack itemStack = new ItemBuilder(Material.valueOf(bp.getString("Rewards.Multi-Reward.Material"))).setName(CC.translate(bp.getString("Rewards.Multi-Reward.Title").replace("%name%", section.getString(key + ".Name")))).setLore(bp.getStringList("Rewards.Multi-Reward.Description")).setGlow(bp.getBoolean("Rewards.Multi-Reward.Glow")).setData(bp.getInt("Rewards.Multi-Reward.Data")).build();
                inventory.setItem(i, itemStack);
                ++i;
            }
            if (config.getString("Rewards." + reward + ".RewardType").contains("Item")) {
                final ItemStack itemStack = BukkitUtils.deserializeItemStack(section.getString(key));
                inventory.setItem(i, itemStack);
                ++i;
            }
        }
        player.openInventory(inventory);
        PlayerMenu.addMenu(player, PlayerMenu.RewardsSelector);
    }
}
