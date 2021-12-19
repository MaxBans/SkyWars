/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Tasks;

import com.connorlinfoot.titleapi.TitleAPI;
import me.MaxBans.SkyWars.GameState.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PreGameCountdownTask extends BukkitRunnable {
    GameState gameState;

    public PreGameCountdownTask(GameState gameState){
        this.gameState = gameState;
    }

    private int timeLeft = 5;

    @Override
    public void run() {
        timeLeft--;
        if(GameState.isState(GameState.PLAYING)) cancel();

        if(timeLeft<= 0){
            cancel();
            for (Player player : Bukkit.getOnlinePlayers()) {
                TitleAPI.sendTitle(player,10,15,10,"§3SkyWars","§7Kill them all!");
            }
            GameState.setState(GameState.PLAYING);
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if(timeLeft == 5 || timeLeft == 4){
                return;
            }else if(timeLeft == 3) {
                TitleAPI.sendTitle(player,10,15,10, ChatColor.GREEN + "" + timeLeft + "","§fGame starting in");
            }else if(timeLeft == 2) {
                TitleAPI.sendTitle(player,10,15,10, ChatColor.YELLOW + "" + timeLeft + "","§fGame starting in");
            } else if(timeLeft == 1) {
                TitleAPI.sendTitle(player,10,15,10, ChatColor.RED + "" + timeLeft + "","§fGame starting in");
            }
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 20, 20);

        }
    }
    }

