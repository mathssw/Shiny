package io.github.maths.shiny.managers.battlepass;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.Title;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.event.*;
import java.time.format.*;
import org.bukkit.scheduler.*;
import org.bukkit.plugin.*;
import java.time.temporal.*;
import java.text.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.entity.*;
import org.bukkit.*;

public class BattlepassManager implements Listener
{
    private int day;
    private ConfigurationFile config;
    private ConfigurationFile data;
    private LocalDate started;
    public Map<OfflinePlayer, Battlepass> battlepasses;

    public BattlepassManager() {
        this.config = new ConfigurationFile(Shiny.getInstance(), "battlepass/battlepass.yml");

        // CÓDIGO CORREGIDO: Manejo de fecha con validación
        String dateString = this.config.getString("Data.First-Date");
        if (dateString == null || dateString.isEmpty()) {
            // Si no hay fecha, usar la fecha actual y guardarla
            this.started = LocalDate.now();
            this.config.set("Data.First-Date", this.started.format(DateTimeFormatter.ofPattern("MM/dd/yy")));
            this.config.save();
        } else {
            try {
                this.started = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("MM/dd/yy"));
            } catch (DateTimeParseException e) {
                // Si el formato es inválido, usar fecha actual
                this.started = LocalDate.now();
                this.config.set("Data.First-Date", this.started.format(DateTimeFormatter.ofPattern("MM/dd/yy")));
                this.config.save();
            }
        }

        this.updateDay();
        this.data = new ConfigurationFile(Shiny.getInstance(), "storage/battlepassdata.yml");
        this.battlepasses = new HashMap<OfflinePlayer, Battlepass>();
        new BukkitRunnable() {
            public void run() {
                if (BattlepassManager.this.data.getConfigurationSection("data") != null && BattlepassManager.this.data.getConfigurationSection("data").getKeys(false) != null && !BattlepassManager.this.data.getConfigurationSection("data").getKeys(false).isEmpty()) {
                    for (final String key : BattlepassManager.this.data.getConfigurationSection("data").getKeys(false)) {
                        BattlepassManager.this.battlepasses.put(Bukkit.getOfflinePlayer(UUID.fromString(key)), new Battlepass(Bukkit.getOfflinePlayer(UUID.fromString(key)), BattlepassManager.this.data.getInt("data." + key + ".xp"), BattlepassManager.this.data.getInt("data." + key + ".level"), BattlepassManager.this.data.getStringList("data." + key + ".quests"), BattlepassManager.this.data.getConfigurationSection("data." + key + ".pending"), BattlepassManager.this.data.getStringList("data." + key + ".rewards"), BattlepassManager.this.data.getStringList("data." + key + ".dailys"), BattlepassManager.this.data.getConfigurationSection("data." + key + ".pendingDailys")));
                    }
                }
            }
        }.runTaskLaterAsynchronously((Plugin) Shiny.getInstance(), 1L);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, (Plugin) Shiny.getInstance());
    }

    public void updateDay() {
        final SimpleDateFormat format = Shiny.getInstance().getFormat();
        format.setTimeZone(TimeZone.getTimeZone("EST"));
        final LocalDate today = LocalDate.now();
        this.day = (int)ChronoUnit.DAYS.between(this.started, today);
    }

    public long getRemaining() {
        final LocalDateTime localDateTime = LocalDateTime.now().withSecond(59).withHour(23).withMinute(59);
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), localDateTime) * 1000L;
    }

    public void register(final OfflinePlayer player) {
        this.data.set("data." + player.getUniqueId().toString() + ".pending", (Object)"");
        this.data.set("data." + player.getUniqueId().toString() + ".dailys", (Object)"");
        this.data.save();
        this.battlepasses.put(player, new Battlepass(player, 0, 0, new ArrayList<String>(), this.data.getConfigurationSection("data." + player.getUniqueId() + ".pending"), new ArrayList<String>(), new ArrayList<String>(), this.data.getConfigurationSection("data." + player.getUniqueId() + ".pendingDailys")));
    }

    public long getRemainingTo(final int day) {
        final LocalDateTime end = this.started.plusDays(day).atStartOfDay();
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), end) * 1000L;
    }

    public void announceLevel(final Player player, final int level) {
        player.playSound(player.getLocation(), Sound.FIREWORK_BLAST, 5.0f, 1.0f);
        new BukkitRunnable() {
            public void run() {
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 3.0f, 1.0f);
            }
        }.runTaskLater((Plugin) Shiny.getInstance(), 2L);
        player.sendMessage(CC.translate("&aYou have unlocked a new level, you are now level &e" + level + "!"));
        final Title title = new Title();
        title.setTitle(CC.translate(CC.translate(this.config.getString("Announces.Title").replace("%level%", level + ""))));
        title.setSubtitle(CC.translate(this.config.getString("Announces.Subtitle").replace("%level%", level + "")));
        title.setTimingsToTicks();
        title.setFadeInTime(10);
        title.setFadeOutTime(10);
        title.setStayTime(60);
        title.send(player);
    }

    public void announceXP(final Player player, final int xp) {
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 3.0f, 1.0f);
        player.sendMessage(CC.translate(this.config.getString("Announces.XP").replace("%xp%", xp + "")));
    }

    public void save() {
        for (final OfflinePlayer key : this.battlepasses.keySet()) {
            final String name = key.getUniqueId().toString();
            final Battlepass battlepass = this.battlepasses.get(key);
            this.data.set("data." + name + ".xp", (Object)battlepass.getXp());
            this.data.set("data." + name + ".level", (Object)battlepass.getLevel());
            this.data.set("data." + name + ".quests", (Object)battlepass.getQuests().stream().map(e -> e.getName()).collect(Collectors.toList()));
            for (final Quest quest : battlepass.getPendingQuests().keySet()) {
                this.data.set("data." + name + ".pending." + quest.getName() + ".amount", (Object)battlepass.getPendingQuests().get(quest));
            }
            this.data.set("data." + name + ".rewards", (Object)battlepass.getRewards());
            this.data.set("data." + name + ".dailys", (Object)battlepass.getDailys().stream().map(e -> e.getName()).collect(Collectors.toList()));
            for (final Daily quest2 : battlepass.getPendingDailys().keySet()) {
                this.data.set("data." + name + ".pendingDailys." + quest2.getName() + ".amount", (Object)battlepass.getPendingDailys().get(quest2));
            }
            this.data.save();
        }
    }

    public int getDay() {
        return this.day;
    }

    public ConfigurationFile getConfig() {
        return this.config;
    }

    public ConfigurationFile getData() {
        return this.data;
    }

    public LocalDate getStarted() {
        return this.started;
    }

    public Map<OfflinePlayer, Battlepass> getBattlepasses() {
        return this.battlepasses;
    }
}