package io.github.maths.shiny.commands.cmd;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.menu.HighrollerMenu;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HighrollerCommand implements CommandExecutor {


    private final Shiny plugin;
    public HighrollerCommand(Shiny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cThis command can only be used in-game"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("Shiny.highroller")) {
            player.sendMessage(CC.translate("§cExclusive §9§lHigh§3§lRoller §ccommand! Bought at §bstore.koramc.us"));
            return true;
        }

        new HighrollerMenu().open(player);
        return true;
    }
}
