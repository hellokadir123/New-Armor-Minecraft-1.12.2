package me.kharo.flightboots;

import org.bukkit.plugin.java.JavaPlugin;

public class FlightBoots extends JavaPlugin {

    private static FlightBoots instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new BootsListener(), this);
        getLogger().info("FlightBoots enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("FlightBoots disabled.");
    }

    public static FlightBoots getInstance() {
        return instance;
    }
}
