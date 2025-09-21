package io.github.maths.shiny.managers;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.events.ChatReactEvent;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.JavaUtil;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.configuration.*;
import org.bukkit.plugin.*;

import java.util.stream.*;
import org.bukkit.scheduler.*;
import org.bukkit.event.player.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.event.*;
import java.util.*;

public class ChatReactionManager implements Listener
{
    private ConfigurationFile config;
    private ConfigurationSection reactions;
    private long started;
    private String active;
    private int last;
    private String answer;

    public ChatReactionManager() {
        this.active = "";
        this.last = -1;
        this.config = new ConfigurationFile(Shiny.getInstance(), "chatreaction.yml");
        this.reactions = this.config.getConfigurationSection("Chats");

        // Validar configuración antes de inicializar
        if (!validateConfig()) {
            CC.log("&cChatReaction config is invalid! Please check your configuration.");
            return;
        }

        if (this.config.getBoolean("Enabled")) {
            Shiny.getInstance().getServer().getPluginManager().registerEvents((Listener)this, (Plugin)Shiny.getInstance());
            Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)Shiny.getInstance(), this::sendReaction, this.config.getInt("Between-Time") * 20L * 60L, this.config.getInt("Between-Time") * 20L * 60L);
        }
    }

    private boolean validateConfig() {
        if (reactions == null || reactions.getKeys(false).isEmpty()) {
            CC.log("&cNo chat reactions found in config!");
            return false;
        }

        // Verificar que existan las secciones de mensajes
        if (!config.contains("Message.Question") || !config.contains("Message.Scramble") ||
                !config.contains("Message.Win") || !config.contains("Message.End")) {
            CC.log("&cMissing required message sections in chatreaction config!");
            return false;
        }

        return true;
    }

    public void reload() {
        this.config = new ConfigurationFile(Shiny.getInstance(), "chatreaction.yml");
        this.reactions = this.config.getConfigurationSection("Chats");
        if (!validateConfig()) {
            CC.log("&cFailed to reload ChatReaction config - invalid configuration!");
        }
    }

    public void sendReaction() {
        if (!this.active.isEmpty()) {
            CC.log("&cAn error occurred trying to create a new Reaction, there is another Reaction in progress.");
            return;
        }

        if (reactions == null || reactions.getKeys(false).isEmpty()) {
            CC.log("&cNo chat reactions available!");
            return;
        }

        Random random = new Random();
        int i;
        Set<String> keys = reactions.getKeys(false);
        List<String> keysList = new ArrayList<>(keys);

        for (i = random.nextInt(keysList.size()); i == this.last && keysList.size() > 1; i = random.nextInt(keysList.size())) {}

        final String reaction = keysList.get(i);
        final String reactionType = reactions.getString(reaction + ".Type");
        final String reactionMessage = reactions.getString(reaction + ".Message");
        final String reactionAnswer = reactions.getString(reaction + ".Answer");

        // Validar que todos los valores necesarios existen
        if (reactionType == null || reactionMessage == null || reactionAnswer == null) {
            CC.log("&cInvalid reaction configuration for: " + reaction);
            return;
        }

        final boolean question = reactionType.equalsIgnoreCase("Question");
        this.answer = reactionAnswer;

        if (question) {
            List<String> questionMessages = config.getStringList("Message.Question");
            if (questionMessages != null && !questionMessages.isEmpty()) {
                this.sendMessage(questionMessages.stream()
                        .map(e -> e != null ? e.replace("%question%", reactionMessage) : "")
                        .filter(e -> !e.isEmpty())
                        .collect(Collectors.toList()));
            }
        }
        else {
            List<String> scrambleMessages = config.getStringList("Message.Scramble");
            if (scrambleMessages != null && !scrambleMessages.isEmpty()) {
                this.sendMessage(scrambleMessages.stream()
                        .map(e -> e != null ? e.replace("%word%", this.scramble(reactionMessage)) : "")
                        .filter(e -> !e.isEmpty())
                        .collect(Collectors.toList()));
            }
        }

        this.active = reaction;
        this.last = i;
        this.started = System.currentTimeMillis();

        new BukkitRunnable() {
            public void run() {
                if (!ChatReactionManager.this.active.isEmpty()) {
                    List<String> endMessages = ChatReactionManager.this.config.getStringList("Message.End");
                    if (endMessages != null && !endMessages.isEmpty() && ChatReactionManager.this.answer != null) {
                        ChatReactionManager.this.sendMessage(endMessages.stream()
                                .map(e -> e != null ? e.replace("%word%", ChatReactionManager.this.answer) : "")
                                .filter(e -> !e.isEmpty())
                                .collect(Collectors.toList()));
                    }
                    ChatReactionManager.this.active = "";
                    ChatReactionManager.this.answer = null;
                }
            }
        }.runTaskLater((Plugin)Shiny.getInstance(), this.config.getInt("Chats-Time") * 20L);
    }

    public void sendReaction(final String reaction) {
        if (!this.active.isEmpty()) {
            CC.log("&cAn error occurred trying to create a new Reaction, there is another Reaction in progress.");
            return;
        }

        if (reactions == null || !reactions.contains(reaction)) {
            CC.log("&cReaction not found: " + reaction);
            return;
        }

        final String reactionType = reactions.getString(reaction + ".Type");
        final String reactionMessage = reactions.getString(reaction + ".Message");
        final String reactionAnswer = reactions.getString(reaction + ".Answer");

        // Validar que todos los valores necesarios existen
        if (reactionType == null || reactionMessage == null || reactionAnswer == null) {
            CC.log("&cInvalid reaction configuration for: " + reaction);
            return;
        }

        final boolean question = reactionType.equalsIgnoreCase("Question");
        this.answer = reactionAnswer;

        if (question) {
            List<String> questionMessages = config.getStringList("Message.Question");
            if (questionMessages != null && !questionMessages.isEmpty()) {
                this.sendMessage(questionMessages.stream()
                        .map(e -> e != null ? e.replace("%question%", reactionMessage) : "")
                        .filter(e -> !e.isEmpty())
                        .collect(Collectors.toList()));
            }
        }
        else {
            List<String> scrambleMessages = config.getStringList("Message.Scramble");
            if (scrambleMessages != null && !scrambleMessages.isEmpty()) {
                this.sendMessage(scrambleMessages.stream()
                        .map(e -> e != null ? e.replace("%word%", this.scramble(reactionMessage)) : "")
                        .filter(e -> !e.isEmpty())
                        .collect(Collectors.toList()));
            }
        }

        this.active = reaction;
        this.last = -1;
        this.started = System.currentTimeMillis();

        new BukkitRunnable() {
            public void run() {
                if (!ChatReactionManager.this.active.isEmpty()) {
                    List<String> endMessages = ChatReactionManager.this.config.getStringList("Message.End");
                    if (endMessages != null && !endMessages.isEmpty() && ChatReactionManager.this.answer != null) {
                        ChatReactionManager.this.sendMessage(endMessages.stream()
                                .map(e -> e != null ? e.replace("%word%", ChatReactionManager.this.answer) : "")
                                .filter(e -> !e.isEmpty())
                                .collect(Collectors.toList()));
                    }
                    ChatReactionManager.this.active = "";
                    ChatReactionManager.this.answer = null;
                }
            }
        }.runTaskLater((Plugin)Shiny.getInstance(), this.config.getInt("Chats-Time") * 20L);
    }

    public void stop() {
        String cancelMessage = this.config.getString("Message.Chat-Cancel");
        if (cancelMessage != null) {
            Bukkit.broadcastMessage(CC.translate(cancelMessage));
        }
        this.active = "";
        this.answer = null;
    }

    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (this.active.isEmpty() || this.answer == null) {
            return;
        }

        if (event.getMessage().equals(this.answer)) {
            String playerWinMessage = this.config.getString("Message.Player-Win");
            if (playerWinMessage != null && !playerWinMessage.isEmpty()) {
                player.sendMessage(CC.translate(playerWinMessage));
            }

            if (this.config.getBoolean("Message.Win-Message")) {
                List<String> winMessages = this.config.getStringList("Message.Win");
                if (winMessages != null && !winMessages.isEmpty()) {
                    this.sendMessage(winMessages.stream()
                            .map(e -> {
                                if (e != null) {
                                    return e.replace("%player%", player.getName())
                                            .replace("%word%", this.answer)
                                            .replace("%time%", JavaUtil.formatDurationLongMiliseconds(System.currentTimeMillis() - this.started));
                                }
                                return "";
                            })
                            .filter(e -> !e.isEmpty())
                            .collect(Collectors.toList()));
                }
            }

            new BukkitRunnable() {
                public void run() {
                    String reward = ChatReactionManager.this.reactions.getString(ChatReactionManager.this.active + ".Reward");
                    if (reward != null) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.replace("%player%", player.getName()));
                    }
                    ChatReactionManager.this.active = "";
                }
            }.runTask((Plugin)Shiny.getInstance());

            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
            Bukkit.getServer().getPluginManager().callEvent(new ChatReactEvent(player));
            this.answer = null;
        }
    }

    public String scramble(final String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }

        final char[] chars = word.toCharArray();
        final Random random = new Random();
        for (int i = 0; i < chars.length; ++i) {
            final int index = random.nextInt(chars.length);
            final char temp = chars[i];
            chars[i] = chars[index];
            chars[index] = temp;
        }
        return new String(chars);
    }

    public void sendMessage(final List<String> message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        for (final Player player : Bukkit.getOnlinePlayers()) {
            for (final String msg : message) {
                if (msg != null) {
                    player.sendMessage(CC.translate(msg));
                }
            }
        }
    }

    public ConfigurationFile getConfig() {
        return this.config;
    }

    public ConfigurationSection getReactions() {
        return this.reactions;
    }

    public String getActive() {
        return this.active;
    }
}