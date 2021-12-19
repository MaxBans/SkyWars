package me.MaxBans.SkyWars.Maps;

import me.MaxBans.SkyWars.Managers.VotingManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MapLoader {

    /*/public void turnOffAutoSave(){
        World world = Bukkit.createWorld(new WorldCreator(VotingManager.getWinner().getWorld().getName()));
        world.setAutoSave(false);
        System.out.println("World " + world.getName() + " turned autosave off!");
    }/*/

    public void reset(){
        for(Player p : Bukkit.getOnlinePlayers()){
            p.kickPlayer("Server is restarting! Code: 003");
        }
        VotingManager.getWinner().unload();
    }
}
