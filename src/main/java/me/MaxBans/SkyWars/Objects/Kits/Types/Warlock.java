package me.MaxBans.SkyWars.Objects.Kits.Types;

import me.MaxBans.SkyWars.Objects.Kits.Kit;
import me.MaxBans.SkyWars.Objects.Kits.KitType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.UUID;

public class Warlock extends Kit{

    public Warlock(UUID uuid){
        super(uuid, KitType.Warlock);
    }

    @Override
    public void onStart(Player player) {
        player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        player.getInventory().setBoots(new ItemStack(Material.GOLD_BOOTS));
        Kit.addItems(player, 1, Material.STONE_AXE);
        Potion splash = new Potion(PotionType.POISON, 1);//poison 1
        splash.setSplash(true);
        player.getInventory().addItem(splash.toItemStack(2));
    }
}
