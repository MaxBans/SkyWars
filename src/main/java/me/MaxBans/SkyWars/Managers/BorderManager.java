package me.MaxBans.SkyWars.Managers;

import com.connorlinfoot.titleapi.TitleAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

public class BorderManager {

    private static ConfigManager configManager = new ConfigManager();
    private static WorldBorder border1 = VotingManager.getWinner().getBukkitWorld().getWorldBorder();

    public static WorldBorder getBorder1(){
        border1.setCenter(configManager.getCenterX(VotingManager.getWinner().getCustomName()), configManager.getCenterZ(VotingManager.getWinner().getCustomName()));
        border1.setSize(configManager.getSize(VotingManager.getWinner().getCustomName()));
        border1.setDamageBuffer(5);
        border1.setDamageAmount(0.5);
        border1.setWarningDistance(5);
        border1.setWarningTime(1);
        return border1;
    }

    public static WorldBorder getBorder2(){
        WorldBorder border1 = configManager.getGameWorld().getWorldBorder();
        border1.setDamageBuffer(2);
        border1.setDamageAmount(2);
        border1.setWarningDistance(5);
        border1.setWarningTime(1);
        return border1;
    }

    public void startShrinking(int timeLeft, WorldBorder border1, int shrinkSpeed){
        if (timeLeft == configManager.startTime(VotingManager.getWinner().getCustomName(), 1)) {
            Bukkit.broadcastMessage("§8[§3SkyWars§8]§c Border is now moving!");
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.playSound(pl.getLocation(), Sound.WITHER_SPAWN, 20, 20);
                TitleAPI.sendTitle(pl, 20, 20, 20, "§c§lBorder is moving", "--");
            }
        }else if(timeLeft == configManager.startTime(VotingManager.getWinner().getCustomName(), 2)){
            Bukkit.broadcastMessage("§8[§3SkyWars§8]§c Border is now moving!");
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.playSound(pl.getLocation(), Sound.WITHER_SPAWN, 20, 20);
                TitleAPI.sendTitle(pl, 20, 20, 20, "§c§lBorder is moving", "--");
            }
        }
        double size = border1.getSize();
        border1.setSize(size - shrinkSpeed, 1);
    }


}
