/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Objects.Kits;


import maxbans.hcurrencyapi.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class KitsGUI {

    public KitsGUI(Player p){
        GamePlayer gamePlayer = new GamePlayer(p);
        Inventory gui = Bukkit.createInventory(null, 18, "Select kit §r§eCoins:§8 " + gamePlayer.getCoins());
        for(KitType type : KitType.values()){
            ItemStack is = new ItemStack(type.getMaterial());
            ItemMeta isMeta = is.getItemMeta();
            isMeta.setDisplayName(type.getDisplay());
            isMeta.setLore(Arrays.asList(type.getDescription()));
            is.setItemMeta(isMeta);
            gui.addItem(is);
        }


        p.openInventory(gui);
    }
}
