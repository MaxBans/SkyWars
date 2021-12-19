package me.MaxBans.SkyWars.Listeners;

import com.connorlinfoot.titleapi.TitleAPI;
import maxbans.hcurrencyapi.CurrencyManager;
import maxbans.hcurrencyapi.DepositReason;
import maxbans.hcurrencyapi.GamePlayer;
import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.Managers.ConfigManager;
import me.MaxBans.SkyWars.Managers.GameManager;
import me.MaxBans.SkyWars.Managers.PlayerManager;
import me.MaxBans.SkyWars.Managers.VotingManager;
import me.MaxBans.SkyWars.SkyWarsPlugin;
import me.MaxBans.SkyWars.Stats.SQLGetter;
import me.MaxBans.SkyWars.Util.KillMessages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class PlayerDeathListener implements Listener {

    private final SkyWarsPlugin plugin;
    private final SQLGetter sqlGetter = new SQLGetter(SkyWarsPlugin.getInstance());
    private final Map<UUID, UUID> lastHitUuid = new HashMap<>();
    private final Set<UUID> causedVoid = new HashSet<>();
    ConfigManager configManager = new ConfigManager();

    public PlayerDeathListener(final SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player p = (Player) evt.getEntity();

            UUID uuid = p.getUniqueId();
            Entity damager = evt.getDamager();
            if (damager instanceof HumanEntity) {
                if (damager instanceof Player) {
                    lastHitUuid.put(uuid, damager.getUniqueId());
                } else {
                    lastHitUuid.remove(uuid);
                }
            } else if (damager instanceof Projectile) {
                ProjectileSource shooter = ((Projectile) damager).getShooter();
                if (shooter == null || !(shooter instanceof LivingEntity)) { // we want to make sure the shooter is a LivingEntity
                    lastHitUuid.remove(uuid);
                } else {
                    if (shooter instanceof HumanEntity) {
                        if (shooter instanceof Player) {
                            lastHitUuid.put(uuid, ((Player) shooter).getUniqueId());
                        } else {
                            lastHitUuid.remove(uuid);
                        }

                    } else {
                        String customName = ((LivingEntity) shooter).getCustomName();
                        lastHitUuid.remove(uuid);
                    }
                }
            } else if (damager instanceof LivingEntity) {
                lastHitUuid.remove(uuid);
            } else {
                lastHitUuid.remove(uuid);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player) {
            UUID uuid = evt.getEntity().getUniqueId();
            if (evt.getCause() == EntityDamageEvent.DamageCause.VOID) {
                causedVoid.add(uuid);
            } else {
                causedVoid.remove(uuid);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void findDeath(EntityDamageEvent e) {
        if(GameState.isState(GameState.PLAYING)){
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            //Player damager = (Player) p.getLastDamageCause();
            if (e.getFinalDamage() >= p.getHealth()) {
                if(PlayerManager.spectators.contains(p.getUniqueId())|| PlayerManager.deadPlayers.contains(p.getUniqueId())) return;

                if ((e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) || (e.getCause() == EntityDamageEvent.DamageCause.FIRE)) {
                    if (lastHitUuid.containsKey(p.getUniqueId())) {
                        UUID killedID = lastHitUuid.get(p.getUniqueId());
                        Player killer = Bukkit.getPlayer(killedID);
                        String message = KillMessages.getMessage(p.getName(), killer.getName(), KillMessages.KillReason.OTHER);
                        Bukkit.broadcastMessage(message);
                        killPlayerByPlayer(killer, p);
                    }else {
                        String message = KillMessages.getMessage(p.getName(), null, KillMessages.KillReason.OTHER);
                        Bukkit.broadcastMessage(message);
                        killPlayer(p);

                        e.setCancelled(true);
                        p.setHealth(20D);
                        p.setFoodLevel(20);
                    }
                } else if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    String message = KillMessages.getMessage(p.getName(), null, KillMessages.KillReason.OTHER);
                    Bukkit.broadcastMessage(message);
                    e.setCancelled(true);
                    p.setHealth(20D);
                    p.setFoodLevel(20);
                    killPlayer(p);

                } else if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {

                    if (lastHitUuid.containsKey(p.getUniqueId())){
                        UUID killedID = lastHitUuid.get(p.getUniqueId());
                        Player killer = Bukkit.getPlayer(killedID);
                        String message = KillMessages.getMessage(p.getName(), killer.getName(), KillMessages.KillReason.VOID);
                        Bukkit.broadcastMessage(message);
                        e.setCancelled(true);
                        killPlayerByPlayer(killer, p);

                    }else {
                        String message = KillMessages.getMessage(p.getName(), null, KillMessages.KillReason.VOID);
                        Bukkit.broadcastMessage(message);
                        killPlayer(p);

                        e.setCancelled(true);
                        p.setHealth(20D);
                        p.setFoodLevel(20);
                    }
                } else if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {

                    EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                    e.setCancelled(true);
                    if (event.getDamager() instanceof Player) {
                        Player killer = (Player) event.getDamager();
                        String message = KillMessages.getMessage(p.getName(), killer.getName(), KillMessages.KillReason.LEFT);
                        Bukkit.broadcastMessage(message);
                        killPlayerByPlayer(killer, p);

                    } else {
                        String message = KillMessages.getMessage(p.getName(), event.getDamager().getName(), KillMessages.KillReason.LEFT);
                        Bukkit.broadcastMessage(message);
                        killPlayer(p);

                    }
                    p.setHealth(20D);
                    p.setFoodLevel(20);
                }else if(e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE){
                    EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                    Projectile projectile = (Projectile) event.getDamager();
                    if (((projectile.getShooter() instanceof Player)) && ((event.getEntity() instanceof Player))) {
                        Player player = ((Player) event.getEntity()).getPlayer();
                        Player killer = ((Player) projectile.getShooter()).getPlayer();
                        UUID u = killer.getUniqueId();
                        String message = KillMessages.getMessage(player.getName(), killer.getName(), KillMessages.KillReason.LEFT);
                        Bukkit.broadcastMessage(message);
                        killPlayerByPlayer(killer, player);

                        e.setCancelled(true);
                        event.setCancelled(true);
                    }
                }
                else {
                    System.out.println("statement not added yet.");
                    String message = KillMessages.getMessage(p.getName(), null, KillMessages.KillReason.OTHER);
                    Bukkit.broadcastMessage(message);
                    e.setCancelled(true);
                    killPlayer(p);

                    p.setHealth(20D);
                    p.setFoodLevel(20);
                    }
                }
            }
        }
    }

    public void killPlayer(Player victim) {
        PlayerManager.alivePlayers.remove(victim.getUniqueId());
        sqlGetter.addDeath(victim.getUniqueId(), 1);

        sqlGetter.addPoint(victim.getUniqueId(), ConfigManager.getPointsForPlay());

        PlayerManager.alivePlayers.remove(victim.getUniqueId());
        PlayerManager.deadPlayers.add(victim.getUniqueId());

        System.out.println("database updated!");
        TitleAPI.sendTitle(victim, 10, 30, 10, "§7You are now spectating", "--");
        for (ItemStack item : victim.getInventory().getContents()) {
            if (item != null) {
                victim.getWorld().dropItem(victim.getLocation(), item).setPickupDelay(20);
            }
        }
        victim.teleport(configManager.getSpectatorSpawn(VotingManager.getWinner().getCustomName()));
        victim.getInventory().clear();

        GameManager.setSpectator(victim);
        GameManager.sendRateMessage(victim, VotingManager.getWinner());

        if (PlayerManager.alivePlayers.size() == 1) {
            GameState.setState(GameState.WINNER);
        }
        //Reset potion effects
        for (PotionEffect ev : victim.getActivePotionEffects()) {
            victim.addPotionEffect(new PotionEffect(ev.getType(), 0, 0), true);
        }
    }

    public void killPlayerByPlayer(Player killer, Player victim) {
        PlayerManager.alivePlayers.remove(victim.getUniqueId());
        sqlGetter.addPoint(victim.getUniqueId(), ConfigManager.getPointsForPlay());
        sqlGetter.addPoint(killer.getUniqueId(), ConfigManager.getPointsForKill());
        int kills = PlayerManager.getKills(killer.getUniqueId());
        kills++;
        if (victim.getUniqueId() != killer.getUniqueId()) {
            sqlGetter.addKill(killer.getUniqueId(), 1);
        }
        sqlGetter.addDeath(victim.getUniqueId(), 1);
        GamePlayer gamePlayer = new GamePlayer(killer);
        CurrencyManager.depositCoins(gamePlayer, ConfigManager.getCoinsForKill(), DepositReason.MINIGAME);
        PlayerManager.coinsEarned.put(killer.getUniqueId(), PlayerManager.getCoins(killer.getUniqueId()) + ConfigManager.getCoinsForKill());

        PlayerManager.playerKills.put(killer.getUniqueId(), kills);

        PlayerManager.alivePlayers.remove(victim.getUniqueId());
        PlayerManager.deadPlayers.add(victim.getUniqueId());

        System.out.println("database updated!");
        killer.playSound(killer.getLocation(), Sound.EXPLODE, 50, 50);
        if(!PlayerManager.deadPlayers.contains(killer.getUniqueId())) {
            killer.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        }


        TitleAPI.sendTitle(victim, 10, 30, 10, "§7You are now spectating", "--");
        for (ItemStack item : victim.getInventory().getContents()) {
            if (item != null) {
                victim.getWorld().dropItem(victim.getLocation(), item).setPickupDelay(20);
            }
        }
        victim.teleport(configManager.getSpectatorSpawn(VotingManager.getWinner().getCustomName()));
        victim.getInventory().clear();

        GameManager.setSpectator(victim);
        GameManager.sendRateMessage(victim, VotingManager.getWinner());

        if (PlayerManager.alivePlayers.size() == 1) {
            GameState.setState(GameState.WINNER);
        }
        //Reset potion effects
        for (PotionEffect ev : victim.getActivePotionEffects()) {
            victim.addPotionEffect(new PotionEffect(ev.getType(), 0, 0), true);
        }

    }

    @EventHandler
    public void onPearl(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
            event.getPlayer().setNoDamageTicks(1);
            event.getPlayer().teleport(event.getTo());
        }
    }

    public void clear(){
        this.lastHitUuid.clear();
        this.causedVoid.clear();
    }
}


