package io.github.maths.shiny.commands.cmd;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;

public class TimeCommand implements CommandExecutor {

    private final Shiny plugin;
    public TimeCommand(Shiny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(CC.line("&e"));
            sender.sendMessage(CC.translate("&f- &e/tc day &7- &6Set the time to day"));
            sender.sendMessage(CC.translate("&f- &e/tc night &7- &6Set the time to night"));
            sender.sendMessage(CC.translate("&f- &e/tc disable &7- &6Disable the time"));
            sender.sendMessage(CC.line("&e"));
            return true;
        }

        if (args[0].equalsIgnoreCase("day")) {
            sender.sendMessage(CC.translate("&aTime set to day"));
            Bukkit.getWorlds().forEach(e -> e.setTime(6000L));
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule doDaylightCycle false");
            return true;
        }

        if (args[0].equalsIgnoreCase("night")) {
            sender.sendMessage(CC.translate("&aTime set to night"));
            Bukkit.getWorlds().forEach(e -> e.setTime(20000L));
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule doDaylightCycle false");
            return true;
        }

        if (args[0].equalsIgnoreCase("disable")) {
            sender.sendMessage(CC.translate("&aTime disabled"));
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule doDaylightCycle true");
            return true;
        }

        sender.sendMessage(CC.line("&e"));
        sender.sendMessage(CC.translate("&f- &e/tc day &7- &6Set the time to day"));
        sender.sendMessage(CC.translate("&f- &e/tc night &7- &6Set the time to night"));
        sender.sendMessage(CC.translate("&f- &e/tc disable &7- &6Disable the time"));
        sender.sendMessage(CC.line("&e"));
        return true;
    }
}