/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Objects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LobbyItemManager  {

    public void giveLobbyItems(Player p){
        ItemStack kit = new ItemStack(Material.FEATHER);
        ItemMeta kitMeta = kit.getItemMeta();
        kitMeta.setDisplayName("§bSelect Class");
        List<String> kitLore = new ArrayList<>();
        kitLore.add("§7Right-Click to select class");
        kitMeta.setLore(kitLore);
        kit.setItemMeta(kitMeta);

        ItemStack map = new ItemStack(Material.PAPER);
        ItemMeta mapMeta = map.getItemMeta();
        mapMeta.setDisplayName("§3Vote for map");
        List<String> mapLore = new ArrayList<>();
        mapLore.add("§7Right-Click to vote for map");
        mapMeta.setLore(mapLore);
        map.setItemMeta(mapMeta);


        p.getInventory().setItem(0,kit);
        p.getInventory().setItem(1, map);
    }


}
