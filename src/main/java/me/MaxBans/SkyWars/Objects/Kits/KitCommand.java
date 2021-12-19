/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Objects.Kits;

import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.Managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;

            if(args.length == 1 && args[0].equalsIgnoreCase("kit")){
                if(GameState.isState(GameState.LOBBY) || GameState.isState(GameState.STARTING)){
                    if(GameManager.playersPlaying.contains(p.getUniqueId())){
                       new KitsGUI(p);
                    }else{
                        p.sendMessage("§cError");
                    }
                }else{
                    p.sendMessage("§cNo permission!");
                }
            }
        }
        return false;
    }
}
