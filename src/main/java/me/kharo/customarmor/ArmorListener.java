package me.kharo.customarmor;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ArmorListener implements Listener {

    private boolean isNamed(ItemStack item, String name) {
        if (item == null) return false;
        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return false;
        return ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase(name);
    }

    private boolean isFlightBoots(ItemStack item) {
        return item != null && item.getType() == Material.LEATHER_BOOTS && isNamed(item, "Flight Boots");
    }

    private boolean isJumpLeggings(ItemStack item) {
        return item != null && item.getType() == Material.LEATHER_LEGGINGS && isNamed(item, "Jump Leggings");
    }

    private boolean isSpeedChestplate(ItemStack item) {
        return item != null && item.getType() == Material.LEATHER_CHESTPLATE && isNamed(item, "Speed Chestplate");
    }

    private boolean isFireBoots(ItemStack item) {
        return item != null && item.getType() == Material.LEATHER_BOOTS && isNamed(item, "Fire Boots");
    }

    private boolean isStrengthChestplate(ItemStack item) {
        return item != null && item.getType() == Material.LEATHER_CHESTPLATE && isNamed(item, "Strength Chestplate");
    }

    private boolean isWaterHelmet(ItemStack item) {
        return item != null && item.getType() == Material.LEATHER_HELMET && isNamed(item, "Water Breathing Helmet");
    }

    private boolean isMace(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != Material.IRON_AXE) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return false;

        return ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase("Mace");
    }

    // Wind Charge cooldowns
    private Map<UUID, Long> windCooldown = new HashMap<>();

    private boolean isWindCharge(ItemStack item) {
        if (item == null) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return false;

        return ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase("Wind Charge");
    }

    private void updateAbilities(Player p) {
        ItemStack boots = p.getInventory().getBoots();
        ItemStack legs = p.getInventory().getLeggings();
        ItemStack chest = p.getInventory().getChestplate();

        // Flight boots
        if (isFlightBoots(boots)) {
            p.setAllowFlight(true);
        } else {
            if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
                p.setAllowFlight(false);
                p.setFlying(false);
            }
        }

        // Jump leggings
        if (isJumpLeggings(legs)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 40, 2, true, false));
        }

        // Speed chestplate
        if (isSpeedChestplate(chest)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 2, true, false));
        }

        // Fire boots (fire resistance)
        if (isFireBoots(boots)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40, 0, true, false));
        }

        // Strength chestplate (increased damage)
        if (isStrengthChestplate(chest)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 1, true, false));
        }

        // Water breathing helmet
        ItemStack helmet = p.getInventory().getHelmet();
        if (isWaterHelmet(helmet)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 40, 0, true, false));
        }

        // --- new armor sets ------------------------------------------------
        boolean hasObsidianHelm = helmet != null && isNamed(helmet, "Obsidian Shield Helmet");
        boolean hasObsidianChest = chest != null && isNamed(chest, "Obsidian Shield Chestplate");
        boolean hasObsidianLegs = legs != null && isNamed(legs, "Obsidian Shield Leggings");
        boolean hasObsidianBoots = boots != null && isNamed(boots, "Obsidian Shield Boots");

        // fire resistance on any piece
        if (hasObsidianHelm || hasObsidianChest || hasObsidianLegs || hasObsidianBoots) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40, 0, true, false));
        }

        // haste armor
        boolean hasHasteHelm = helmet != null && isNamed(helmet, "Haste Helmet");
        boolean hasHasteChest = chest != null && isNamed(chest, "Haste Chestplate");
        boolean hasHasteLegs = legs != null && isNamed(legs, "Haste Leggings");
        boolean hasHasteBoots = boots != null && isNamed(boots, "Haste Boots");

        if (hasHasteHelm || hasHasteChest || hasHasteLegs || hasHasteBoots) {
            int amp = 0;
            if (hasHasteHelm && hasHasteChest && hasHasteLegs && hasHasteBoots) {
                amp = 1; // Haste II full set
            }
            p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 40, amp, true, false));
        }

        // mafia armor
        boolean hasMafiaHelm = helmet != null && isNamed(helmet, "Mafia Helmet");
        boolean hasMafiaChest = chest != null && isNamed(chest, "Mafia Chestplate");
        boolean hasMafiaLegs = legs != null && isNamed(legs, "Mafia Leggings");
        boolean hasMafiaBoots = boots != null && isNamed(boots, "Mafia Boots");

        if (hasMafiaHelm || hasMafiaChest || hasMafiaLegs || hasMafiaBoots) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0, true, false));
            if (hasMafiaHelm && hasMafiaChest && hasMafiaLegs && hasMafiaBoots) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, true, false));
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        updateAbilities(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();

        CustomArmorAbilities.getInstance().getServer().getScheduler().runTaskLater(
                CustomArmorAbilities.getInstance(),
                () -> updateAbilities(p),
                1L
        );
    }

    // DOUBLE JUMP
    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();

        if (p.getGameMode() != GameMode.SURVIVAL && p.getGameMode() != GameMode.ADVENTURE) return;

        // Only allow double jump if wearing Flight Boots
        if (!isFlightBoots(p.getInventory().getBoots())) return;

        e.setCancelled(true);
        p.setAllowFlight(false);
        p.setFlying(false);

        Vector vel = p.getLocation().getDirection().multiply(0.1);
        vel.setY(1.0);
        p.setVelocity(vel);
    }

    // Re-enable double jump when touching ground and update other abilities
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (p.getGameMode() != GameMode.SURVIVAL && p.getGameMode() != GameMode.ADVENTURE) return;

        // Track fall distance for mace smash
        if (p.isOnGround()) {
            p.setMetadata("lastFall", new FixedMetadataValue(
                    CustomArmorAbilities.getInstance(), p.getFallDistance()));
        }

        if (p.isOnGround() && isFlightBoots(p.getInventory().getBoots())) {
            p.setAllowFlight(true);
        }

        updateAbilities(p);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (!isWindCharge(item)) return;

        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        long now = System.currentTimeMillis();
        long last = windCooldown.getOrDefault(p.getUniqueId(), 0L);

        if (now - last < 2000) { // 2 second cooldown
            p.sendMessage(ChatColor.RED + "Wind Charge is recharging!");
            return;
        }

        windCooldown.put(p.getUniqueId(), now);

        Snowball proj = p.launchProjectile(Snowball.class);
        proj.setVelocity(p.getLocation().getDirection().multiply(2));
        proj.setCustomName("WindChargeProjectile");

        // Launch the player upward slightly (dash upward)
        p.setVelocity(p.getVelocity().add(new Vector(0, 0.8, 0)));
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Snowball)) return;
        Snowball s = (Snowball) e.getEntity();

        if (!"WindChargeProjectile".equals(s.getCustomName())) return;

        Location loc = s.getLocation();

        // AOE knockback
        for (Entity nearby : loc.getWorld().getNearbyEntities(loc, 4, 4, 4)) {
            if (nearby instanceof LivingEntity) {
                LivingEntity le = (LivingEntity) nearby;

                Vector knock = le.getLocation().toVector()
                        .subtract(loc.toVector())
                        .normalize().multiply(1.5);

                knock.setY(0.8);
                le.setVelocity(knock);
            }
        }

        // Visual & sound effect
        loc.getWorld().playEffect(loc, Effect.EXPLOSION_LARGE, 0);
        loc.getWorld().playSound(loc, Sound.ENTITY_ENDERDRAGON_FLAP, 1f, 1.2f);
    }

    @EventHandler
    public void onHit(org.bukkit.event.entity.EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;

        Player p = (Player) e.getDamager();
        ItemStack weapon = p.getInventory().getItemInMainHand();

        if (!isMace(weapon)) return;

        // Base mace damage boost
        double damage = e.getDamage() + 6;

        // Fall damage multiplier
        double fall = 0;
        if (p.hasMetadata("lastFall")) {
            fall = p.getMetadata("lastFall").get(0).asDouble();
        }

        // If player fell before hitting → apply smash bonus
        if (fall > 2) {
            damage += fall * 1.5;

            // Smash AOE knockback
            Location loc = e.getEntity().getLocation();
            for (Entity nearby : loc.getWorld().getNearbyEntities(loc, 3, 3, 3)) {
                if (nearby instanceof LivingEntity && nearby != p) {
                    LivingEntity le = (LivingEntity) nearby;
                    Vector knock = le.getLocation().toVector()
                            .subtract(p.getLocation().toVector())
                            .normalize().multiply(1.2);
                    knock.setY(0.6);
                    le.setVelocity(knock);
                }
            }
        }

        e.setDamage(damage);
    }

    // reduce explosion damage when wearing a full Obsidian Shield set
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        ItemStack helm = player.getInventory().getHelmet();
        ItemStack chest = player.getInventory().getChestplate();
        ItemStack legs = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        if (helm != null && chest != null && legs != null && boots != null &&
            isNamed(helm, "Obsidian Shield Helmet") &&
            isNamed(chest, "Obsidian Shield Chestplate") &&
            isNamed(legs, "Obsidian Shield Leggings") &&
            isNamed(boots, "Obsidian Shield Boots")) {

            EntityDamageEvent.DamageCause cause = e.getCause();
            if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
                cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                e.setDamage(e.getDamage() * 0.5);
            }
        }
    }
}
