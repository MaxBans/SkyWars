package me.MaxBans.SkyWars.Objects.Kits.Types;

import me.MaxBans.SkyWars.Objects.Kits.Kit;
import me.MaxBans.SkyWars.Objects.Kits.KitType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Hawkeye extends Kit {

    public Hawkeye(UUID uuid){
        super(uuid, KitType.Hawkeye);
    }

    @Override
    public void onStart(Player player) {
        Kit.addItems(player, 1, Material.BOW);
        player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
        Kit.addItems(player, 4, Material.APPLE);
    }
}
