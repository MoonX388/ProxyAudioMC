package com.moonx.bukkit.core.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class BukkitConfiguration extends JavaPlugin {

    private FileConfiguration config;
    private FileConfiguration data;

    @Override
    public void onEnable() {
        // Muat config.yml secara otomatis
        saveDefaultConfig();
        config = getConfig();

        // Muat data.yml
        loadDataYml();

        // Contoh akses data dari config.yml
        boolean debugMode = config.getBoolean("settings.debug");
        String welcomeMsg = config.getString("messages.welcome");
        getLogger().info("Debug Mode: " + debugMode);
        getLogger().info("Welcome message: " + welcomeMsg);

        // Contoh akses data dari data.yml
        if (data != null && data.contains("serverStatus")) {
            String status = data.getString("serverStatus");
            getLogger().info("Server status: " + status);
        }
    }

    private void loadDataYml() {
        File dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            // Jika file belum ada, salin dari resources
            saveResource("data.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }
}