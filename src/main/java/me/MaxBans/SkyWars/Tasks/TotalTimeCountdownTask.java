
/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Tasks;

import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.Managers.BorderManager;
import me.MaxBans.SkyWars.Managers.ConfigManager;
import me.MaxBans.SkyWars.Managers.VotingManager;
import me.MaxBans.SkyWars.SkyWarsPlugin;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitRunnable;

public class TotalTimeCountdownTask extends BukkitRunnable {
    private  GameState gameState;
    public static int timeLeft = 600;
    private final BorderManager borderManager = new BorderManager();
    private final WorldBorder border1 = BorderManager.getBorder1();
    private final WorldBorder border2 = BorderManager.getBorder2();
    private final ConfigManager configManager = new ConfigManager();

    public TotalTimeCountdownTask(GameState gameState){
        this.gameState = gameState;
    }

    @Override
    public void run() {
        if (GameState.isState(GameState.STARTING) || GameState.isState(GameState.LOBBY)) {
            return;
        }

        timeLeft--;
        if (timeLeft <= 0) {
            cancel();
            GameState.setState(GameState.END);
            return;
        } else if (timeLeft <=configManager.startTime(VotingManager.getWinner().getCustomName(), 1)  && !(timeLeft <= configManager.endTime(VotingManager.getWinner().getCustomName(), 1))) {
            borderManager.startShrinking(timeLeft, border1, configManager.getShrinkSpeed(VotingManager.getWinner().getCustomName(), 1));
        }else if (timeLeft <=configManager.startTime(VotingManager.getWinner().getCustomName(), 2)  && !(timeLeft <= configManager.endTime(VotingManager.getWinner().getCustomName(), 2))) {
            borderManager.startShrinking(timeLeft, border2, configManager.getShrinkSpeed(VotingManager.getWinner().getCustomName(), 2));
        }
        SkyWarsPlugin.scoreboard.updateScoreboard();
    }

    public static String getTitle(){
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        String str;
        str = String.format("%d:%02d", minutes, seconds);
        return str;
    }
}
