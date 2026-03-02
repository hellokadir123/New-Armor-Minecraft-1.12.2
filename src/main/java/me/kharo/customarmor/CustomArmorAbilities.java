package me.kharo.customarmor;

import org.bukkit.plugin.java.JavaPlugin;

public class CustomArmorAbilities extends JavaPlugin {

    private static CustomArmorAbilities instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new ArmorListener(), this);
        getLogger().info("CustomArmorAbilities enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("CustomArmorAbilities disabled.");
    }

    public static CustomArmorAbilities getInstance() {
        return instance;
    }
}
