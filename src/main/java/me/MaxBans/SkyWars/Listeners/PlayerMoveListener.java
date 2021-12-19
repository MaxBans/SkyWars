package me.MaxBans.SkyWars.Listeners;

import com.connorlinfoot.titleapi.TitleAPI;
import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.Managers.ConfigManager;
import me.MaxBans.SkyWars.Managers.PlayerManager;
import me.MaxBans.SkyWars.Managers.VotingManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    private ConfigManager configManager = new ConfigManager();
    @EventHandler
    public void movement(PlayerMoveEvent event) {
        if(PlayerManager.spectators.contains(event.getPlayer().getUniqueId()) || PlayerManager.deadPlayers.contains(event.getPlayer().getUniqueId())){
            if(isOutsideBorder(event.getPlayer())){
                event.getPlayer().teleport(configManager.getSpectatorSpawn(VotingManager.getWinner().getCustomName()));
                TitleAPI.sendTitle(event.getPlayer(), 15, 30, 15, ChatColor.RED + "" + ChatColor.BOLD + "Stay inside border!", "--");
            }
        }

        if(GameState.isState(GameState.PREGAME)) {
            Player player = event.getPlayer();
            double xTo = event.getTo().getX();
            double xFrom = event.getFrom().getX();
            double yTo = event.getTo().getY();
            double yFrom = event.getFrom().getY();
            double zTo = event.getTo().getZ();
            double zFrom = event.getFrom().getZ();
            if (event.getTo().locToBlock(xTo) != event.getFrom().locToBlock(xFrom) || event.getTo().locToBlock(zTo) != event.getFrom().locToBlock(zFrom) || event.getTo().locToBlock(yTo) != event.getFrom().locToBlock(yFrom)) {
                player.teleport(event.getFrom());
            }
        }
    }

    public boolean isOutsideBorder(Player p){
        Location loc = p.getLocation();
        WorldBorder border = p.getWorld().getWorldBorder();
        double size = border.getSize()/2;
        Location center = border.getCenter();
        double x = loc.getX() - center.getX(), z = loc.getZ() - center.getZ();
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }
}
