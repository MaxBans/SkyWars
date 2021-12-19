package me.MaxBans.SkyWars.Listeners;

import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.Managers.GameManager;
import me.MaxBans.SkyWars.Managers.VotingManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPingListener implements Listener {
    private GameState gameState;

        @EventHandler
        public void onPing(ServerListPingEvent event) {
            if(GameState.isState(GameState.LOBBY) || GameState.isState(GameState.STARTING)) {
                event.setMotd("Waiting | " + GameManager.playersPlaying.size() + "/12 | Voting");
            }
            else if(GameState.isState(GameState.PLAYING)){
                event.setMotd("Active | " + GameManager.playersPlaying.size() + "/12 | " + VotingManager.getWinner().getCustomName());
            }else if(GameState.isState(GameState.WINNER) || GameState.isState(GameState.END)){
                event.setMotd("Ending | " + GameManager.playersPlaying.size() + "/12 | " + VotingManager.getWinner().getCustomName());
            }
            event.setMaxPlayers(20);
        }
    }


