package io.github.maths.shiny.managers.battlepass;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.ConfigurationFile;
import org.bukkit.configuration.*;
import java.util.*;

public class QuestManager
{
    private ConfigurationFile config;
    public List<Quest> quests;
    public List<Daily> daily;
    
    public QuestManager() {
        this.config = Shiny.getInstance().getQuestConfig();
        final ConfigurationSection section = this.config.getConfigurationSection("Quests");
        this.quests = new ArrayList<Quest>();
        for (final String key : section.getKeys(false)) {
            this.quests.add(new Quest(MissionTypes.valueOf(section.getString(key + ".Type")), section.getInt(key + ".XP"), section.getInt(key + ".Day"), section.getInt(key + ".Amount"), key));
        }
        final ConfigurationSection sec = this.config.getConfigurationSection("Daily");
        this.daily = new ArrayList<Daily>();
        for (final String key2 : sec.getKeys(false)) {
            if (sec.getInt(key2 + ".Day") == Shiny.getInstance().getBattlepassManager().getDay()) {
                this.daily.add(new Daily(MissionTypes.valueOf(sec.getString(key2 + ".Type")), sec.getInt(key2 + ".XP"), sec.getInt(key2 + ".Day"), sec.getInt(key2 + ".Amount"), key2));
            }
        }
    }
}
