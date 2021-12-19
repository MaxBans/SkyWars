/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Stats;


import me.MaxBans.SkyWars.SkyWarsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    private SQLGetter sqlGetter = new SQLGetter(SkyWarsPlugin.getInstance());

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if(args[0].equalsIgnoreCase("kills")) {
            String[] stat = sqlGetter.getTop("KILLS", 10);
            p.sendMessage("§7----§3§lTOP 10 KILLERS§r§7----");
            for (int i = 1; i < 10; i++) {
                p.sendMessage(stat[i]);
            }
        }else if(args[0].equalsIgnoreCase("wins")) {
            String[] stat = sqlGetter.getTop("WINS", 10);
            p.sendMessage("§7----§3§lTOP 10 WINS§r§7----");
            for (int i = 1; i < 10; i++) {
                p.sendMessage(stat[i]);
            }
        }else if(args[0].equalsIgnoreCase("deaths")) {
            String[] stat = sqlGetter.getTop("DEATHS", 10);
            p.sendMessage("§7----§3§lTOP 10 DEATHS§r§7----");
            for (int i = 1; i < 10; i++) {
                p.sendMessage(stat[i]);
            }
        }else{
            return false;
        }
        return false;
    }
}
