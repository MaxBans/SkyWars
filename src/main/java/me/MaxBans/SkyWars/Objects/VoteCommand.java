/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Objects;

import me.MaxBans.SkyWars.Managers.ConfigManager;
import me.MaxBans.SkyWars.Managers.VotingManager;
import me.MaxBans.SkyWars.Maps.GameMap;
import me.MaxBans.SkyWars.Maps.RatingManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class VoteCommand implements CommandExecutor {

   private ConfigManager configManager = new ConfigManager();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(VotingManager.isRunning){
                openVoteInventory(p);
            }else{
                p.sendMessage(ChatColor.RED + "Voting has ended!");
            }
        }
        return false;
    }

    public void openVoteInventory(Player p){
        Inventory gui = Bukkit.createInventory(null, 9, "Vote for map");
        for(GameMap map : GameMap.values()){
            ItemStack is = new ItemStack(map.getMaterial());
            ItemMeta isMeta = is.getItemMeta();
            isMeta.setDisplayName(map.getCustomName());
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Votes:§b " + VotingManager.getVotes(map));
            lore.add(" ");
            lore.add("§7Rating: §b" + RatingManager.getAverageRating(map) + "§e§l✫");
            lore.add(" ");
            lore.add(ChatColor.GRAY + "Builder:§b " + map.getBuilder());
            isMeta.setLore(lore);
            is.setItemMeta(isMeta);
            gui.addItem(is);
        }
        p.openInventory(gui);
    }
}


