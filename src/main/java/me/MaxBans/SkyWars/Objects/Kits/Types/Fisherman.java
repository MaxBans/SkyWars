package me.MaxBans.SkyWars.Objects.Kits.Types;

import me.MaxBans.SkyWars.Objects.Kits.Kit;
import me.MaxBans.SkyWars.Objects.Kits.KitType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Fisherman extends Kit {

    public Fisherman(UUID uuid){
        super(uuid, KitType.Fisherman);
    }

    @Override
    public void onStart(Player player) {
        player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        Kit.addItems(player, 4, Material.COOKED_FISH);
    }
}
