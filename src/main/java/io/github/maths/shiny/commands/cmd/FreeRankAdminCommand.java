package io.github.maths.shiny.commands.cmd;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

public class FreeRankAdminCommand implements CommandExecutor {

    private final Shiny plugin;
    public FreeRankAdminCommand(Shiny plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(CC.line("&e"));
            sender.sendMessage(CC.translate("&f- &e/freerankadmin reset (all / player)"));
            sender.sendMessage(CC.translate("&f- &e/freerankadmin change (MM/dd/yy) (hh:mm:ss)"));
            sender.sendMessage(CC.translate("&a"));
            sender.sendMessage(CC.translate("&6Example:"));
            sender.sendMessage(CC.translate("&f- &e/freerankadmin change 03/30/24 00:00:00"));
            sender.sendMessage(CC.line("&e"));
            return true;
        }

        // Reset command
        if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            if (args[1].equalsIgnoreCase("all")) {
                Shiny.getInstance().getFreeRankManager().resetall();
                sender.sendMessage(CC.translate("&aSuccessfully reset all freeranks"));
                return true;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
            if (!Shiny.getInstance().getFreeRankManager().alreadyClaimed(offlinePlayer)) {
                sender.sendMessage(CC.translate("&cThis player has not claimed the freerank reward"));
                return true;
            }

            Shiny.getInstance().getFreeRankManager().reset(offlinePlayer);
            sender.sendMessage(CC.translate("&aSuccessfully reset the freerank for " + args[1]));
            return true;
        }

        // Change end date command
        if (args.length == 3 && args[0].equalsIgnoreCase("change")) {
            Pattern datePattern = Pattern.compile("^\\d{2}/\\d{2}/\\d{2}$");
            if (!datePattern.matcher(args[1]).matches()) {
                sender.sendMessage(CC.translate("&cThe date format is invalid"));
                return true;
            }

            Pattern timePattern = Pattern.compile("^([01]?\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$");
            if (!timePattern.matcher(args[2]).matches()) {
                sender.sendMessage(CC.translate("&cThe time format is invalid"));
                return true;
            }

            Shiny.getInstance().getConfig().set("FreeRank.End-Date", args[1] + " " + args[2]);
            Shiny.getInstance().saveConfig();
            sender.sendMessage(CC.translate("&aSuccessfully changed the FreeRank end time"));
            return true;
        }

        // Default usage
        sender.sendMessage(CC.line("&e"));
        sender.sendMessage(CC.translate("&f- &e/freerankadmin reset (all / player)"));
        sender.sendMessage(CC.translate("&f- &e/freerankadmin change (MM/dd/yy) (hh:mm:ss)"));
        sender.sendMessage(CC.line("&e"));
        return true;
    }
}
