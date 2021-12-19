package me.MaxBans.SkyWars.Managers;

import me.MaxBans.SkyWars.SkyWarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    public FileConfiguration getCfg(){
        return SkyWarsPlugin.getInstance().getConfig();
    }



    public World getGameWorld(){
        World world = VotingManager.getWinner().getBukkitWorld();
        return world;
    }
    public Location getSpawn(int i, String map){

        return new Location(getGameWorld(), SkyWarsPlugin.getInstance().getMapsConfig().getInt("settings.spawns."+ map + ".spawn-" + i + ".x"),
                SkyWarsPlugin.getInstance().getMapsConfig().getInt("settings.spawns."+ map + ".spawn-" + i + ".y"),
                SkyWarsPlugin.getInstance().getMapsConfig().getInt("settings.spawns."+ map + ".spawn-" + i + ".z"));
    }

    public Location getLobbySpawn(){
        World world = Bukkit.getWorld(getCfg().getString("settings.spawns.lobby.world"));
        return new Location(world, getCfg().getInt("settings.spawns.lobby.x"), getCfg().getInt("settings.spawns.lobby.y"), getCfg().getInt("settings.spawns.lobby.z"));
    }

    public static List<String> getMapList(){
        return SkyWarsPlugin.getInstance().getConfig().getStringList("settings.maps");
    }
    public static String getServerName(){
        return SkyWarsPlugin.getInstance().getConfig().getString("settings.serverName");
    }

    public static Integer getPointsForWin(){
        return SkyWarsPlugin.getInstance().getConfig().getInt("settings.Points-Win");
    }

    public static Integer getPointsForKill(){
        return SkyWarsPlugin.getInstance().getConfig().getInt("settings.Points-Kills");
    }

    public static Integer getPointsForPlay(){
        return SkyWarsPlugin.getInstance().getConfig().getInt("settings.Points-Play");
    }

    public static Integer getCoinsForWin(){
        return SkyWarsPlugin.getInstance().getConfig().getInt("settings.Coins-Win");
    }

    public static Integer getCoinsForKill(){
        return SkyWarsPlugin.getInstance().getConfig().getInt("settings.Coins-Kill");
    }


    //Border section
    public double getCenterX(String map){
        return SkyWarsPlugin.getInstance().getMapsConfig().getDouble("settings.spawns." + map + ".centerBorder1.x");
    }
    public double getCenterZ(String map){
        return SkyWarsPlugin.getInstance().getMapsConfig().getDouble("settings.spawns." + map + ".centerBorder1.Z");
    }
    public double getSize(String map){
        return SkyWarsPlugin.getInstance().getMapsConfig().getDouble("settings.spawns." + map + ".centerBorder1.size");
    }
    public int getShrinkSpeed(String map, int i){
        return SkyWarsPlugin.getInstance().getMapsConfig().getInt("settings.spawns." + map + ".centerBorder" + i + ".shrinkSpeed");
    }
    public int startTime(String map, int i){
        return SkyWarsPlugin.getInstance().getMapsConfig().getInt("settings.spawns." + map + ".centerBorder"+  i + ".startTime");
    }
    public int endTime(String map, int i){
        return SkyWarsPlugin.getInstance().getMapsConfig().getInt("settings.spawns." + map + ".centerBorder"+ i +".endTime");
    }
    //

    public Location getSpectatorSpawn(String map){
        World world = getGameWorld();
        return new Location(world, SkyWarsPlugin.getInstance().getMapsConfig().getInt("settings.spawns." + map + ".spectator.x"),
                SkyWarsPlugin.getInstance().getMapsConfig().getInt("settings.spawns." + map + ".spectator.y"),
                SkyWarsPlugin.getInstance().getMapsConfig().getInt("settings.spawns." + map + ".spectator.z"));
    }

    public List<String> getWinLocList(){
        return getCfg().getStringList("settings.spawns.WinLocation.locations");
    }
    public List<String> getMaps(){
        return getCfg().getStringList("settings.maps");
    }

    public List<String> getChestList(String map){
        return getCfg().getStringList("chests.locations." + map);
    }

    public List<String> getHologramList(String type){
        return getCfg().getStringList("holograms.locations." + type);
    }

    public Location getWinLocForWinner(){
        World w = Bukkit.getWorld(getCfg().getString("settings.spawns.WinLocation.Winner.world"));
        return new Location(w, getCfg().getDouble("settings.spawns.WinLocation.Winner.x"), getCfg().getDouble("settings.spawns.WinLocation.Winner.y"), getCfg().getDouble("settings.spawns.WinLocation.Winner.z"));
    }

    public  String getWinCoords(int i) {
        return getWinLocList().get(i);
    }

   
}
