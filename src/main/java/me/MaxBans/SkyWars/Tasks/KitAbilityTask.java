/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Tasks;

import me.MaxBans.SkyWars.Managers.GameManager;
import me.MaxBans.SkyWars.Managers.PlayerManager;
import me.MaxBans.SkyWars.Objects.Kits.Kit;
import me.MaxBans.SkyWars.Objects.Kits.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class KitAbilityTask extends BukkitRunnable {
    public static int timeLeft = 15;

    public KitAbilityTask() {

    }

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(GameManager.hasKit(p)){
                p.setLevel(timeLeft);
            }
        }
        System.out.println(timeLeft);

        if(timeLeft == 15) {
            setCooldown();
        }
        else if (timeLeft == 0) {
            removeCooldown();
        }else if(timeLeft <= 0){
            cancel();
        }
        timeLeft = timeLeft - 1;

    }

    public void setCooldown(){
        for(Player p : Bukkit.getOnlinePlayers()){
            KitType kit = GameManager.getKit(p);
            if(!GameManager.hasKit(p)) continue;
            switch (kit){
                case Warrior:
                    p.sendMessage("§8[§3Kits§8] §7Your §bSword§7 will be given in §b15§7 seconds!");
                    break;
                case EndServant:
                    p.sendMessage("§8[§3Kits§8] §7You will be given §bEnder Pearls§7 in §b15§7 seconds!");
                    break;
                case Hawkeye:
                    p.sendMessage("§8[§3Kits§8] §7You will be given §bArrows§7 in §b15§7 seconds!");
                    break;
                case Fisherman:
                    p.sendMessage("§8[§3Kits§8] §7You will be given §bFishing Rod§7 in §b15§7 seconds!");
                    break;
                case Warlock:
                    p.sendMessage("§8[§3Kits§8] §7You will receive your §bSpeed§7 in 15 seconds!");
                    break;
            }
        }
    }

    public void removeCooldown(){
        for(Player p : Bukkit.getOnlinePlayers()){
            KitType kit = GameManager.getKit(p);
            if(!GameManager.hasKit(p)) continue;
            if(PlayerManager.deadPlayers.contains(p.getUniqueId())) return;
            if(PlayerManager.spectators.contains(p.getUniqueId())) return;

            switch (kit) {
                case Warrior:
                    ItemStack sword = new ItemStack(Material.IRON_SWORD);
                    ItemMeta meta1 = sword.getItemMeta();
                    meta1.setDisplayName("Warrior's sword");
                    meta1.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                    sword.setItemMeta(meta1);
                    p.getInventory().addItem(sword);
                    p.sendMessage("§8[§3Kits§8] §7You can now use your §bSpecial Sword§7!");
                    break;
                case EndServant:
                    p.sendMessage("§8[§3Kits§8] §7You can now use your §bEnder Pearls§7!");
                    ItemStack pearl = new ItemStack(Material.ENDER_PEARL);
                    ItemMeta meta = pearl.getItemMeta();
                    meta.setDisplayName("§6Servant's Tool");
                    pearl.setItemMeta(meta);
                    pearl.setAmount(2);
                    p.getInventory().addItem(pearl);
                    break;
                case Hawkeye:
                    p.sendMessage("§8[§3Kits§8] §7You were given §b8 Arrows§7!");
                    Kit.addItems(p, 8, Material.ARROW);
                    break;
                case Warlock:
                    p.sendMessage("§8[§3Kits§8] §7You received your §bSpeed§7 for 60 seconds!");
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60, 2));
                    break;
                case Fisherman:
                    ItemStack item = new ItemStack(Material.FISHING_ROD);
                    item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                    p.getInventory().addItem(item);
                    p.sendMessage("§8[§3Kits§8] §7You can now use your §bFishing Rod§7!");
                    break;
            }
        }
    }
}

