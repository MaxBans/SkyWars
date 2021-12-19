/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Tasks;

import me.MaxBans.SkyWars.GameState.GameState;
import org.bukkit.scheduler.BukkitRunnable;

public class VictoryEndCountdownTask extends BukkitRunnable {

    GameState gameState;

    public VictoryEndCountdownTask(GameState gameState){
        this.gameState = gameState;
    }

    private int timeLeft = 15;

    @Override
    public void run() {
        timeLeft--;

        if(timeLeft<= 0){
            cancel();
            GameState.setState(GameState.END);
            return;
        }
    }
}
