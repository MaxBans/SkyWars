package me.MaxBans.SkyWars.Listeners;

import me.MaxBans.SkyWars.Managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        if(!PlayerManager.spectators.contains(player.getUniqueId()) || !PlayerManager.deadPlayers.contains(player.getUniqueId())) {
            for (Player onlinePlayers : e.getRecipients()) {
                onlinePlayers.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Alive" + " " + ChatColor.RESET + player.getName() + ": " + e.getMessage());
            }
        }else{
            for(UUID id : PlayerManager.deadPlayers){
                Player p = Bukkit.getPlayer(id);
                p.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Spectator" + " " + ChatColor.RESET + player.getName() + ": " + e.getMessage());
            }
        }
        e.setCancelled(true);
    }
}
