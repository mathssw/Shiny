package io.github.maths.shiny.events;

import org.bukkit.event.*;
import org.bukkit.entity.*;

public class ChatReactEvent extends Event implements Cancellable
{
    private static final HandlerList handlers;
    private final Player player;
    private boolean isCancelled;
    
    public ChatReactEvent(final Player player) {
        this.player = player;
        this.isCancelled = false;
    }
    
    public HandlerList getHandlers() {
        return ChatReactEvent.handlers;
    }
    
    public boolean isCancelled() {
        return this.isCancelled;
    }
    
    public void setCancelled(final boolean b) {
        this.isCancelled = b;
    }
    
    public static HandlerList getHandlerList() {
        return ChatReactEvent.handlers;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    static {
        handlers = new HandlerList();
    }
}
