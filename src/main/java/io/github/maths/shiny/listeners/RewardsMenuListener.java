package io.github.maths.shiny.listeners;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.managers.battlepass.Battlepass;
import io.github.maths.shiny.menu.BattlePassMenu;
import io.github.maths.shiny.menu.RewardSelector;
import io.github.maths.shiny.menu.RewardsMenu;
import io.github.maths.shiny.utils.BukkitUtils;
import io.github.maths.shiny.utils.PlayerMenu;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import org.bukkit.inventory.*;

import java.util.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.configuration.*;

public class RewardsMenuListener implements Listener
{
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        if (PlayerMenu.hasMenu(player) && PlayerMenu.getMenu(player).equals(PlayerMenu.RewardsMenu)) {
            event.setCancelled(true);
            if (event.getClickedInventory() == null || event.getClickedInventory().equals((Object)player.getInventory())) {
                return;
            }
            if (!event.getClickedInventory().getTitle().startsWith(CC.translate("&eBattlepass Rewards Page"))) {
                return;
            }
            final int page = this.getPage(event.getClickedInventory().getTitle());
            if (event.getSlot() == 41 && page != 11) {
                player.closeInventory();
                new RewardsMenu().open(player, page + 1);
                return;
            }
            if (event.getSlot() == 39 && page != 1) {
                player.closeInventory();
                new RewardsMenu().open(player, page - 1);
                return;
            }
            if (event.getSlot() == 44) {
                player.closeInventory();
                new BattlePassMenu().open(player);
                return;
            }
            if ((event.getSlot() > 8 && event.getSlot() < 18) || (event.getSlot() > 26 && event.getSlot() < 36)) {
                final String reward = this.getReward(event.getSlot(), page);
                final Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(player);
                if (!battlepass.getRewards().contains(reward) && battlepass.getLevel() >= event.getSlot() - ((event.getSlot() > 8 && event.getSlot() < 18) ? 8 : 26) + 9 * (page - 1)) {
                    if (event.getSlot() > 8 && event.getSlot() < 18 && player.hasPermission("Shiny.battlepass.premium")) {
                        if (Shiny.getInstance().getRewardConfig().getString("Rewards." + reward + ".RewardType").startsWith("Multi")) {
                            player.closeInventory();
                            new RewardSelector().open(player, reward);
                            return;
                        }
                        if (Shiny.getInstance().getRewardConfig().getString("Rewards." + reward + ".RewardType").contains("Command")) {
                            Shiny.getInstance().getServer().dispatchCommand((CommandSender) Shiny.getInstance().getServer().getConsoleSender(), Shiny.getInstance().getRewardConfig().getString("Rewards." + reward + ".Command").replace("%player%", player.getName()));
                        }
                        else if (Shiny.getInstance().getRewardConfig().getString("Rewards." + reward + ".RewardType").contains("Item")) {
                            final Map<Integer, ItemStack> drops = (Map<Integer, ItemStack>)player.getInventory().addItem(new ItemStack[] { BukkitUtils.deserializeItemStack(Shiny.getInstance().getRewardConfig().getString("Rewards." + reward + ".Command")) });
                            for (final ItemStack dropItem : drops.values()) {
                                player.getWorld().dropItem(player.getLocation().add(0.0, 1.0, 0.0), dropItem);
                            }
                        }
                        Shiny.getInstance().getBattlepassManager().battlepasses.get(player).getRewards().add(reward);
                        player.closeInventory();
                        new RewardsMenu().open(player, page);
                        player.sendMessage(CC.translate("&aYou successfully claimed your reward!"));
                    }
                    else if (event.getSlot() > 26 && event.getSlot() < 36) {
                        if (Shiny.getInstance().getRewardConfig().getString("Rewards." + reward + ".RewardType").startsWith("Multi")) {
                            player.closeInventory();
                            new RewardSelector().open(player, reward);
                            return;
                        }
                        if (Shiny.getInstance().getRewardConfig().getString("Rewards." + reward + ".RewardType").contains("Command")) {
                            Shiny.getInstance().getServer().dispatchCommand((CommandSender) Shiny.getInstance().getServer().getConsoleSender(), Shiny.getInstance().getRewardConfig().getString("Rewards." + reward + ".Command").replace("%player%", player.getName()));
                        }
                        else if (Shiny.getInstance().getRewardConfig().getString("Rewards." + reward + ".RewardType").contains("Item")) {
                            final Map<Integer, ItemStack> drops = (Map<Integer, ItemStack>)player.getInventory().addItem(new ItemStack[] { BukkitUtils.deserializeItemStack(Shiny.getInstance().getRewardConfig().getString("Rewards." + reward + ".Command")) });
                            for (final ItemStack dropItem : drops.values()) {
                                player.getWorld().dropItem(player.getLocation().add(0.0, 1.0, 0.0), dropItem);
                            }
                        }
                        Shiny.getInstance().getBattlepassManager().battlepasses.get(player).getRewards().add(reward);
                        player.closeInventory();
                        new RewardsMenu().open(player, page);
                        player.sendMessage(CC.translate("&aYou successfully claimed your reward!"));
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        final Player player = (Player)event.getPlayer();
        if (PlayerMenu.hasMenu(player) && PlayerMenu.getMenu(player).equals(PlayerMenu.RewardsMenu)) {
            PlayerMenu.removeMenu(player);
        }
    }
    
    public int getPage(final String title) {
        final String[] titleSplit = title.split(" ");
        return Integer.parseInt(titleSplit[3]);
    }
    
    public String getReward(final int slot, final int page) {
        if (slot > 8 && slot < 18) {
            final ConfigurationSection section = Shiny.getInstance().getRewardConfig().getConfigurationSection("Rewards");
            for (final String key : section.getKeys(false)) {
                if (section.getString(key + ".Type").equalsIgnoreCase("Free")) {
                    continue;
                }
                if (section.getInt(key + ".Level") == slot - 8 + 9 * (page - 1)) {
                    return key;
                }
            }
        }
        if (slot > 26 && slot < 36) {
            final ConfigurationSection section = Shiny.getInstance().getRewardConfig().getConfigurationSection("Rewards");
            for (final String key : section.getKeys(false)) {
                if (section.getString(key + ".Type").equalsIgnoreCase("Premium")) {
                    continue;
                }
                if (section.getInt(key + ".Level") == slot - 26 + 9 * (page - 1)) {
                    return key;
                }
            }
        }
        return null;
    }
}
