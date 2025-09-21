package io.github.maths.shiny.commands.cmd;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.menu.BattlePassMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BattlepassCommand implements CommandExecutor {

    private final Shiny plugin;
    public BattlepassCommand(Shiny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        new BattlePassMenu().open(player);
        return true;
    }
}
