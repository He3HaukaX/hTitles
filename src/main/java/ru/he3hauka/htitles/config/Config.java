package ru.he3hauka.htitles.config;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

@Getter
public class Config {
    public List<String> actions_help;
    public List<String> actions_permission;
    public List<String> actions_finish;
    public List<String> actions_max_size;
    public List<String> actions_confirm;
    public List<String> actions_confirm_null;
    public List<String> actions_confirm_expired;
    public List<String> actions_blocked_word;
    public int settings_priority;
    public int settings_max_size;
    public int settings_delay;
    public List<String> settings_blocked_words;
    private final JavaPlugin plugin;
    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        this.actions_help = config.getStringList("actions.help");
        this.actions_permission = config.getStringList("actions.permission");
        this.actions_finish = config.getStringList("actions.finish");
        this.actions_confirm = config.getStringList("actions.confirm");
        this.actions_confirm_expired = config.getStringList("actions.confirm-expired");
        this.actions_confirm_null = config.getStringList("actions.confirm-null");
        this.actions_max_size = config.getStringList("actions.max-size");
        this.actions_blocked_word = config.getStringList("actions.blocked-word");

        this.settings_priority = config.getInt("settings.priority", 100);
        this.settings_max_size = config.getInt("settings.max-size", 16);
        this.settings_delay = config.getInt("settings.delay", 10000);
        this.settings_blocked_words = config.getStringList("settings.blocked-words");
    }
}

