package io.github.maths.shiny.commands.cmd;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NameMCCommand implements CommandExecutor {

    private final Shiny plugin;

    public NameMCCommand(Shiny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players can use this command."));
            return true;
        }

        Player player = (Player) sender;

        boolean hasRegisteredVote = plugin.getFreeRankManager().hasRegisteredVote(player);
        boolean canVote = plugin.getFreeRankManager().getNameMCVote(player);

        if (!hasRegisteredVote && canVote) {
            String firstVoteMessage = plugin.getConfig().getString("NameMC.First-Vote");
            if (firstVoteMessage != null) player.sendMessage(CC.translate(firstVoteMessage));

            String commandStr = plugin.getConfig().getString("NameMC.Command");
            if (commandStr != null && !commandStr.isEmpty()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandStr);
            }
            return true;
        }

        if (canVote) {
            player.sendMessage(CC.translate("&aYou already voted for our server"));
        } else {
            String notVotedMessage = plugin.getConfig().getString("FreeRank.NameMC-Not-Voted");
            if (notVotedMessage != null) player.sendMessage(CC.translate(notVotedMessage));
        }

        return true;
    }
}
