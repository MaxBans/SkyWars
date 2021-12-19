package me.MaxBans.SkyWars.Managers;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private Player p;
    public PlayerManager(Player p){
       this.p = p;
    }
    public static List<UUID> deadPlayers = new ArrayList<>();
    public static List<UUID> alivePlayers = new ArrayList<>();
    public static List<UUID> spectators = new ArrayList<>();
    public static HashMap<UUID, Integer> playerKills = new HashMap<>();
    public static HashMap<UUID, Integer> pointsEarned = new HashMap<>();
    public static HashMap<UUID, Integer> coinsEarned = new HashMap<>();
    public static HashMap<UUID, Integer> keysEarned = new HashMap<>();


    public static Integer getKills(UUID uuid){
        return playerKills.get(uuid);
    }

    public static Integer getPoints(UUID uuid){
        return pointsEarned.get(uuid);
    }

    public static Integer getCoins(UUID uuid){
        return coinsEarned.get(uuid);
    }

    public static Integer getKeys(UUID uuid){
        return keysEarned.get(uuid);
    }
    public static HashMap getKillsMap(){
        return  playerKills;
    }



}
