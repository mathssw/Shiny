package io.github.maths.shiny.managers;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.BukkitUtils;
import io.github.maths.shiny.utils.ConfigurationFile;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;

import java.util.*;
import org.bukkit.event.player.*;
import org.bukkit.block.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.util.Vector;

public class LaunchpadManager implements Listener
{
    private ConfigurationFile config;
    private List<Location> launchpads;
    private List<Player> falldamage;
    private List<String> locations;

    public LaunchpadManager() {
        this.falldamage = new ArrayList<Player>();
        this.config = new ConfigurationFile(Shiny.getInstance(), "storage/launchpads.yml");
        this.locations = this.config.getStringList("Launchpads");
        this.launchpads = new ArrayList<Location>();
        for (final String key : this.locations) {
            this.launchpads.add(BukkitUtils.deserializeLocation(key));
        }
        Shiny.getInstance().getServer().getPluginManager().registerEvents((Listener)this, (Plugin)Shiny.getInstance());
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        final Location to = event.getTo();
        final Player player = event.getPlayer();
        if (to.getBlock().getRelative(BlockFace.SELF).getType() == Material.GOLD_PLATE && this.launchpads.stream().anyMatch(e -> this.locequals(e, to))) {
            event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(3));
            event.getPlayer().setVelocity(new Vector(event.getPlayer().getVelocity().getX(), 1.0, event.getPlayer().getVelocity().getZ()));
            player.playSound(player.getLocation(), Sound.FIREWORK_BLAST, 3.0f, 1.0f);
            this.falldamage.add(player);
        }
    }

    @EventHandler
    public void onPlayerDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player)event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && this.falldamage.contains(player)) {
                event.setCancelled(true);
                this.falldamage.remove(player);
            }
        }
    }

    @EventHandler
    public void onPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (event.getBlockPlaced().getType() == Material.GOLD_PLATE && player.hasPermission("Shiny.launchpads")) {
            this.locations.add(BukkitUtils.serializeLocation(event.getBlockPlaced().getLocation()));
            this.config.set("Launchpads", this.locations);
            this.config.save();
            this.launchpads.add(event.getBlockPlaced().getLocation());
        }
    }

    private boolean locequals(final Location loc1, final Location loc2) {
        final int x1 = loc1.getBlockX();
        final int y1 = loc1.getBlockY();
        final int z1 = loc1.getBlockZ();
        final int x2 = loc2.getBlockX();
        final int y2 = loc2.getBlockY();
        final int z2 = loc2.getBlockZ();
        return x1 == x2 && y1 == y2 && z1 == z2;
    }
}