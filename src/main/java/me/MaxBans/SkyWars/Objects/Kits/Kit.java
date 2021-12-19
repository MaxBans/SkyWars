/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Objects.Kits;


import me.MaxBans.SkyWars.SkyWarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class Kit implements Listener {

    private UUID uuid;
    private KitType type;

    public Kit(UUID uuid, KitType type){

        this.uuid = uuid;
        this.type = type;

        Bukkit.getPluginManager().registerEvents(this, SkyWarsPlugin.getInstance());
    }

    public UUID getUuid() {
        return uuid;
    }

    public KitType getType() {
        return type;
    }

    public abstract void onStart(Player player);

    public void remove(){
        HandlerList.unregisterAll(this);
    }

    public static void addItems(Player p, int amount, Material material){
        for(int i=0; i < amount; i++){
            p.getInventory().addItem(new ItemStack(material));
        }
    }
}
