/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Objects;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class LootItem {

    private final Material material;
    private final String customName;
    private final Map<Enchantment, Integer> enchantmentToLevelMap = new HashMap<>();
    private final double chance;

    private final int minAmount;
    private final int maxAmount;

    private boolean isPotion;
    private Map<PotionType, Integer> potionEffectToLevelMap = new HashMap<>();
    private boolean isSplash;
    private int time;

    public LootItem(ConfigurationSection section){
        Material material;

        try{
            material = Material.valueOf(section.getString("material"));
        }catch (Exception e){
            material = Material.BARRIER;
        }

        this.material = material;
        this.customName = section.getString("name");
        this.isPotion = section.getBoolean("isPotion");

        ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
        if (enchantmentsSection != null) {
            for (String enchantmentKey : enchantmentsSection.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByName(enchantmentKey.toUpperCase());

                if (enchantment != null) {
                    int level = enchantmentsSection.getInt(enchantmentKey);
                    enchantmentToLevelMap.put(enchantment, level);
                }
            }
        }

        if(isPotion){
            this.isSplash = section.getBoolean("isSplash");
            ConfigurationSection potionSection = section.getConfigurationSection("effect");
                if(potionSection != null){
                    for(String key : potionSection.getKeys(false)){
                        PotionType effect = PotionType.valueOf(key);
                        int level = potionSection.getInt(key);
                        potionEffectToLevelMap.put(effect, level);
                    }
                }
            if(section.getConfigurationSection("time") != null) {
                this.time = section.getInt("time");
            }

        }

        this.chance = section.getDouble("chance");
        this.minAmount = section.getInt("minAmount");
        this.maxAmount = section.getInt("maxAmount");
    }

    public boolean shouldFill(Random random) {
        return random.nextDouble() <= chance;
    }

    public ItemStack make(ThreadLocalRandom random){
        if(isPotion){
           ItemStack potion = new ItemStack(Material.POTION, random.nextInt(minAmount, maxAmount + 1));
            Potion pot = new Potion(1);
           if(!potionEffectToLevelMap.isEmpty()){
               for(Map.Entry<PotionType, Integer> potionEntry : potionEffectToLevelMap.entrySet()){
                   pot = new Potion(potionEntry.getKey(), potionEntry.getValue());
                   pot.setSplash(isSplash);
               }
           }
           pot.apply(potion);
            return potion;
        }else {
            int amount = random.nextInt(minAmount, maxAmount + 1);
            ItemStack itemStack = new ItemStack(material, amount);
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (customName != null) {
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', customName));
            }

            if (!enchantmentToLevelMap.isEmpty()) {
                for (Map.Entry<Enchantment, Integer> enchantEntry : enchantmentToLevelMap.entrySet()) {
                    itemMeta.addEnchant(
                            enchantEntry.getKey(),
                            enchantEntry.getValue(),
                            true
                    );
                }
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }

    }
}
