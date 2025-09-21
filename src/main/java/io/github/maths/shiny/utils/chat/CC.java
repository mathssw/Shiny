package io.github.maths.shiny.utils.chat;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.bukkit.*;

public class CC
{
    public static String line(final String color) {
        return translate(color + "&m------------------------");
    }

    public static String translate(final String source) {
        return ChatColor.translateAlternateColorCodes('&', source);
    }

    public static List<String> translate(final List<String> source) {
        return source.stream().map(CC::translate).collect(Collectors.toList());
    }

    public static void log(final String message) {
        Bukkit.getConsoleSender().sendMessage(translate("&e[Shiny] &7" + message));
    }
}