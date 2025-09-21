package io.github.maths.shiny.extra.hooks.impl.hcf;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.extra.hooks.types.HCFHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import me.keano.azurite.HCF;

public class AzuriteHook implements HCFHook {

    private HCF hcf;
    private boolean enabled = false;

    @Override
    public String getName() {
        return "Azurite";
    }

    @Override
    public boolean isEnabled() {
        return enabled && hcf != null;
    }

    @Override
    public void enable() {
        try {
            final Plugin plugin = Bukkit.getPluginManager().getPlugin("Azurite");
            if (plugin != null && plugin.isEnabled()) {
                this.hcf = (HCF) plugin;
                this.enabled = true;
            }
        } catch (Exception e) {
            this.enabled = false;
        }
    }

    @Override
    public void disable() {
        this.hcf = null;
        this.enabled = false;
    }

    @Override
    public boolean hasSotwActive() {
        return isEnabled() && hcf.getSotwManager().isActive();
    }

    @Override
    public int getSotwTime() {
        return Shiny.getInstance().getHooksConfig().getInt("HCF.Azurite.sotw-time", 30);
    }

    @Override
    public int getNormalTime() {
        return Shiny.getInstance().getHooksConfig().getInt("HCF.Azurite.normal-time", 60);
    }
}