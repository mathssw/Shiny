package io.github.maths.shiny.commands.cmd;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.extra.hooks.types.HCFHook;
import io.github.maths.shiny.extra.hooks.types.RankHook;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HooksCommand implements CommandExecutor {

    private final Shiny plugin;

    public HooksCommand(Shiny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("shiny.hooks")) {
            sender.sendMessage(CC.translate("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("test") && sender instanceof Player player) {
            testHooks(player);
            return true;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        String currentTime = dateFormat.format(new Date());
        long systemTime = System.currentTimeMillis();

        sender.sendMessage(CC.translate("&7&m-------------------"));
        sender.sendMessage(CC.translate("&6Hooks Information:"));
        sender.sendMessage(CC.translate("&7Current Time: &f" + currentTime));
        sender.sendMessage(CC.translate("&7System Time: &f" + systemTime));
        sender.sendMessage("");

        HCFHook hcfHook = plugin.getHookManager().getHCFHook();
        sender.sendMessage(CC.translate("&eHCF Hook: &a" + hcfHook.getName()));
        sender.sendMessage(CC.translate("&eSOTW Active: " + (hcfHook.hasSotwActive() ? "&aYes" : "&cNo")));

        int cleanTime = plugin.getCleanTime();
        sender.sendMessage(CC.translate("&eClean Time: &f" + cleanTime + "s &7(" + formatTime(cleanTime) + ")"));

        if (hcfHook.hasSotwActive()) {
            int sotwTime = hcfHook.getSotwTime();
            sender.sendMessage(CC.translate("&eSoTW Time: &f" + sotwTime + "s &7(" + formatTime(sotwTime) + ")"));
        }

        sender.sendMessage("");

        RankHook rankHook = plugin.getHookManager().getRankHook();
        sender.sendMessage(CC.translate("&eRank Hook: &a" + rankHook.getName()));
        sender.sendMessage(CC.translate("&eRank Loaded: " + (rankHook.isLoaded() ? "&aYes" : "&cNo")));
        sender.sendMessage("");

        sender.sendMessage(CC.translate("&6Active Hooks:"));
        plugin.getHookManager().getActiveHooks().forEach((type, hook) -> {
            String status = hook.isEnabled() ? "&aEnabled" : "&cDisabled";
            sender.sendMessage(CC.translate("&7- &f" + type + ": " + hook.getName() + " " + status));
        });

        sender.sendMessage("");
        sender.sendMessage(CC.translate("&7Use &f/hooks test &7to test hook functionality."));
        sender.sendMessage(CC.translate("&7&m-------------------"));

        return true;
    }

    private void testHooks(Player player) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String testTime = dateFormat.format(new Date());

        player.sendMessage(CC.translate("&6Testing Hooks &7(" + testTime + "):"));
        player.sendMessage("");

        String rank = plugin.getPlayerRank(player.getUniqueId());
        String prefix = plugin.getPlayerPrefix(player.getUniqueId());
        boolean hasHighroller = plugin.hasRank(player.getUniqueId(), "highroller");

        player.sendMessage(CC.translate("&eYour Rank: &f" + rank));
        player.sendMessage(CC.translate("&eYour Prefix: &f" + prefix));
        player.sendMessage(CC.translate("&eHas Highroller: " + (hasHighroller ? "&aYes" : "&cNo")));
        player.sendMessage(CC.translate("&eSOTW Active: " + (plugin.isSotwActive() ? "&aYes" : "&cNo")));

        // Información adicional del jugador
        player.sendMessage(CC.translate("&eYour UUID: &f" + player.getUniqueId().toString()));
        player.sendMessage(CC.translate("&eTest completed at: &f" + testTime));
    }

    private String formatTime(int seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            int minutes = seconds / 60;
            int remainingSeconds = seconds % 60;
            return minutes + "m " + remainingSeconds + "s";
        } else {
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            int remainingSeconds = seconds % 60;
            return hours + "h " + minutes + "m " + remainingSeconds + "s";
        }
    }

}