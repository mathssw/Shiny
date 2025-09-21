package io.github.maths.shiny.extra.hooks.types;

import io.github.maths.shiny.extra.hooks.Hook;

import java.util.UUID;

public interface RankHook extends Hook {
    boolean hasRank(UUID playerId, String rankName);
    String getPlayerRank(UUID playerId);
    String getPrefix(UUID playerId);
    String getSuffix(UUID playerId);
    boolean isLoaded();

    @Override
    default String getType() {
        return "Rank";
    }
}
