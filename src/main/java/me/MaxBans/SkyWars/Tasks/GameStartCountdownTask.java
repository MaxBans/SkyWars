/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Tasks;

import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.Managers.GameManager;
import me.MaxBans.SkyWars.Managers.VotingManager;
import me.MaxBans.SkyWars.SkyWarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameStartCountdownTask extends BukkitRunnable {
    GameState gameState;

    public GameStartCountdownTask(GameState gameState){
        this.gameState = gameState;
    }

    public static int timeLeft = 120;

    @Override
    public void run() {
        this.timeLeft--;
        if(timeLeft<= 0){
            cancel();
            GameState.setState(GameState.PREGAME);
            return;
        }else if(timeLeft == 5){
            VotingManager.end();
        }
        if(GameState.isState(GameState.LOBBY)) {
            setTimeLeft(120);
        }


        if(GameManager.playersPlaying.size() == SkyWarsPlugin.getInstance().getConfig().getInt("settings.skipTimePlayers") && !(timeLeft <= 30) && GameState.isState(GameState.STARTING)){ //TODO 11
            setTimeLeft(SkyWarsPlugin.getInstance().getConfig().getInt("settings.reduce-time"));
            Bukkit.broadcastMessage("§8[§3Server§8]§7 Time was reduced to§b " + SkyWarsPlugin.getInstance().getConfig().getInt("settings.reduce-time") +  "§7 seconds");
            return;
        }

        SkyWarsPlugin.getInstance().lobbyScoreboard.updateScoreboard();
    }

    public static void setTimeLeft(int i){
        timeLeft = i;
    }

    public static String getTitle() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        String str = "§b§lVoting§r§f | " + String.format("%d:%02d", minutes, seconds);
        return str;
    }

}
