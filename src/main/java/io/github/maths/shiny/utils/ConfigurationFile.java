package io.github.maths.shiny.utils;

import io.github.maths.shiny.utils.chat.CC;
import org.bukkit.plugin.java.*;
import org.bukkit.configuration.file.*;
import java.io.*;
import io.github.maths.shiny.utils.chat.*;
import java.util.*;
import java.util.stream.*;
import org.bukkit.configuration.*;

public class ConfigurationFile extends YamlConfiguration
{
    private File file;
    private JavaPlugin plugin;
    private FileConfiguration configuration;
    private String name;
    
    public ConfigurationFile(final JavaPlugin plugin, final String name) {
        this.file = new File(plugin.getDataFolder(), name);
        this.plugin = plugin;
        this.name = name;
        if (!this.file.exists()) {
            plugin.saveResource(name, false);
        }
        try {
            this.load(this.file);
        }
        catch (IOException | InvalidConfigurationException ex2) {
            final Exception ex = null;
            final Exception e = ex;
            e.printStackTrace();
        }
    }
    
    public void load() {
        this.file = new File(this.plugin.getDataFolder(), this.name);
        if (!this.file.exists()) {
            this.plugin.saveResource(this.name, false);
        }
        try {
            this.load(this.file);
        }
        catch (IOException | InvalidConfigurationException ex2) {
            final Exception ex = null;
            final Exception e = ex;
            e.printStackTrace();
        }
    }
    
    public void save() {
        try {
            this.save(this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public int getInt(final String path) {
        return super.getInt(path, 0);
    }
    
    public double getDouble(final String path) {
        return super.getDouble(path, 0.0);
    }
    
    public boolean getBoolean(final String path) {
        return super.getBoolean(path, false);
    }
    
    public String getString(final String path) {
        return CC.translate(super.getString(path, "String at path '" + path + "' not found.")).replace("|", "\u2503");
    }
    
    public List<String> getStringList(final String path) {
        return (List<String>)super.getStringList(path).stream().map(CC::translate).collect(Collectors.toList());
    }
    
    public List<String> getStringList(final String path, final boolean check) {
        if (!super.contains(path)) {
            return null;
        }
        return (List<String>)super.getStringList(path).stream().map(CC::translate).collect(Collectors.toList());
    }
    
    public FileConfiguration getConfig() {
        return (FileConfiguration)this;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public JavaPlugin getPlugin() {
        return this.plugin;
    }
    
    public FileConfiguration getConfiguration() {
        return this.configuration;
    }
    
    public String getName() {
        return this.name;
    }
}
