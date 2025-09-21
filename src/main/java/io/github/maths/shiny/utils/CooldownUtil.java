package io.github.maths.shiny.utils;

import org.bukkit.entity.*;
import java.util.*;

public class CooldownUtil
{
    private static final Map<UUID, Long> cooldown;
    
    public static boolean hasCooldown(final Player player) {
        return CooldownUtil.cooldown.containsKey(player.getUniqueId()) && CooldownUtil.cooldown.get(player.getUniqueId()) > System.currentTimeMillis();
    }
    
    public static void setCooldown(final Player player, final int time) {
        CooldownUtil.cooldown.put(player.getUniqueId(), System.currentTimeMillis() + time * 1000L);
    }
    
    public static long getCooldown(final Player player) {
        return CooldownUtil.cooldown.get(player.getUniqueId()) - System.currentTimeMillis();
    }
    
    public static void removeCooldown(final String kit, final Player player) {
        CooldownUtil.cooldown.remove(kit, player.getUniqueId());
    }
    
    public static Map<UUID, Long> getCooldown() {
        return CooldownUtil.cooldown;
    }
    
    static {
        cooldown = new HashMap<UUID, Long>();
    }
}
