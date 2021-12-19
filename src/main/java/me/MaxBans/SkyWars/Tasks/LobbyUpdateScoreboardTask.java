/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Tasks;

import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.SkyWarsPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyUpdateScoreboardTask extends BukkitRunnable {

    private SkyWarsPlugin skyWarsPlugin;

    public LobbyUpdateScoreboardTask(SkyWarsPlugin skyWarsPlugin){

        this.skyWarsPlugin = skyWarsPlugin;
    }

    @Override
    public void run() {
        SkyWarsPlugin.lobbyScoreboard.updateScoreboard();
        if(GameState.isState(GameState.PREGAME) || GameState.isState(GameState.PLAYING) || GameState.isState(GameState.WINNER)){
            cancel();
        }
    }
}
