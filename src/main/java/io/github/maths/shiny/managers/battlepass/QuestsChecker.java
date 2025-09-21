package io.github.maths.shiny.managers.battlepass;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.events.ChatReactEvent;
import io.github.maths.shiny.events.DailyEvent;
import io.github.maths.shiny.events.KothEvent;
import io.github.maths.shiny.events.RaidEvent;
import io.github.maths.shiny.utils.ConfigurationFile;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import org.bukkit.event.entity.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class QuestsChecker implements Listener
{
    private ConfigurationFile config;
    private QuestManager questManager;
    private Map<Player, Location> checkMove;
    
    public QuestsChecker() {
        this.config = Shiny.getInstance().getQuestConfig();
        this.checkMove = new HashMap<Player, Location>();
        Shiny.getInstance().getServer().getPluginManager().registerEvents((Listener)this, (Plugin) Shiny.getInstance());
        this.questManager = Shiny.getInstance().getQuestManager();
    }
    
    public boolean checkMove(final Player player, final Location loc) {
        if (this.checkMove.containsKey(player) && (this.checkMove.get(player).getBlockX() != loc.getBlockX() || this.checkMove.get(player).getBlockZ() != loc.getBlockZ())) {
            this.checkMove.put(player, loc);
            return true;
        }
        this.checkMove.put(player, loc);
        return false;
    }
    
    @EventHandler
    public void onKill(final PlayerDeathEvent event) {
        final Player killer = event.getEntity().getKiller();
        if (killer == null || !killer.isOnline() || !killer.isValid()) {
            return;
        }
        if (this.questManager.quests.stream().noneMatch(e -> e.getType().equals(MissionTypes.KillPlayer))) {
            return;
        }
        Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(killer);
        if (battlepass == null) {
            Shiny.getInstance().getBattlepassManager().register((OfflinePlayer)killer);
            battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(killer);
        }
        for (final Quest quest : this.questManager.quests) {
            if (!quest.getType().equals(MissionTypes.KillPlayer)) {
                continue;
            }
            if (!quest.isEnabled()) {
                continue;
            }
            if (battlepass.getQuests().stream().anyMatch(e -> e.getName().equals(quest.getName()))) {
                continue;
            }
            if (battlepass.getPendingQuests().get(quest) == null) {
                battlepass.getPendingQuests().put(quest, 1);
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else if (quest.getAmount() == 1) {
                Shiny.getInstance().getBattlepassManager().announceXP(killer, quest.getXp());
                battlepass.getPendingQuests().remove(quest);
                battlepass.getQuests().add(quest);
                battlepass.addXP(quest.getXp());
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else {
                int kills = battlepass.getPendingQuests().get(quest);
                if (++kills == quest.getAmount()) {
                    Shiny.getInstance().getBattlepassManager().announceXP(killer, quest.getXp());
                    battlepass.getPendingQuests().remove(quest);
                    battlepass.getQuests().add(quest);
                    battlepass.addXP(quest.getXp());
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else {
                    battlepass.getPendingQuests().put(quest, kills);
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
            }
        }
        for (final Daily daily : this.questManager.daily) {
            if (!daily.getType().equals(MissionTypes.KillPlayer)) {
                continue;
            }
            if (!daily.isEnabled()) {
                continue;
            }
            if (battlepass.getDailys().stream().anyMatch(e -> e.getName().equals(daily.getName()))) {
                continue;
            }
            if (battlepass.getPendingDailys().get(daily) == null) {
                battlepass.getPendingDailys().put(daily, 1);
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else if (daily.getAmount() == 1) {
                Shiny.getInstance().getBattlepassManager().announceXP(killer, daily.getXp());
                battlepass.getPendingDailys().remove(daily);
                battlepass.getDailys().add(daily);
                battlepass.addXP(daily.getXp());
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else {
                int kills = battlepass.getPendingDailys().get(daily);
                if (++kills == daily.getAmount()) {
                    Shiny.getInstance().getBattlepassManager().announceXP(killer, daily.getXp());
                    battlepass.getPendingDailys().remove(daily);
                    battlepass.getDailys().add(daily);
                    battlepass.addXP(daily.getXp());
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else {
                    battlepass.getPendingDailys().put(daily, kills);
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
            }
        }
    }
    
    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getZ() == event.getTo().getZ()) {
            return;
        }
        if (this.checkMove(event.getPlayer(), event.getTo())) {
            Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(event.getPlayer());
            if (battlepass == null) {
                Shiny.getInstance().getBattlepassManager().register((OfflinePlayer)event.getPlayer());
                battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(event.getPlayer());
                if (battlepass == null) {
                    return;
                }
            }
            for (final Quest quest : this.questManager.quests) {
                if (!quest.isEnabled()) {
                    continue;
                }
                if (!quest.getType().equals(MissionTypes.WalkDistance)) {
                    continue;
                }
                if (battlepass.getQuests().stream().anyMatch(e -> e.getName().equals(quest.getName()))) {
                    continue;
                }
                if (quest.getAmount() == 1) {
                    Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), quest.getXp());
                    battlepass.getPendingQuests().remove(quest);
                    battlepass.getQuests().add(quest);
                    battlepass.addXP(quest.getXp());
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else if (battlepass.getPendingQuests().get(quest) == null) {
                    battlepass.getPendingQuests().put(quest, 1);
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else {
                    int amount = battlepass.getPendingQuests().get(quest);
                    if (++amount >= quest.getAmount()) {
                        Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), quest.getXp());
                        battlepass.getPendingQuests().remove(quest);
                        battlepass.getQuests().add(quest);
                        battlepass.addXP(quest.getXp());
                        Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                    }
                    else {
                        battlepass.getPendingQuests().put(quest, amount);
                        Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                    }
                }
            }
            for (final Daily daily : this.questManager.daily) {
                if (!daily.getType().equals(MissionTypes.WalkDistance)) {
                    continue;
                }
                if (!daily.isEnabled()) {
                    continue;
                }
                if (battlepass.getDailys().stream().anyMatch(e -> e.getName().equals(daily.getName()))) {
                    continue;
                }
                if (battlepass.getPendingDailys().get(daily) == null) {
                    battlepass.getPendingDailys().put(daily, 1);
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else if (daily.getAmount() == 1) {
                    Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), daily.getXp());
                    battlepass.getPendingDailys().remove(daily);
                    battlepass.getDailys().add(daily);
                    battlepass.addXP(daily.getXp());
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else {
                    int amount = battlepass.getPendingDailys().get(daily);
                    if (++amount == daily.getAmount()) {
                        Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), daily.getXp());
                        battlepass.getPendingDailys().remove(daily);
                        battlepass.getDailys().add(daily);
                        battlepass.addXP(daily.getXp());
                        Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                    }
                    else {
                        battlepass.getPendingDailys().put(daily, amount);
                        Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onKoth(final KothEvent event) {
        final Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(event.getPlayer());
        if (battlepass == null) {
            Shiny.getInstance().getBattlepassManager().register((OfflinePlayer)event.getPlayer());
        }
        for (final Quest quest : this.questManager.quests) {
            if (!quest.isEnabled()) {
                continue;
            }
            if (!quest.getType().equals(MissionTypes.Koth)) {
                continue;
            }
            if (battlepass.getQuests().stream().anyMatch(e -> e.getName().equals(quest.getName()))) {
                continue;
            }
            if (battlepass.getPendingQuests().get(quest) == null) {
                battlepass.getPendingQuests().put(quest, 1);
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else if (quest.getAmount() == 1) {
                Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), quest.getXp());
                battlepass.getPendingQuests().remove(quest);
                battlepass.getQuests().add(quest);
                battlepass.addXP(quest.getXp());
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else {
                int amount = battlepass.getPendingQuests().get(quest);
                if (++amount >= quest.getAmount()) {
                    Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), quest.getXp());
                    battlepass.getPendingQuests().remove(quest);
                    battlepass.getQuests().add(quest);
                    battlepass.addXP(quest.getXp());
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else {
                    battlepass.getPendingQuests().put(quest, amount);
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
            }
        }
        for (final Daily daily : this.questManager.daily) {
            if (!daily.getType().equals(MissionTypes.Koth)) {
                continue;
            }
            if (!daily.isEnabled()) {
                continue;
            }
            if (battlepass.getDailys().stream().anyMatch(e -> e.getName().equals(daily.getName()))) {
                continue;
            }
            if (battlepass.getPendingDailys().get(daily) == null) {
                battlepass.getPendingDailys().put(daily, 1);
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else if (daily.getAmount() == 1) {
                Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), daily.getXp());
                battlepass.getPendingDailys().remove(daily);
                battlepass.getDailys().add(daily);
                battlepass.addXP(daily.getXp());
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else {
                int amount = battlepass.getPendingDailys().get(daily);
                if (++amount == daily.getAmount()) {
                    Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), daily.getXp());
                    battlepass.getPendingDailys().remove(daily);
                    battlepass.getDailys().add(daily);
                    battlepass.addXP(daily.getXp());
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else {
                    battlepass.getPendingDailys().put(daily, amount);
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
            }
        }
    }
    
    @EventHandler
    public void onRaid(final RaidEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        for (final Quest quest : this.questManager.quests) {
            if (!quest.isEnabled()) {
                continue;
            }
            if (!quest.getType().equals(MissionTypes.RaidFaction)) {
                continue;
            }
            final Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(event.getPlayer());
            if (battlepass == null) {
                Shiny.getInstance().getBattlepassManager().register((OfflinePlayer)event.getPlayer());
            }
            if (battlepass.getQuests().stream().anyMatch(e -> e.getName().equals(quest.getName()))) {
                continue;
            }
            if (battlepass.getPendingQuests().get(quest) == null) {
                battlepass.getPendingQuests().put(quest, 1);
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else if (quest.getAmount() == 1) {
                Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), quest.getXp());
                battlepass.getPendingQuests().remove(quest);
                battlepass.getQuests().add(quest);
                battlepass.addXP(quest.getXp());
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else {
                int amount = battlepass.getPendingQuests().get(quest);
                if (++amount >= quest.getAmount()) {
                    Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), quest.getXp());
                    battlepass.getPendingQuests().remove(quest);
                    battlepass.getQuests().add(quest);
                    battlepass.addXP(quest.getXp());
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else {
                    battlepass.getPendingQuests().put(quest, amount);
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
            }
        }
        for (final Daily daily : this.questManager.daily) {
            if (!daily.getType().equals(MissionTypes.RaidFaction)) {
                continue;
            }
            if (!daily.isEnabled()) {
                continue;
            }
            final Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(event.getPlayer());
            if (battlepass.getDailys().stream().anyMatch(e -> e.getName().equals(daily.getName()))) {
                continue;
            }
            if (battlepass.getPendingDailys().get(daily) == null) {
                battlepass.getPendingDailys().put(daily, 1);
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else if (daily.getAmount() == 1) {
                Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), daily.getXp());
                battlepass.getPendingDailys().remove(daily);
                battlepass.getDailys().add(daily);
                battlepass.addXP(daily.getXp());
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else {
                int amount = battlepass.getPendingDailys().get(daily);
                if (++amount == daily.getAmount()) {
                    Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), daily.getXp());
                    battlepass.getPendingDailys().remove(daily);
                    battlepass.getDailys().add(daily);
                    battlepass.addXP(daily.getXp());
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else {
                    battlepass.getPendingDailys().put(daily, amount);
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
            }
        }
    }
    
    @EventHandler
    public void onDaily(final DailyEvent event) {
        for (final Quest quest : this.questManager.quests) {
            if (!quest.isEnabled()) {
                continue;
            }
            if (!quest.getType().equals(MissionTypes.DailyClaim)) {
                continue;
            }
            final Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(event.getPlayer());
            if (battlepass == null) {
                Shiny.getInstance().getBattlepassManager().register((OfflinePlayer)event.getPlayer());
            }
            if (battlepass.getQuests().stream().anyMatch(e -> e.getName().equals(quest.getName()))) {
                continue;
            }
            if (battlepass.getPendingQuests().get(quest) == null) {
                battlepass.getPendingQuests().put(quest, 1);
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else if (quest.getAmount() == 1) {
                Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), quest.getXp());
                battlepass.getPendingQuests().remove(quest);
                battlepass.getQuests().add(quest);
                battlepass.addXP(quest.getXp());
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else {
                int amount = battlepass.getPendingQuests().get(quest);
                if (++amount >= quest.getAmount()) {
                    Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), quest.getXp());
                    battlepass.getPendingQuests().remove(quest);
                    battlepass.getQuests().add(quest);
                    battlepass.addXP(quest.getXp());
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else {
                    battlepass.getPendingQuests().put(quest, amount);
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
            }
        }
        for (final Daily daily : this.questManager.daily) {
            if (!daily.getType().equals(MissionTypes.DailyClaim)) {
                continue;
            }
            if (!daily.isEnabled()) {
                continue;
            }
            final Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(event.getPlayer());
            if (battlepass.getDailys().stream().anyMatch(e -> e.getName().equals(daily.getName()))) {
                continue;
            }
            if (battlepass.getPendingDailys().get(daily) == null) {
                battlepass.getPendingDailys().put(daily, 1);
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else if (daily.getAmount() == 1) {
                Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), daily.getXp());
                battlepass.getPendingDailys().remove(daily);
                battlepass.getDailys().add(daily);
                battlepass.addXP(daily.getXp());
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else {
                int amount = battlepass.getPendingDailys().get(daily);
                if (++amount == daily.getAmount()) {
                    Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), daily.getXp());
                    battlepass.getPendingDailys().remove(daily);
                    battlepass.getDailys().add(daily);
                    battlepass.addXP(daily.getXp());
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else {
                    battlepass.getPendingDailys().put(daily, amount);
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
            }
        }
    }
    
    @EventHandler
    public void onReact(final ChatReactEvent event) {
        for (final Quest quest : this.questManager.quests) {
            if (!quest.isEnabled()) {
                continue;
            }
            if (!quest.getType().equals(MissionTypes.ChatReaction)) {
                continue;
            }
            final Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(event.getPlayer());
            if (battlepass == null) {
                Shiny.getInstance().getBattlepassManager().register((OfflinePlayer)event.getPlayer());
            }
            if (battlepass.getQuests().stream().anyMatch(e -> e.getName().equals(quest.getName()))) {
                continue;
            }
            if (battlepass.getPendingQuests().get(quest) == null) {
                battlepass.getPendingQuests().put(quest, 1);
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else if (quest.getAmount() == 1) {
                Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), quest.getXp());
                battlepass.getPendingQuests().remove(quest);
                battlepass.getQuests().add(quest);
                battlepass.addXP(quest.getXp());
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else {
                int amount = battlepass.getPendingQuests().get(quest);
                if (++amount >= quest.getAmount()) {
                    Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), quest.getXp());
                    battlepass.getPendingQuests().remove(quest);
                    battlepass.getQuests().add(quest);
                    battlepass.addXP(quest.getXp());
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else {
                    battlepass.getPendingQuests().put(quest, amount);
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
            }
        }
        for (final Daily daily : this.questManager.daily) {
            if (!daily.getType().equals(MissionTypes.ChatReaction)) {
                continue;
            }
            if (!daily.isEnabled()) {
                continue;
            }
            final Battlepass battlepass = Shiny.getInstance().getBattlepassManager().battlepasses.get(event.getPlayer());
            if (battlepass.getDailys().stream().anyMatch(e -> e.getName().equals(daily.getName()))) {
                continue;
            }
            if (battlepass.getPendingDailys().get(daily) == null) {
                battlepass.getPendingDailys().put(daily, 1);
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else if (daily.getAmount() == 1) {
                Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), daily.getXp());
                battlepass.getPendingDailys().remove(daily);
                battlepass.getDailys().add(daily);
                battlepass.addXP(daily.getXp());
                Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
            }
            else {
                int amount = battlepass.getPendingDailys().get(daily);
                if (++amount == daily.getAmount()) {
                    Shiny.getInstance().getBattlepassManager().announceXP(event.getPlayer(), daily.getXp());
                    battlepass.getPendingDailys().remove(daily);
                    battlepass.getDailys().add(daily);
                    battlepass.addXP(daily.getXp());
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
                else {
                    battlepass.getPendingDailys().put(daily, amount);
                    Shiny.getInstance().getBattlepassManager().battlepasses.put(battlepass.getPlayer(), battlepass);
                }
            }
        }
    }
}
