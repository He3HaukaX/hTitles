package ru.he3hauka.htitles;

import org.bukkit.plugin.java.JavaPlugin;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.RegisteredServiceProvider;
import ru.he3hauka.htitles.actions.ActionExecutor;
import ru.he3hauka.htitles.command.TitleHandler;
import ru.he3hauka.htitles.config.Config;
import ru.he3hauka.htitles.manager.TitleManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Config config = new Config(this);
        config.init();

        LuckPerms luckPerms = loadLuckPerms();
        TitleManager titleManager = new TitleManager(luckPerms);
        getCommand("htitles").setExecutor(new TitleHandler(titleManager, new ActionExecutor(), config));

        authorInfo();
    }

    private LuckPerms loadLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider =
                getServer().getServicesManager().getRegistration(LuckPerms.class);
        return provider != null ? provider.getProvider() : null;
    }

    public void authorInfo(){
        File file = new File(getDataFolder(), "info.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Files.copy(getResource("info.txt"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
