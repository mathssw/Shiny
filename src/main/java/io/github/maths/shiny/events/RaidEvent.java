package io.github.maths.shiny.events;

import org.bukkit.event.*;
import org.bukkit.entity.*;

public class RaidEvent extends Event implements Cancellable
{
    private static final HandlerList handlers;
    private final Player player;
    private boolean isCancelled;
    
    public RaidEvent(final Player player) {
        this.player = player;
        this.isCancelled = false;
    }
    
    public HandlerList getHandlers() {
        return RaidEvent.handlers;
    }
    
    public boolean isCancelled() {
        return this.isCancelled;
    }
    
    public void setCancelled(final boolean b) {
        this.isCancelled = b;
    }
    
    public static HandlerList getHandlerList() {
        return RaidEvent.handlers;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    static {
        handlers = new HandlerList();
    }
}
