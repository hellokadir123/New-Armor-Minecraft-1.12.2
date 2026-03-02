package me.kharo.flightboots;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BootsListener implements Listener {

    private boolean isFlightBoots(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != Material.LEATHER_BOOTS) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return false;

        return ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase("Flight Boots");
    }

    private void updateFlight(Player p) {
        ItemStack boots = p.getInventory().getBoots();

        if (isFlightBoots(boots)) {
            p.setAllowFlight(true);
        } else {
            // Only disable flight if they are not in creative or spectator
            if (p.getGameMode().name().equals("SURVIVAL") || p.getGameMode().name().equals("ADVENTURE")) {
                p.setAllowFlight(false);
                p.setFlying(false);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        updateFlight(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player p = (Player) e.getWhoClicked();

        // Delay 1 tick so the armor actually updates
        FlightBoots.getInstance().getServer().getScheduler().runTaskLater(
                FlightBoots.getInstance(),
                () -> updateFlight(p),
                1L
        );
    }
}
