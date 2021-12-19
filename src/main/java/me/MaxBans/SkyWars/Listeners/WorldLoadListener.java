package me.MaxBans.SkyWars.Listeners;

import me.MaxBans.SkyWars.Managers.VotingManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldLoadListener implements Listener {
    @EventHandler
    public void onWorldLoad(WorldLoadEvent e){
        if(e.getWorld().getName().equals(VotingManager.getWinner().getCustomName())){
            System.out.println("DEBUG World " + VotingManager.getWinner().getWorld().getName() + " was fully reset!");
        }
    }
}
