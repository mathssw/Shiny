package io.github.maths.shiny.commands;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.commands.cmd.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager {

    public static void registerCommands(JavaPlugin plugin) {
        Shiny shinyPlugin = (Shiny) plugin;

        registerCommand(plugin, "tc", new TimeCommand(shinyPlugin));
        registerCommand(plugin, "namemc", new NameMCCommand(shinyPlugin));
        registerCommand(plugin, "shiny", new MainCommand(shinyPlugin));
        registerCommand(plugin, "hooks", new HooksCommand(shinyPlugin));
        registerCommand(plugin, "highroller", new HighrollerCommand(shinyPlugin));
        registerCommand(plugin, "freerank", new FreeRankCommand(shinyPlugin));
        registerCommand(plugin, "freerankadmin", new FreeRankAdminCommand(shinyPlugin));
        registerCommand(plugin, "enderchest", new EnderchestCommand(shinyPlugin));
        registerCommand(plugin, "chatreaction", new ChatReactionCommand(shinyPlugin));
        registerCommand(plugin, "battlepass", new BattlepassCommand(shinyPlugin));
        registerCommand(plugin, "bpadmin", new BattlepassAdminCommand(shinyPlugin));
    }

    private static void registerCommand(JavaPlugin plugin, String commandName, CommandExecutor executor) {
        PluginCommand command = plugin.getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
        } else {
            plugin.getLogger().warning("Command '" + commandName + "' not found in plugin.yml! Make sure to define it.");
        }
    }
}