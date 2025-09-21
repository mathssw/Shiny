package io.github.maths.shiny.managers.battlepass;

import io.github.maths.shiny.Shiny;

public class Quest
{
    private MissionTypes type;
    private int xp;
    private int day;
    private int amount;
    private boolean enabled;
    private String name;
    
    public Quest(final MissionTypes type, final int xp, final int day, final int amount, final String name) {
        this.type = type;
        this.xp = xp;
        this.day = day;
        this.amount = amount;
        this.enabled = (Shiny.getInstance().getBattlepassManager().getDay() >= day);
        this.name = name;
    }
    
    public MissionTypes getType() {
        return this.type;
    }
    
    public int getXp() {
        return this.xp;
    }
    
    public int getDay() {
        return this.day;
    }
    
    public int getAmount() {
        return this.amount;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setType(final MissionTypes type) {
        this.type = type;
    }
    
    public void setXp(final int xp) {
        this.xp = xp;
    }
    
    public void setDay(final int day) {
        this.day = day;
    }
    
    public void setAmount(final int amount) {
        this.amount = amount;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
