package me.MaxBans.SkyWars.Managers;

import me.MaxBans.SkyWars.SkyWarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.MaxBans.SkyWars.Managers.PlayerManager.alivePlayers;

public class SpectatorManager implements Listener {

    private ConfigManager configManager = new ConfigManager();



    @EventHandler
    public void onRightClick(PlayerInteractEvent e){
        if(PlayerManager.deadPlayers.contains(e.getPlayer().getUniqueId())|| PlayerManager.spectators.contains(e.getPlayer().getUniqueId())) {
            if(e.getPlayer().getItemInHand() == null || e.getPlayer().getItemInHand().getType().equals(Material.AIR)) return;

            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals("§bTeleport to player")) {
                    e.getPlayer().openInventory(getTrackingInventory(e.getPlayer()));
                }else if(e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals("§cReturn to lobby!")){
                    String server = "Lobby";
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);
                    try {
                        out.writeUTF("Connect");
                        out.writeUTF(server);
                    } catch (Exception es) {
                        es.printStackTrace();
                    }
                    e.getPlayer().sendMessage("§8[§3Server§8]§7 Connecting you to§b " + server + "...");
                    e.getPlayer().sendPluginMessage(SkyWarsPlugin.getInstance(), "BungeeCord", b.toByteArray());
                }else if(e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals("§e§lFind new Arena") && e.getPlayer().getItemInHand().getType().equals(Material.PAPER)){
                        GameManager.autoJoin(e.getPlayer());
                    }
                }
            }
        }


    @EventHandler
    public void onItemBreak(BlockBreakEvent e){
        if(PlayerManager.deadPlayers.contains(e.getPlayer().getUniqueId())|| PlayerManager.spectators.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCollect(PlayerPickupItemEvent e) {
       if(PlayerManager.deadPlayers.contains(e.getPlayer().getUniqueId()) || PlayerManager.spectators.contains(e.getPlayer().getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onOpenChest(InventoryOpenEvent e){
        InventoryHolder holder = e.getInventory().getHolder();

        if (holder instanceof Chest) {
            Chest chest = (Chest) holder;
            if(PlayerManager.deadPlayers.contains(e.getPlayer().getUniqueId())|| PlayerManager.spectators.contains(e.getPlayer().getUniqueId())){
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onItemPlace(BlockPlaceEvent e){
        if(PlayerManager.deadPlayers.contains(e.getPlayer().getUniqueId())|| PlayerManager.spectators.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e){
        if(PlayerManager.deadPlayers.contains(e.getPlayer().getUniqueId())|| PlayerManager.spectators.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e){
        if(PlayerManager.deadPlayers.contains(e.getDamager().getUniqueId())|| PlayerManager.spectators.contains(e.getDamager().getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void OnInteractAtEntity(PlayerInteractAtEntityEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent e) {
        Entity p = e.getEntity();
        if(PlayerManager.deadPlayers.contains(p.getUniqueId()) || PlayerManager.spectators.contains(p.getUniqueId())) {
            if (((p instanceof Player))) {
                if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    p.setFallDistance(0);
                    p.teleport(configManager.getSpectatorSpawn(VotingManager.getWinner().getCustomName()));
                    e.setCancelled(true);
                } else if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                    e.setCancelled(true);
                }
            }
            e.setCancelled(true);
        }
    }

    public static Inventory getTrackingInventory(Player p){
        Inventory inv = Bukkit.createInventory(null, 27, "§7Teleport to player");
        ItemStack players = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta playersMeta = players.getItemMeta();
        playersMeta.setDisplayName("§a§lPlayers");
        List<String> pLore = new ArrayList<>();
        pLore.add("§aPlayers alive: " +  alivePlayers.size());
        playersMeta.setLore(pLore);
        players.setItemMeta(playersMeta);

        inv.setItem(22, players);

        for(UUID uuid : alivePlayers){
            Player pl = Bukkit.getPlayer(uuid);

                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwner(pl.getName());
                meta.setDisplayName(pl.getName());
                List<String> lore = new ArrayList<>();
                lore.add("§7Kills: §b" + PlayerManager.getKills(uuid));
                meta.setLore(lore);

                skull.setItemMeta(meta);
                inv.addItem(skull);
            }


        return inv;

    }

}
