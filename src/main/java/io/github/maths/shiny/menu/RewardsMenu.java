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
import org.bukkit.configuration.*;
import java.util.*;

public class RewardsMenu
{
    public void open(final Player player, final int page) {
        if (page > 11) {
            return;
        }
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)player, 45, CC.translate("&eBattlepass Rewards Page " + page));

        // Vidrio para los bordes
        final ItemStack glassPane = new ItemBuilder(Material.STAINED_GLASS_PANE).setData(15).build();
        for (int i = 0; i < 9; ++i) {
            inventory.setItem(i, glassPane);
            inventory.setItem(i + 36, glassPane);
        }

        // Niveles en la parte inferior
        for (int i = 18; i <= 26; ++i) {
            final int level = i - 17 + 9 * (page - 1);
            ItemStack item;
            if (level <= Shiny.getInstance().getBattlepassManager().battlepasses.get(player).getLevel()) {
                item = new ItemBuilder(Material.STAINED_GLASS_PANE).setName("&a&lLevel " + level).setData(5).build();
            }
            else {
                item = new ItemBuilder(Material.STAINED_GLASS_PANE).setName("&c&lLevel " + level).setData(14).build();
            }
            inventory.setItem(i, item);
        }

        final ConfigurationSection section = Shiny.getInstance().getRewardConfig().getConfigurationSection("Rewards");
        final ConfigurationFile config = Shiny.getInstance().getBattlepassConfig();
        final int[] levelSlots = { 1 + 9 * (page - 1), 2 + 9 * (page - 1), 3 + 9 * (page - 1), 4 + 9 * (page - 1), 5 + 9 * (page - 1), 6 + 9 * (page - 1), 7 + 9 * (page - 1), 8 + 9 * (page - 1), 9 + 9 * (page - 1) };

        if (section != null) {
            for (final String key : section.getKeys(false)) {
                final int rewardLevel = section.getInt(key + ".Level");

                if (Arrays.stream(levelSlots).anyMatch(x -> x == rewardLevel)) {
                    final String rewardType = section.getString(key + ".Type");
                    final Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(player);
                    final boolean hasPremium = player.hasPermission("Shiny.battlepass.premium");
                    final boolean levelReached = battlepass.getLevel() >= rewardLevel;
                    final boolean isClaimed = battlepass.getRewards().contains(key);

                    if (rewardType.equalsIgnoreCase("Free")) {
                        // Recompensa gratuita
                        List<String> lore = new ArrayList<>();
                        for (String line : config.getStringList("Rewards.Free.Description")) {
                            String status;
                            if (isClaimed) {
                                status = "&cClaimed";
                            } else if (levelReached) {
                                status = "&aUnlocked";
                            } else {
                                status = "&cLocked";
                            }

                            if (line.equalsIgnoreCase("%rewards%")) {
                                for (final String reward : section.getStringList(key + ".Rewards")) {
                                    lore.add("&e� &f" + reward);
                                }
                            } else {
                                lore.add(line.replace("%status%", status));
                            }
                        }

                        final ItemStack item = new ItemBuilder(Material.valueOf(config.getString("Rewards.Free.Material")))
                                .setData(config.getInt("Rewards.Free.Data"))
                                .setLore(lore)
                                .setName(config.getString("Rewards.Free.Title").replace("%title%", "Level " + rewardLevel))
                                .setGlow(config.getBoolean("Rewards.Free.Glow"))
                                .build();

                        inventory.setItem(rewardLevel - 9 * (page - 1) - 1 + 27, item);
                    } else {
                        // Recompensa premium
                        List<String> lore = new ArrayList<>();
                        for (String line : config.getStringList("Rewards.Premium.Description")) {
                            String status;
                            if (isClaimed) {
                                status = "&cClaimed";
                            } else if (levelReached && hasPremium) {
                                status = "&aUnlocked";
                            } else {
                                status = "&cLocked";
                            }

                            if (line.equalsIgnoreCase("%rewards%")) {
                                for (final String reward : section.getStringList(key + ".Rewards")) {
                                    lore.add("&e� &f" + reward);
                                }
                            } else {
                                lore.add(line.replace("%status%", status));
                            }
                        }

                        final ItemStack item = new ItemBuilder(Material.valueOf(config.getString("Rewards.Premium.Material")))
                                .setData(config.getInt("Rewards.Premium.Data"))
                                .setLore(lore)
                                .setName(config.getString("Rewards.Premium.Title").replace("%title%", "Level " + rewardLevel))
                                .setGlow(config.getBoolean("Rewards.Premium.Glow"))
                                .build();

                        inventory.setItem(rewardLevel - 9 * (page - 1) - 1 + 9, item);
                    }
                }
            }
        }

        // Botones de navegación
        final ItemStack next = new ItemBuilder(Material.ARROW).setName("&aNext Page").setLore("&7Click to go to the next page").build();
        final ItemStack prev = new ItemBuilder(Material.ARROW).setName("&aPrevious Page").setLore("&7Click to go to the previous page").build();

        if (page == 1) {
            inventory.setItem(41, next);
        }
        else if (page == 11) {
            inventory.setItem(39, prev);
        }
        else {
            inventory.setItem(39, prev);
            inventory.setItem(41, next);
        }

        final ItemStack back = new ItemBuilder(Material.REDSTONE).setName("&cBack Button").setLore("&7Use this button to go back", "&7to the Battlepass Menu").setGlow().build();
        inventory.setItem(44, back);

        player.openInventory(inventory);
        PlayerMenu.addMenu(player, PlayerMenu.RewardsMenu);
    }
}