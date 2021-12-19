package me.MaxBans.SkyWars.Listeners;

import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.Managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class LobbyListener implements Listener {

    private ConfigManager configManager = new ConfigManager();
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();

        if(e.getItem() != null) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (GameState.isState(GameState.LOBBY) || GameState.isState(GameState.STARTING)) {
                    if (e.getItem().getType() == Material.FEATHER) {
                        Bukkit.dispatchCommand(p, "game kit");
                    } else if (e.getItem().getType() == Material.PAPER) {
                        Bukkit.dispatchCommand(p, "vote");
                    }
                }
            }
        }else{
            return;
        }
    }

    @EventHandler
    public void onItemBreak(BlockBreakEvent e){
        if (GameState.isState(GameState.LOBBY) || GameState.isState(GameState.STARTING)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onItemPlace(BlockPlaceEvent e){
        if (GameState.isState(GameState.LOBBY) || GameState.isState(GameState.STARTING)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e){
        if (GameState.isState(GameState.LOBBY) || GameState.isState(GameState.STARTING)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e){
        if (!GameState.isState(GameState.PLAYING)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFireSpread(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent e) {
        Entity p = e.getEntity();
        if(GameState.isState(GameState.LOBBY) || GameState.isState(GameState.STARTING) || GameState.isState(GameState.WINNER)) {
            if (((p instanceof Player))) {
                if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    p.teleport(configManager.getLobbySpawn());
                    e.setCancelled(true);
                    p.setFallDistance(0);
                    ((Player) p).setHealth(20);

                }else if(e.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
                    e.setCancelled(true);
                }else{
                    e.setCancelled(true);
                }
            }
        }else if(GameState.isState(GameState.PREGAME)){
            if((p instanceof Player)){
                if(e.getCause() == EntityDamageEvent.DamageCause.VOID){
                    e.setCancelled(true);
                    ((Player) p).setHealth(20);
                }
            }
        }
    }
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e){
        e.setCancelled(true);
    }
 @EventHandler
    public void onBed(PlayerBedEnterEvent e){
        e.setCancelled(true);
    }
    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e){
        if(GameState.isState(GameState.LOBBY) || GameState.isState(GameState.STARTING)){
            e.setFoodLevel(20);
            e.setCancelled(true);
        }
    }
}
