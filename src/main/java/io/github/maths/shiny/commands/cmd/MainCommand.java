package io.github.maths.shiny.commands.cmd;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MainCommand implements CommandExecutor {

    private final Shiny plugin;

    public MainCommand(Shiny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();

        if (args.length == 0) {
            sender.sendMessage(CC.line("&e"));
            sender.sendMessage(CC.translate("&6Usage:"));
            sender.sendMessage(CC.translate("&f- &e/shiny reload &7- &6Reload the config"));
            sender.sendMessage(CC.translate("&f- &e/freerankadmin &7- &6FreeRank manager command"));
            sender.sendMessage(CC.translate("&f- &e/shiny save &7- &6Toggle save-all"));
            sender.sendMessage(CC.line("&e"));
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "reload":
                plugin.reloadConfig();
                sender.sendMessage(CC.translate("&aSuccessfully reloaded the config"));
                break;

            case "save":
                boolean autoSave = config.getBoolean("Auto-Save", false);

                if (autoSave) {
                    // Disable
                    config.set("Auto-Save", false);
                    if (plugin.runnable != null) {
                        plugin.runnable.cancel();
                        plugin.runnable = null;
                    }
                    sender.sendMessage(CC.translate("&aSuccessfully disabled save-all"));
                } else {
                    // Enable
                    plugin.runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
                        }
                    }.runTaskTimer((Plugin) plugin, 0L, 6000L); // 5 min interval
                    config.set("Auto-Save", true);
                    sender.sendMessage(CC.translate("&aSuccessfully enabled save-all"));
                }

                plugin.saveConfig();
                break;

            default:
                sender.sendMessage(CC.translate("&cUnknown subcommand."));
                break;
        }

        return true;
    }
}
