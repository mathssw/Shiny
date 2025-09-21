package io.github.maths.shiny.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.chat.CC;

public class HighrollerPlayerListener implements Listener {

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (Shiny.getInstance().hasRank(player.getUniqueId(), "highroller")) {
            Shiny.getInstance().getHighrollerManager().loadPrefix(player);
        }

        if (player.hasPermission("alert.join")) {
            if (!Shiny.getInstance().getConfig().getBoolean("Join-Alert")) {
                return;
            }
            Bukkit.broadcastMessage(CC.translate("&4[Alert] " + player.getName() + " &ehas joined the hub."));
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (Shiny.getInstance().hasRank(player.getUniqueId(), "highroller")) {
            Shiny.getInstance().getHighrollerManager().savePrefix(player);
        }
    }
}