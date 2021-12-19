package me.MaxBans.SkyWars.Objects.Kits.Types;

import me.MaxBans.SkyWars.Objects.Kits.Kit;
import me.MaxBans.SkyWars.Objects.Kits.KitType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EndServant extends Kit {

    public EndServant(UUID uuid){
        super(uuid, KitType.EndServant);
    }

    @Override
    public void onStart(Player player) {
        Kit.addItems(player, 1, Material.GOLD_AXE);
        player.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET));
        player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
    }
}
