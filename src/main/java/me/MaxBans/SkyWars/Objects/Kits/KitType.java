/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Objects.Kits;

import org.bukkit.Material;

public enum KitType {


    Warrior("Warrior", Material.IRON_SWORD, new String[]{
            "",
            "§b§lRarity: §r§fRare",
            "",
            "§71x Iron Sword - Sharpness 1",
            "§71x Chainmail Chestplate",
            "§71x Leather Boots",
            "§74x Steak",
            "",
            "§e§lThis kit is Free!",
            "",
            "§3Left-click to select"},
            0),
    EndServant("EndServant", Material.ENDER_PEARL, new String[]{
            "",
            "§b§lRarity: §r§fRare",
            "",
            "§71x Golden Axe",
            "§71x Golden Helmet",
            "§71x Chainmail Boots - Feather Falling 1",
            "§72x Ender Pearl",
            "",
            "§e§lThis kit is Free!",
            "",
            "§3Left-click to select"},
            0),
    Hawkeye("Hawkeye", Material.BOW, new String[]{
            "",
            "§b§lRarity: §r§fRare",
            "",
            "§71x Bow",
            "§71x Leather Chestplate ",
            "§71x Leather Boots",
            "§78x Arrow",
            "§74x Apple",
            "",
            "§e§lThis kit is Free!",
            "",
            "§3Left-click to select"},
            0),
    Fisherman("Fisherman", Material.FISHING_ROD, new String[]{
            "",
            "§b§lRarity: §r§fRare",
            "",
            "§71x Fishing Rod - Knockback 1",
            "§71x Leather Chestplate",
            "§71x Leather Leggings",
            "§74x Cooked Fish",
            "",
            "§e§lThis kit is Free!",
            "",
            "§3Left-click to select"},
            0),
    Warlock("Warlock", Material.STONE_AXE, new String[]{
            "",
            "§b§lRarity: §r§fRare",
            "",
            "§71x Stone Axe",
            "§71x Leather Chestplate",
            "§71x Golden Boots",
            "§72x Splash Potion of Poison",
            "",
            "§7§lAbility:§r After 15s receive Speed effect for 60 seconds.",
            "",
            "§e§lThis kit is Free!",
            "",
            "§3Left-click to select"},
            0);


    private final String display;
    private final Material material;
    private final String[] description;
    private final int price;

    KitType(String display, Material material, String[] description, int price){
        this.display = display;
        this.material = material;
        this.description = description;
        this.price = price;
    }


    public String getDisplay() {
        return display;
    }

    public Material getMaterial() {
        return material;
    }

    public String[] getDescription() {
        return description;
    }

    public Integer getPrice(){
        return price;
    }
}
