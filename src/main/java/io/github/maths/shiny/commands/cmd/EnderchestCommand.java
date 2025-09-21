package io.github.maths.shiny.commands.cmd;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.CooldownUtil;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderchestCommand implements CommandExecutor {

    private final Shiny plugin;
    public EnderchestCommand(Shiny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players can execute this command!"));
            return true;
        }

        Player player = (Player) sender;

        // No arguments: open own enderchest
        if (args.length == 0) {
            if (player.hasPermission("Shiny.enderchest.bypass")) {
                Shiny.getInstance().getEnderchestManager().open(player);
                return true;
            }

            if (CooldownUtil.hasCooldown(player)) {
                long seconds = CooldownUtil.getCooldown(player) / 1000L;
                player.sendMessage(CC.translate("&cYou are on cooldown for " + seconds + " seconds"));
                return true;
            }

            int cooldown = Shiny.getInstance().getConfig().getInt("Enderchest.Cooldown");
            CooldownUtil.setCooldown(player, cooldown);
            Shiny.getInstance().getEnderchestManager().open(player);
            return true;
        }

        // Inspect another player's enderchest
        if (player.hasPermission("Shiny.enderchest.inspect")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null) {
                player.sendMessage(CC.translate("&cThis player does not exist"));
                return true;
            }

            Shiny.getInstance().getEnderchestManager().inspect(player, target);
        } else {
            player.sendMessage(CC.translate("&cYou are not allowed to perform this command"));
        }

        return true;
    }
}
