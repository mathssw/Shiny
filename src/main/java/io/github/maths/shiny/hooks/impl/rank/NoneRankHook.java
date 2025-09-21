package io.github.maths.shiny.hooks.impl.rank;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.hooks.types.RankHook;

import java.util.UUID;

public class NoneRankHook implements RankHook {

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
    public boolean hasRank(UUID playerId, String rankName) {
        return rankName.equalsIgnoreCase("default");
    }

    @Override
    public String getPlayerRank(UUID playerId) {
        return Shiny.getInstance().getHooksConfig().getString("Rank.Default.fallback-rank", "Default");
    }

    @Override
    public String getPrefix(UUID playerId) {
        return Shiny.getInstance().getHooksConfig().getString("Rank.Default.fallback-prefix", "");
    }

    @Override
    public String getSuffix(UUID playerId) {
        return Shiny.getInstance().getHooksConfig().getString("Rank.Default.fallback-suffix", "");
    }

    @Override
    public boolean isLoaded() {
        return true;
    }
}