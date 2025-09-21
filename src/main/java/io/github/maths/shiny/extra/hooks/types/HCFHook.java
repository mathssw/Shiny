package io.github.maths.shiny.extra.hooks.types;


import io.github.maths.shiny.extra.hooks.Hook;

public interface HCFHook extends Hook {
    boolean hasSotwActive();
    int getSotwTime();
    int getNormalTime();

    @Override
    default String getType() {
        return "HCF";
    }
}