package io.github.maths.shiny.utils;

import io.github.maths.shiny.Shiny;
import org.bukkit.*;
import java.util.function.*;
import java.util.*;
import java.io.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;

public class FallbackMessage
{
    public FallbackMessage() {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(b);
        final UUID[] uuids = (UUID[])Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getUniqueId).toArray(UUID[]::new);
        final String[] names = (String[])Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).toArray(String[]::new);
        try {
            out.writeUTF("Fallback");
            out.writeUTF(String.join(" ", (CharSequence[])Arrays.stream(uuids).map((Function<? super UUID, ?>)UUID::toString).toArray(String[]::new)));
            out.writeUTF(String.join(" ", (CharSequence[])names));
            out.writeUTF(Shiny.getInstance().getServer().getName());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        final Player player = Bukkit.getPlayer(uuids[0]);
        player.sendPluginMessage((Plugin) Shiny.getInstance(), "Shiny", b.toByteArray());
    }
}
