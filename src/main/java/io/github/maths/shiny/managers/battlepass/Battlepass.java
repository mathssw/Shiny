package io.github.maths.shiny.managers.battlepass;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.ConfigurationFile;
import org.bukkit.configuration.*;

import java.util.*;
import org.bukkit.*;

public class Battlepass
{
    private OfflinePlayer player;
    private int xp;
    private int level;
    private List<Quest> quests;
    private List<Daily> dailys;
    private Map<Daily, Integer> pendingDailys;
    private Map<Quest, Integer> pendingQuests;
    private List<String> rewards;

    public Battlepass(final OfflinePlayer player, final int xp, final int level, final List<String> quests, final ConfigurationSection pending, final List<String> rewards, final List<String> dailys, final ConfigurationSection pendingDailys) {
        this.player = player;
        this.xp = xp;
        this.level = level;
        this.quests = new ArrayList<Quest>();
        final ConfigurationFile config = Shiny.getInstance().getQuestConfig();
        for (final String key : quests) {
            final String questKey = key; // Create effectively final copy
            if (Shiny.getInstance().getQuestManager().quests.stream().noneMatch(e -> e.getName().equals(questKey))) {
                continue;
            }
            this.quests.add(Shiny.getInstance().getQuestManager().quests.stream().filter(e -> e.getName().equals(questKey)).findFirst().get());
        }
        this.pendingQuests = new HashMap<Quest, Integer>();
        if (pending != null && !pending.getKeys(false).isEmpty()) {
            for (final String key : pending.getKeys(false)) {
                final String pendingKey = key; // Create effectively final copy
                if (Shiny.getInstance().getQuestManager().quests.stream().noneMatch(e -> e.getName().equals(pendingKey))) {
                    continue;
                }
                this.pendingQuests.put(Shiny.getInstance().getQuestManager().quests.stream().filter(e -> e.getName().equals(pendingKey)).findFirst().get(), pending.getInt(key + ".amount"));
            }
        }
        this.rewards = rewards;
        this.dailys = new ArrayList<Daily>();
        if (dailys != null && !dailys.isEmpty()) {
            for (final String key : dailys) {
                final String dailyKey = key; // Create effectively final copy
                if (Shiny.getInstance().getQuestManager().daily.stream().noneMatch(e -> e.getName().equals(dailyKey))) {
                    continue;
                }
                this.dailys.add(Shiny.getInstance().getQuestManager().daily.stream().filter(e -> e.getName().equals(dailyKey)).findFirst().get());
            }
        }
        this.pendingDailys = new HashMap<Daily, Integer>();
        if (pendingDailys != null && !pendingDailys.getKeys(false).isEmpty()) {
            for (final String key : pendingDailys.getKeys(false)) {
                final String pendingDailyKey = key; // Create effectively final copy
                if (Shiny.getInstance().getQuestManager().daily.stream().noneMatch(e -> e.getName().equals(pendingDailyKey))) {
                    continue;
                }
                this.pendingDailys.put(Shiny.getInstance().getQuestManager().daily.stream().filter(e -> e.getName().equals(pendingDailyKey)).findFirst().get(), pendingDailys.getInt(key + ".amount"));
            }
        }
    }

    public void addXP(final int xp) {
        if (this.xp + xp >= 100) {
            this.xp = this.xp + xp - 100;
            ++this.level;
            Shiny.getInstance().getBattlepassManager().announceLevel(Bukkit.getPlayer(this.player.getUniqueId()), this.level);
        }
        else {
            this.xp += xp;
        }
    }

    public OfflinePlayer getPlayer() {
        return this.player;
    }

    public int getXp() {
        return this.xp;
    }

    public int getLevel() {
        return this.level;
    }

    public List<Quest> getQuests() {
        return this.quests;
    }

    public List<Daily> getDailys() {
        return this.dailys;
    }

    public Map<Daily, Integer> getPendingDailys() {
        return this.pendingDailys;
    }

    public Map<Quest, Integer> getPendingQuests() {
        return this.pendingQuests;
    }

    public List<String> getRewards() {
        return this.rewards;
    }

    public void setPlayer(final OfflinePlayer player) {
        this.player = player;
    }

    public void setXp(final int xp) {
        this.xp = xp;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public void setQuests(final List<Quest> quests) {
        this.quests = quests;
    }

    public void setDailys(final List<Daily> dailys) {
        this.dailys = dailys;
    }

    public void setPendingDailys(final Map<Daily, Integer> pendingDailys) {
        this.pendingDailys = pendingDailys;
    }

    public void setPendingQuests(final Map<Quest, Integer> pendingQuests) {
        this.pendingQuests = pendingQuests;
    }

    public void setRewards(final List<String> rewards) {
        this.rewards = rewards;
    }
}