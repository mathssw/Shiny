package io.github.maths.shiny.managers;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.ConfigurationFile;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class CleanManager {

    private final ConfigurationFile config;

    public CleanManager() {
        this.config = Shiny.getInstance().getConfig();
        this.startCleanTask();
    }

    private void startCleanTask() {
        int time = this.getTime();

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lag clear");
            }
        }.runTaskTimer(Shiny.getInstance(), 0L, time * 20L);
    }

    private int getTime() {
        return Shiny.getInstance().getCleanTime();
    }
}