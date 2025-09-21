package io.github.maths.shiny.hooks.impl.hcf;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.hooks.types.HCFHook;

public class NoneHCFHook implements HCFHook {

    @Override
    public String getName() {
        return "None";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public boolean hasSotwActive() {
        return false;
    }

    @Override
    public int getSotwTime() {
        return Shiny.getInstance().getHooksConfig().getInt("HCF.Default.sotw-time", 30);
    }

    @Override
    public int getNormalTime() {
        return Shiny.getInstance().getHooksConfig().getInt("HCF.Default.normal-time", 60);
    }
}
