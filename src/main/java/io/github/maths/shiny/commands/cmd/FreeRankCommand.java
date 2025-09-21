package io.github.maths.shiny.commands.cmd;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.managers.FreeRankManager;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreeRankCommand implements CommandExecutor {

    private final Shiny plugin;
    public FreeRankCommand(Shiny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cThis command can only be used in-game"));
            return true;
        }

        Player player = (Player) sender;
        FreeRankManager manager = Shiny.getInstance().getFreeRankManager();
        ConfigurationFile config = Shiny.getInstance().getConfig();

        if (!manager.isEnabled()) {
            player.sendMessage(CC.translate(
                    config.getString("FreeRank.Disabled-Message")
                            .replace("%time%", config.getString("FreeRank.End-Date").split(" ")[0])
            ));
            return true;
        }

        if (manager.alreadyClaimed(player)) {
            player.sendMessage(CC.translate(config.getString("FreeRank.Already-Claimed-Message")));
            return true;
        }

        if (!manager.getNameMCVote(player)) {
            player.sendMessage(CC.translate(config.getString("FreeRank.Unavailable-Message")));
            return true;
        }

        manager.giveRank(player);
        return true;
    }
}
