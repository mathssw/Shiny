package io.github.maths.shiny.commands.cmd;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class ChatReactionCommand implements CommandExecutor {
    private final Shiny plugin;
    public ChatReactionCommand(Shiny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "start":
                if (!Shiny.getInstance().getChatReactionManager().getActive().isEmpty()) {
                    sender.sendMessage(CC.translate("&cThere is already an active chat reaction!"));
                    return true;
                }

                if (args.length == 1) {
                    Shiny.getInstance().getChatReactionManager().sendReaction();
                } else if (args.length == 2) {
                    Shiny.getInstance().getChatReactionManager().sendReaction(args[1]);
                } else {
                    sendUsage(sender);
                    return true;
                }

                sender.sendMessage(CC.translate("&aSuccessfully started a chat reaction!"));
                break;

            case "stop":
                if (Shiny.getInstance().getChatReactionManager().getActive().isEmpty()) {
                    sender.sendMessage(CC.translate("&cThere is no chat reaction active!"));
                    return true;
                }
                Shiny.getInstance().getChatReactionManager().stop();
                sender.sendMessage(CC.translate("&aSuccessfully stopped the chat reaction!"));
                break;

            case "list":
                ConfigurationSection section = Shiny.getInstance().getChatReactionManager().getReactions();
                sender.sendMessage(CC.line("&e"));
                sender.sendMessage(CC.translate("&eChat Reaction List: &f - &eName &f- &eType &f- &eAnswer"));
                for (String key : section.getKeys(false)) {
                    sender.sendMessage(CC.translate("&f* &e" + key + " &f- &e" +
                            section.getString(key + ".Type") + " &f- &e" +
                            section.getString(key + ".Answer")));
                }
                sender.sendMessage(CC.line("&e"));
                break;

            default:
                sendUsage(sender);
                break;
        }

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(CC.line("&6"));
        sender.sendMessage(CC.translate("&eChat Reaction Usage:"));
        sender.sendMessage(CC.translate("&f- &e/chatreaction start &f- &eStart random chat reaction"));
        sender.sendMessage(CC.translate("&f- &e/chatreaction start <name> &f- &eStart chat reaction"));
        sender.sendMessage(CC.translate("&f- &e/chatreaction stop &f- &eStop chat reaction"));
        sender.sendMessage(CC.translate("&f- &e/chatreaction list &f- &eList of chat reactions"));
        sender.sendMessage(CC.line("&6"));
    }
}
