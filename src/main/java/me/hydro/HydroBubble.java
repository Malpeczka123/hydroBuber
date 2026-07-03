package me.hydro;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class HydroBubble extends JavaPlugin implements Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getItem() == null || e.getItem().getType() != Material.SLIME_BALL) return;

        Action a = e.getAction();
        if (a != Action.RIGHT_CLICK_AIR && a != Action.RIGHT_CLICK_BLOCK) return;

        e.setCancelled(true);

        UUID id = p.getUniqueId();
        long now = System.currentTimeMillis();

        if (cooldowns.containsKey(id) && cooldowns.get(id) > now) {
            long left = (cooldowns.get(id) - now) / 1000;
            p.sendMessage("§c✖ HYDRO BLOKADA §7" + left + "s");
            return;
        }

        cooldowns.put(id, now + 120_000);

        p.sendMessage("§b✦ HYDRO AKTYWOWANE!");
        p.playSound(p.getLocation(), Sound.BLOCK_GLASS_PLACE, 1f, 1f);

        createBubble(p.getLocation());
    }

    private void createBubble(Location loc) {
        Set<Block> blocks = new HashSet<>();

        int r = 2;

        for (int x = -r; x <= r; x++) {
            for (int y = 0; y <= r; y++) {
                for (int z = -r; z <= r; z++) {

                    if (x*x + y*y + z*z <= r*r) {
                        Block b = loc.clone().add(x, y, z).getBlock();

                        if (b.getType() == Material.AIR) {
                            b.setType(Material.LIGHT_BLUE_STAINED_GLASS);
                            blocks.add(b);
                        }
                    }
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block b : blocks) {
                    if (b.getType() == Material.LIGHT_BLUE_STAINED_GLASS) {
                        b.setType(Material.AIR);
                    }
                }
            }
        }.runTaskLater(this, 20 * 15);
    }
}
