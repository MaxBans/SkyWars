package me.MaxBans.SkyWars.commands;

import me.MaxBans.SkyWars.Managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectatorCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission("skywars.spectate.games")){
                GameManager.setWatcher(p);
            }
        }
        return false;
    }
}
