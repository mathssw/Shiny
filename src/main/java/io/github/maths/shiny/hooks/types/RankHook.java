package io.github.maths.shiny.hooks.types;

import io.github.maths.shiny.hooks.Hook;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface RankHook extends Hook {
    boolean hasRank(UUID playerId, String rankName);
    String getPlayerRank(UUID playerId);
    String getPrefix(UUID playerId);
    String getSuffix(UUID playerId);
    boolean isLoaded();

    @Override
    default String getType() {
        return "RANK";
    }
}
