package io.github.maths.shiny.managers;

import io.github.maths.shiny.Shiny;
import io.github.maths.shiny.utils.BukkitUtils;
import io.github.maths.shiny.utils.ConfigurationFile;
import io.github.maths.shiny.utils.ItemBuilder;
import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.entity.*;

import java.util.*;

import org.bukkit.inventory.*;
import org.bukkit.event.inventory.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.inventory.meta.*;

public class EnderchestManager implements Listener
{
    private ConfigurationFile storage;
    private ConfigurationFile config;
    private Map<Player, Integer> rows;

    public EnderchestManager() {
        final Shiny plugin = Shiny.getInstance();
        this.storage = new ConfigurationFile(Shiny.getInstance(), "storage/enderchest.yml");
        this.config = plugin.getConfig();
        this.rows = new HashMap<Player, Integer>();
        final long intervalSave = 6000L;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> this.storage.save()), intervalSave, intervalSave);
        Shiny.getInstance().getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void inspectClick(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        if (event.getInventory().getName().endsWith(CC.translate("Enderchest")) && player.hasPermission("Shiny.enderchest.inspect")) {
            final int rows = 40;
            if (event.getInventory().equals(player.getInventory())) {
                return;
            }
            if (event.getSlot() >= rows) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void inspectClose(final InventoryCloseEvent event) {
        final Player ins = (Player)event.getPlayer();
        if (event.getInventory().getName().endsWith(CC.translate("Enderchest")) && ins.hasPermission("Shiny.enderchest.inspect")) {
            final String playerName = event.getInventory().getTitle().split(" ")[0].replace(CC.translate("&5"), "");
            final OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            final int rows = 40;

            // Variables finales para usar en el lambda
            final OfflinePlayer finalPlayer = player;
            final int finalRows = rows;

            Bukkit.getScheduler().runTaskAsynchronously(Shiny.getInstance(), () -> {
                ArrayList<String> items = new ArrayList<String>();
                for (int i = 0; i < finalRows; ++i) {
                    ItemStack item = event.getInventory().getItem(i);
                    items.add(BukkitUtils.serializeItemStack(item));
                }
                List<String> save = this.storage.getStringList("Enderchests." + finalPlayer.getUniqueId() + ".items");
                if (save.size() > finalRows) {
                    ArrayList<String> saved = new ArrayList<String>();
                    for (int j = finalRows; j < save.size(); ++j) {
                        saved.add(save.get(j));
                    }
                    this.storage.set("Enderchests." + finalPlayer.getUniqueId() + ".saved", saved);
                }
                this.storage.set("Enderchests." + finalPlayer.getUniqueId() + ".items", items);
            });
        }
    }

    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        if (event.getInventory().getName().equals(CC.translate("&5" + player.getName() + " Enderchest"))) {
            if (event.getClickedInventory() != null && event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                return;
            }
            final int rows = this.getRows(player);
            if (event.getInventory().equals(player.getInventory())) {
                return;
            }
            if (event.getSlot() >= rows) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        final Player player = (Player)event.getPlayer();
        if (event.getInventory().getName().equals(CC.translate("&5" + player.getName() + " Enderchest"))) {
            final int rows = this.rows.get(player);

            // Variables finales para usar en el lambda
            final Player finalPlayer = player;
            final int finalRows = rows;

            Bukkit.getScheduler().runTaskAsynchronously(this.storage.getPlugin(), () -> {
                ArrayList<String> items = new ArrayList<String>();
                for (int i = 0; i < finalRows; ++i) {
                    ItemStack item = event.getInventory().getItem(i);
                    items.add(BukkitUtils.serializeItemStack(item));
                }
                List<String> save = this.storage.getStringList("Enderchests." + finalPlayer.getUniqueId() + ".items");
                if (save.size() > finalRows) {
                    ArrayList<String> saved = new ArrayList<String>();
                    for (int j = finalRows; j < save.size(); ++j) {
                        saved.add(save.get(j));
                    }
                    this.storage.set("Enderchests." + finalPlayer.getUniqueId() + ".saved", saved);
                }
                this.storage.set("Enderchests." + finalPlayer.getUniqueId() + ".items", items);
            });
            this.rows.remove(player);
        }
    }

    public void open(final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 45, CC.translate("&5" + player.getName() + " Enderchest"));
        final int rows = this.getRows(player);
        if (this.storage.contains("Enderchests." + player.getUniqueId())) {
            if (player.getOpenInventory().getTitle().equals(CC.translate("&5" + player.getName() + " Enderchest"))) {
                player.getOpenInventory().close();
            }
            int j = 0;
            for (int i = 0; i < rows; ++i) {
                if (i >= this.storage.getStringList("Enderchests." + player.getUniqueId() + ".items").size()) {
                    if (!this.storage.contains("Enderchests." + player.getUniqueId() + ".saved")) {
                        break;
                    }
                    if (this.storage.getStringList("Enderchests." + player.getUniqueId() + ".saved").size() == j) {
                        break;
                    }
                    final ItemStack itemStack = BukkitUtils.deserializeItemStack(this.storage.getStringList("Enderchests." + player.getUniqueId() + ".saved").get(j));
                    ++j;
                    inventory.setItem(i, itemStack);
                }
                else {
                    final ItemStack item = BukkitUtils.deserializeItemStack(this.storage.getStringList("Enderchests." + player.getUniqueId() + ".items").get(i));
                    inventory.setItem(i, item);
                }
            }
        }
        if (9 > rows) {
            final ItemStack item2 = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(this.config.getString("Enderchest.Rows.1.Title")).setLore(this.config.getStringList("Enderchest.Rows.1.Description")).setData(this.config.getInt("Enderchest.Rows.1.Data")).build();
            for (int i = 0; i <= 8; ++i) {
                inventory.setItem(i, item2);
            }
        }
        if (13 > rows) {
            final ItemStack item2 = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(this.config.getString("Enderchest.Rows.2.Title")).setLore(this.config.getStringList("Enderchest.Rows.2.Description")).setData(this.config.getInt("Enderchest.Rows.2.Data")).build();
            for (int i = 9; i <= 12; ++i) {
                inventory.setItem(i, item2);
            }
        }
        if (18 > rows) {
            final ItemStack item2 = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(this.config.getString("Enderchest.Rows.3.Title")).setLore(this.config.getStringList("Enderchest.Rows.3.Description")).setData(this.config.getInt("Enderchest.Rows.3.Data")).build();
            for (int i = 13; i <= 17; ++i) {
                inventory.setItem(i, item2);
            }
        }
        if (27 > rows) {
            final ItemStack item2 = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(this.config.getString("Enderchest.Rows.4.Title")).setLore(this.config.getStringList("Enderchest.Rows.4.Description")).setData(this.config.getInt("Enderchest.Rows.4.Data")).build();
            for (int i = 18; i <= 26; ++i) {
                inventory.setItem(i, item2);
            }
        }
        if (36 > rows) {
            final ItemStack item2 = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(this.config.getString("Enderchest.Rows.5.Title")).setLore(this.config.getStringList("Enderchest.Rows.5.Description")).setData(this.config.getInt("Enderchest.Rows.5.Data")).build();
            for (int i = 27; i <= 35; ++i) {
                inventory.setItem(i, item2);
            }
        }
        if (40 > rows) {
            final ItemStack item2 = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(this.config.getString("Enderchest.Rows.6.Title")).setLore(this.config.getStringList("Enderchest.Rows.6.Description")).setData(this.config.getInt("Enderchest.Rows.6.Data")).build();
            for (int i = 36; i <= 39; ++i) {
                inventory.setItem(i, item2);
            }
        }
        final ItemStack item2 = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(this.config.getString("Enderchest.Rows.Disabled.Title")).setLore(this.config.getStringList("Enderchest.Rows.Disabled.Description")).setData(this.config.getInt("Enderchest.Rows.Disabled.Data")).build();
        for (int i = 40; i <= 44; ++i) {
            inventory.setItem(i, item2);
        }
        this.rows.put(player, rows);
        player.openInventory(inventory);
    }

    @EventHandler
    public void enderchestOpen(final InventoryOpenEvent event) {
        final Player player = (Player)event.getPlayer();
        if (event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
            event.setCancelled(true);
            this.open(player);
        }
    }

    public void inspect(final Player inspector, final OfflinePlayer player) {
        final Inventory inventory = Bukkit.createInventory(null, 45, CC.translate("&5" + player.getName() + " Enderchest"));
        final int rows = 40;
        if (this.storage.contains("Enderchests." + player.getUniqueId())) {
            int j = 0;
            int k = 0;
            for (int i = 0; i < rows; ++i) {
                if (i >= this.storage.getStringList("Enderchests." + player.getUniqueId() + ".items").size()) {
                    if (!this.storage.contains("Enderchests." + player.getUniqueId() + ".saved")) {
                        break;
                    }
                    if (j >= this.storage.getStringList("Enderchests." + player.getUniqueId() + ".saved").size()) {
                        if (k >= this.storage.getStringList("Enderchests." + player.getUniqueId() + ".saved").size()) {
                            break;
                        }
                        final ItemStack itemStack = BukkitUtils.deserializeItemStack(this.storage.getStringList("Enderchests." + player.getUniqueId() + ".saved").get(k));
                        inventory.setItem(i, itemStack);
                        ++k;
                    }
                    else {
                        final ItemStack itemStack = BukkitUtils.deserializeItemStack(this.storage.getStringList("Enderchests." + player.getUniqueId() + ".saved").get(j));
                        ++j;
                        inventory.setItem(i, itemStack);
                    }
                }
                else {
                    final ItemStack item = BukkitUtils.deserializeItemStack(this.storage.getStringList("Enderchests." + player.getUniqueId() + ".items").get(i));
                    inventory.setItem(i, item);
                }
            }
        }
        final ItemStack item2 = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(this.config.getString("Enderchest.Rows.Disabled.Title")).setLore(this.config.getStringList("Enderchest.Rows.Disabled.Description")).setData(this.config.getInt("Enderchest.Rows.Disabled.Data")).build();
        for (int l = 40; l <= 44; ++l) {
            inventory.setItem(l, item2);
        }
        inspector.openInventory(inventory);
    }

    public int getRows(final Player player) {
        player.recalculatePermissions();
        if (player.hasPermission("Shiny.enderchest.6")) {
            return 40;
        }
        if (player.hasPermission("Shiny.enderchest.5")) {
            return 36;
        }
        if (player.hasPermission("Shiny.enderchest.4")) {
            return 27;
        }
        if (player.hasPermission("Shiny.enderchest.3")) {
            return 18;
        }
        if (player.hasPermission("Shiny.enderchest.2")) {
            return 13;
        }
        if (player.hasPermission("Shiny.enderchest.1")) {
            return 9;
        }
        return 0;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!this.rows.containsKey(event.getWhoClicked())) {
            return;
        }
        if (event.getView().getTopInventory() != event.getClickedInventory()) {
            if (!event.isShiftClick() || event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            this.check(event, event.getCurrentItem());
        }
        else {
            if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
                return;
            }
            this.check(event, event.getCursor());
        }
    }

    @EventHandler
    private void onInv(final InventoryDragEvent event) {
        if (event.getView().getTopInventory() != event.getInventory()) {
            return;
        }
        if (!this.rows.containsKey(event.getWhoClicked())) {
            return;
        }
        if (event.getOldCursor() == null || event.getOldCursor().getType() == Material.AIR) {
            return;
        }
        this.check(event, event.getOldCursor());
    }

    private void check(final InventoryInteractEvent event, final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        final List<String> lore = meta.getLore();
        if (lore == null || lore.stream().noneMatch(line -> ChatColor.stripColor(line).equalsIgnoreCase("Not-Storable"))) {
            return;
        }
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
        event.getWhoClicked().sendMessage(CC.translate(this.config.getString("Enderchest.Not-Storable")));
    }

    public ConfigurationFile getStorage() {
        return this.storage;
    }
}