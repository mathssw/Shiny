package io.github.maths.shiny.utils;

import org.bukkit.entity.*;
import java.util.*;

public enum PlayerMenu
{
    BattlepassMenu,
    RewardsMenu,
    QuestMenu,
    DayQuestMenu,
    DailyQuestMenu,
    RewardsSelector,
    HighrollerMenu;

    private static Map<Player, PlayerMenu> menu;

    public static void addMenu(final Player player, final PlayerMenu playerMenu) {
        PlayerMenu.menu.put(player, playerMenu);
    }

    public static boolean hasMenu(final Player player) {
        return PlayerMenu.menu.containsKey(player);
    }

    public static PlayerMenu getMenu(final Player player) {
        return PlayerMenu.menu.get(player);
    }

    public static void removeMenu(final Player player) {
        PlayerMenu.menu.remove(player);
    }

    static {
        menu = new HashMap<Player, PlayerMenu>();
    }
}