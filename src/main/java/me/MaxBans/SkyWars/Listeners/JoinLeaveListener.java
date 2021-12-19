package me.MaxBans.SkyWars.Listeners;

import com.connorlinfoot.titleapi.TitleAPI;
import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.Managers.ConfigManager;
import me.MaxBans.SkyWars.Managers.GameManager;
import me.MaxBans.SkyWars.Managers.PlayerManager;
import me.MaxBans.SkyWars.Managers.VotingManager;
import me.MaxBans.SkyWars.Objects.LobbyItemManager;
import me.MaxBans.SkyWars.SkyWarsPlugin;
import me.MaxBans.SkyWars.Stats.SQLGameInfo;
import me.MaxBans.SkyWars.Tasks.GameStartCountdownTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

public class JoinLeaveListener implements Listener {
    private GameManager gameManager = new GameManager(SkyWarsPlugin.getInstance());
    private ConfigManager configManager = new ConfigManager();
    private static GameState gameState;
    private static GameStartCountdownTask gameStartCountdownTask = new GameStartCountdownTask(gameState);
    private LobbyItemManager lobbyItemManager = new LobbyItemManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        e.setJoinMessage(null);

        if(GameState.getState() == GameState.LOBBY || GameState.isState(GameState.STARTING)) {
            if(SkyWarsPlugin.getInstance().isUsingMySql()) {
                SkyWarsPlugin.createPlayerProfile(p);
            }

            if(GameManager.playersPlaying.size() >= 12){
                if(p.hasPermission("skywars.spectate.games")){
                    GameManager.setWatcher(p);
                    p.teleport(configManager.getLobbySpawn());
                }else {

                    p.kickPlayer(ChatColor.RED + "This game is full! You need§a skywars.spectate.games§7 permission to spectate games!");
                }
            }else {
                UUID uuid = p.getUniqueId();
                GameManager.playersPlaying.add(uuid);
                p.setGameMode(GameMode.ADVENTURE);
                p.setHealth(20);
                clearArmor(p);
                p.setLevel(0);
                p.setCanPickupItems(true);
                p.setWalkSpeed(0.2F);
                TitleAPI.sendTitle(p,10,15,10,"§3SkyWars","§7Welcome!");
                Bukkit.broadcastMessage("§8[§3SkyWars§8]§b " + p.getName() + " §7has joined the game! " + "§3(" + Bukkit.getServer().getOnlinePlayers().size() + ")");
                sendInfoMessage(p);
                p.getInventory().clear();
                SQLGameInfo.updatePlayers(GameManager.playersPlaying.size());
                lobbyItemManager.giveLobbyItems(p);
                for (Player players : Bukkit.getOnlinePlayers()) {
                    players.showPlayer(p);
                    p.showPlayer(players);
                }
                p.setFoodLevel(20);
                p.teleport(configManager.getLobbySpawn());
                for (PotionEffect ev : p.getActivePotionEffects()) {
                    p.addPotionEffect(new PotionEffect(ev.getType(), 0, 0), true);
                }

                if (!(GameState.isState(GameState.STARTING)) && GameManager.playersPlaying.size() >= SkyWarsPlugin.getInstance().getConfig().getInt("settings.playersNeeded")) {
                    GameState.setState(GameState.STARTING);
                }
            }
        }else{
            if(p.hasPermission("skywars.spectate.games")){
                GameManager.setWatcher(p);
                p.teleport(configManager.getSpectatorSpawn(VotingManager.getWinner().getCustomName()));
            }else {
                p.kickPlayer("You cant join an recruiting game");
            }
        }
    }

    public void clearArmor(Player player){
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }


    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();
        GameManager.playersPlaying.remove(p.getUniqueId());
        PlayerManager.alivePlayers.remove(p.getUniqueId());
        PlayerManager.deadPlayers.remove(p.getUniqueId());
        PlayerManager.spectators.remove(p.getUniqueId());
        gameManager.removeKit(p.getUniqueId());
        SQLGameInfo.updatePlayers(GameManager.playersPlaying.size());
        p.getInventory().clear();
        if(GameState.isState(GameState.STARTING) && GameManager.playersPlaying.size() < configManager.getCfg().getInt("settings.playersNeeded")){
            GameState.setState(GameState.LOBBY);
            gameStartCountdownTask.setTimeLeft(120);
            VotingManager.isRunning = true;
            int a = Bukkit.getServer().getOnlinePlayers().size() - 1;
            if(PlayerManager.deadPlayers.contains(p.getUniqueId()) || PlayerManager.spectators.contains(p.getUniqueId())){
                e.setQuitMessage(null);
            }else {
                e.setQuitMessage("§8[§3SkyWars§8]§b " + p.getName() + " §7has left the game! " + "§3(" + a + ")");
            }
        }
        else if(GameState.isState(GameState.PLAYING) || GameState.isState(GameState.PREGAME) || GameState.isState(GameState.WINNER)){
                e.setQuitMessage(null);
            if(GameManager.playersPlaying.size() == 0){
               GameState.setState(GameState.END);
            }
        }else if(GameState.isState(GameState.LOBBY)){
            int a = Bukkit.getServer().getOnlinePlayers().size() - 1;
            e.setQuitMessage("§8[§3SkyWars§8]§b " + p.getName() + " §7has left the game! " + "§3(" + a + ")");
        }

        if(PlayerManager.alivePlayers.size() == 1 && GameState.isState(GameState.PLAYING)){
            GameState.setState(GameState.WINNER);
        }
    }

    public void sendInfoMessage(Player p){
        p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "■■■■■■■■■■■■■ §b§lINFO§r ■■■■■■■■■■■■■■");
        p.sendMessage("");
        p.sendMessage("§r§7 - All Kits are free for the tournament!");
        p.sendMessage("");
        p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
    }


}


