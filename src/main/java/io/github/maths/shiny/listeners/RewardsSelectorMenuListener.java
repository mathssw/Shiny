package io.github.maths.shiny.listeners;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.menu.RewardsMenu;
import io.github.maths.shiny.utils.BukkitUtils;
import io.github.maths.shiny.utils.PlayerMenu;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.inventory.*;
import org.bukkit.configuration.*;
import java.util.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;

public class RewardsSelectorMenuListener implements Listener
{
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        if (PlayerMenu.hasMenu(player) && PlayerMenu.getMenu(player) == PlayerMenu.RewardsSelector) {
            event.setCancelled(true);
            String reward = event.getClickedInventory().getTitle().split(" ")[0];
            reward = ChatColor.stripColor(reward);
            final ConfigurationSection section = Shiny.getInstance().getRewardConfig().getConfigurationSection("Rewards." + reward + ".Command");
            if (section == null) {
                return;
            }
            final List<String> keys = new ArrayList<String>(section.getKeys(false));
            if (keys.size() <= event.getSlot()) {
                return;
            }
            if (Shiny.getInstance().getRewardConfig().getString("Rewards." + reward + ".RewardType").contains("Command")) {
                Shiny.getInstance().getServer().dispatchCommand((CommandSender) Shiny.getInstance().getServer().getConsoleSender(), Shiny.getInstance().getRewardConfig().getString("Rewards." + reward + ".Command." + keys.get(event.getSlot()) + ".Command").replace("%player%", player.getName()));
            }
            else if (Shiny.getInstance().getRewardConfig().getString("Rewards." + reward + ".RewardType").contains("Item")) {
                final Map<Integer, ItemStack> drops = (Map<Integer, ItemStack>)player.getInventory().addItem(new ItemStack[] { BukkitUtils.deserializeItemStack(section.getString((String)keys.get(event.getSlot()))) });
                for (final ItemStack dropItem : drops.values()) {
                    player.getWorld().dropItem(player.getLocation().add(0.0, 1.0, 0.0), dropItem);
                }
            }
            Shiny.getInstance().getBattlepassManager().battlepasses.get(player).getRewards().add(reward);
            player.closeInventory();
            new RewardsMenu().open(player, 1);
        }
    }
    
    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        final Player player = (Player)event.getPlayer();
        if (PlayerMenu.hasMenu(player) && PlayerMenu.getMenu(player) == PlayerMenu.RewardsSelector) {
            PlayerMenu.removeMenu(player);
        }
    }
}
