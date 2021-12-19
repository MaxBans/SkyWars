package me.MaxBans.SkyWars.Managers;

import me.MaxBans.SkyWars.Maps.GameMap;
import me.MaxBans.SkyWars.Maps.RatingManager;
import me.MaxBans.SkyWars.SkyWarsPlugin;
import me.MaxBans.SkyWars.Stats.SQLGameInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class VotingManager {
    private SkyWarsPlugin plugin;

    private GameMap map;

    public static boolean isRunning = true;
    public static Map<GameMap, Integer> voteMap = new HashMap<>();
    public static List<UUID> voted = new ArrayList<>();




    public static void start(){
        voteMap.clear();
        voted.clear();
        for(GameMap map : GameMap.values()){
            voteMap.put(map, 0);
        }
    }

    public static void vote(GameMap map, Player p){
        int count = voteMap.get(map);
        if(!voted.contains(p.getUniqueId())) {
            if (isRunning()) {
                if (map.getVoted().containsKey(p.getUniqueId())) {
                    p.sendMessage(ChatColor.RED + "You have already voted for this map");
                } else {
                    voteMap.replace(map, count + 1);
                    map.getVoted().put(p.getUniqueId(), map);
                    voted.add(p.getUniqueId());
                    p.sendMessage("§8[§3Vote§8]§7 You voted for map: §b" + map.getCustomName());
                }
            } else {
                p.sendMessage(ChatColor.RED + "Voting has ended");
            }
        }else{
            p.sendMessage(ChatColor.RED + "You don't have any remaining votes!");
        }
    }

    public static Integer getVotes(GameMap map){
        return voteMap.get(map);
    }

    public static GameMap getWinner(){
        return Collections.max(voteMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    public static boolean isRunning(){
        return isRunning;
    }

    public static void end(){
        isRunning = false;
        for(Player p : Bukkit.getOnlinePlayers()){
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 5,5);
        }
        for(Player pl : Bukkit.getOnlinePlayers()){
            pl.closeInventory();
        }
        Bukkit.broadcastMessage("§8[§3SkyWars§8]§7 Map voting has ended!");
        Bukkit.broadcastMessage("§8[§3SkyWars§8]§3 Map winner:§b " + getWinner().getCustomName());
        SQLGameInfo.updateMap(getWinner().getCustomName());
        VotingManager.getWinner().load();
    }
    public static List<String> getMaps(){
        return SkyWarsPlugin.getInstance().getConfig().getStringList("settings.maps");
    }

    public static void sendMapMessage(){
        Bukkit.broadcastMessage("■■■■■■■■■■ §3§lTournament§r ■■■■■■■■■■");
        Bukkit.broadcastMessage("   §3§lSkyWars-" + VotingManager.getWinner().getCustomName());
        Bukkit.broadcastMessage("   §7§oRating of Map: §b" + RatingManager.getAverageRating(VotingManager.getWinner()) + "§e§l✫" );
        Bukkit.broadcastMessage("   §bBuild by: §3" + VotingManager.getWinner().getBuilder());
        Bukkit.broadcastMessage("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
    }



}
