package me.MaxBans.SkyWars.Maps;

import me.MaxBans.SkyWars.Managers.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can't do this in console!");
            return false;
        }
        Player player = (Player) sender;
        if(args.length >= 1)
            if (!RatingManager.rated.contains(player.getUniqueId())) {
                if (ConfigManager.getMapList().contains(args[0])) {
                    if(isStringInt(args[1])) {
                        RatingManager.rate(GameMap.valueOf(args[0]), Integer.parseInt(args[1]), player);
                    }else {
                        player.sendMessage(ChatColor.RED + "Not a number");
                    }
                }else{
                    player.sendMessage(ChatColor.RED + "Not valid map");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You already rated a map!");
            }
        return false;
    }

    public boolean isStringInt(String s)
    {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
