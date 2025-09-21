package io.github.maths.shiny.commands.cmd;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.BukkitUtils;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class BattlepassAdminCommand implements CommandExecutor {

    private final Shiny plugin;
    public BattlepassAdminCommand(Shiny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players can use this command"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "give":
                if (args.length != 2) {
                    player.sendMessage(CC.translate("&cUsage: /bpadmin give (player)"));
                    return true;
                }
                giveBattlepass(player, args[1]);
                break;

            case "setxp":
                if (args.length != 3) {
                    player.sendMessage(CC.translate("&cUsage: /bpadmin setxp (player) (xp)"));
                    return true;
                }
                setXP(player, args[1], args[2]);
                break;

            case "addxp":
                if (args.length != 3) {
                    player.sendMessage(CC.translate("&cUsage: /bpadmin addxp (player) (xp)"));
                    return true;
                }
                addXP(player, args[1], args[2]);
                break;

            case "setlevel":
                if (args.length != 3) {
                    player.sendMessage(CC.translate("&cUsage: /bpadmin setlevel (player) (level)"));
                    return true;
                }
                setLevel(player, args[1], args[2]);
                break;

            case "resetrewards":
                if (args.length != 2) {
                    player.sendMessage(CC.translate("&cUsage: /bpadmin resetrewards (player)"));
                    return true;
                }
                resetRewards(player, args[1]);
                break;

            case "setreward":
                if (args.length < 2) {
                    player.sendMessage(CC.translate("&cUsage: /bpadmin setreward (reward) (index)"));
                    return true;
                }
                setReward(player, args);
                break;

            case "addreward":
                if (args.length != 2) {
                    player.sendMessage(CC.translate("&cUsage: /bpadmin addreward (reward)"));
                    return true;
                }
                addReward(player, args[1]);
                break;

            case "resetall":
                resetAll(player);
                break;

            default:
                sendUsage(player);
        }

        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(CC.line("&e"));
        player.sendMessage(CC.translate("&e&lBattlepass Admin Usage:"));
        player.sendMessage(CC.translate("&a"));
        player.sendMessage(CC.translate("&e/bpadmin give (player)"));
        player.sendMessage(CC.translate("&e/bpadmin setxp (player) (xp)"));
        player.sendMessage(CC.translate("&e/bpadmin addxp (player) (xp)"));
        player.sendMessage(CC.translate("&e/bpadmin setlevel (player) (level)"));
        player.sendMessage(CC.translate("&e/bpadmin resetrewards (player)"));
        player.sendMessage(CC.translate("&e/bpadmin resetall"));
        player.sendMessage(CC.translate("&e/bpadmin setreward (reward) (index)"));
        player.sendMessage(CC.translate("&e/bpadmin addreward (reward)"));
        player.sendMessage(CC.line("&e"));
    }

    private void giveBattlepass(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(CC.translate("&cThis player does not exist"));
            return;
        }

        String cmd = Shiny.getInstance().getBattlepassConfig().getString("Command")
                .replace("%player%", target.getName())
                .replace("%time%", Shiny.getInstance().getBattlepassManager().getRemainingTo(
                        Shiny.getInstance().getBattlepassConfig().getInt("Data.Max-Days")) / 1000L + "s");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        player.sendMessage(CC.translate("&aSuccessfully gave " + target.getName() + " the Premium Battlepass!"));
    }

    private void setXP(Player player, String targetName, String xpStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(CC.translate("&cThis player does not exist"));
            return;
        }
        int xp;
        try {
            xp = Integer.parseInt(xpStr);
        } catch (NumberFormatException e) {
            player.sendMessage(CC.translate("&cInvalid XP value"));
            return;
        }
        Shiny.getInstance().getBattlepassManager().battlepasses.get(target).setXp(xp);
        player.sendMessage(CC.translate("&aSuccessfully set the xp of " + target.getName() + " to " + xp));
    }

    private void addXP(Player player, String targetName, String xpStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(CC.translate("&cThis player does not exist"));
            return;
        }
        int xp;
        try {
            xp = Integer.parseInt(xpStr);
        } catch (NumberFormatException e) {
            player.sendMessage(CC.translate("&cInvalid XP value"));
            return;
        }
        Shiny.getInstance().getBattlepassManager().battlepasses.get(target).addXP(xp);
        player.sendMessage(CC.translate("&aSuccessfully added the xp to " + target.getName()));
    }

    private void setLevel(Player player, String targetName, String levelStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(CC.translate("&cThis player does not exist"));
            return;
        }
        int level;
        try {
            level = Integer.parseInt(levelStr);
        } catch (NumberFormatException e) {
            player.sendMessage(CC.translate("&cInvalid level value"));
            return;
        }
        Shiny.getInstance().getBattlepassManager().battlepasses.get(target).setLevel(level);
        player.sendMessage(CC.translate("&aSuccessfully set the level of " + target.getName() + " to " + level));
    }

    private void resetRewards(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(CC.translate("&cThis player does not exist"));
            return;
        }
        Shiny.getInstance().getBattlepassManager().battlepasses.get(target).getRewards().clear();
        player.sendMessage(CC.translate("&aSuccessfully reset the rewards of " + target.getName()));
    }

    private void resetAll(Player player) {
        Shiny.getInstance().getBattlepassManager().getData().set("data", "{}");
        Shiny.getInstance().getBattlepassManager().getData().save();
        player.sendMessage(CC.translate("&aSuccessfully reset all data"));
    }

    private void setReward(Player player, String[] args) {
        String reward = args[1];
        ConfigurationFile rewards = Shiny.getInstance().getRewardConfig();
        if (!rewards.contains("Rewards." + reward)) {
            player.sendMessage(CC.translate("&cThis reward does not exist"));
            return;
        }

        int index = -1;
        if (args.length == 3) {
            try {
                index = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(CC.translate("&cInvalid index, 0-99"));
            }
        }

        ConfigurationSection section = rewards.getConfigurationSection("Rewards." + reward + ".Command");
        ItemStack item = player.getItemInHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(CC.translate("&cYou must hold an item in your hand"));
            return;
        }

        if (index == -1) {
            rewards.set("Rewards." + reward + ".RewardType", "Item");
            rewards.set("Rewards." + reward + ".Command", BukkitUtils.serializeItemStack(item));
            rewards.save();
            player.sendMessage(CC.translate("&aSuccessfully set the reward"));
            return;
        }

        if (section.getKeys(false).isEmpty()) {
            player.sendMessage(CC.translate("&cThis reward doesn't have multiple items, please add one first"));
            return;
        }

        if (section.getKeys(false).size() <= index) {
            player.sendMessage(CC.translate("&cYou are using an invalid index, use 0-" + (section.getKeys(false).size() - 1)));
            return;
        }

        String key = new ArrayList<>(section.getKeys(false)).get(index);
        section.set(key, BukkitUtils.serializeItemStack(item));
        rewards.save();
        player.sendMessage(CC.translate("&aSuccessfully set the reward"));
    }

    private void addReward(Player player, String reward) {
        ConfigurationFile rewards = Shiny.getInstance().getRewardConfig();
        if (!rewards.contains("Rewards." + reward)) {
            player.sendMessage(CC.translate("&cThis reward does not exist"));
            return;
        }

        ConfigurationSection section = rewards.getConfigurationSection("Rewards." + reward + ".Command");
        ItemStack item = player.getItemInHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(CC.translate("&cYou must hold an item in your hand"));
            return;
        }

        rewards.set("Rewards." + reward + ".RewardType", "MultiItem");
        String key = (section.getKeys(false).size()) + "";
        section.set(key, BukkitUtils.serializeItemStack(item));
        rewards.save();
        player.sendMessage(CC.translate("&aSuccessfully added the reward"));
    }
}
