package me.MaxBans.SkyWars.Listeners;

import maxbans.hcurrencyapi.GamePlayer;
import me.MaxBans.SkyWars.Managers.GameManager;
import me.MaxBans.SkyWars.Managers.PlayerManager;
import me.MaxBans.SkyWars.Managers.VotingManager;
import me.MaxBans.SkyWars.Maps.GameMap;
import me.MaxBans.SkyWars.Objects.Kits.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();
        GamePlayer gamePlayer = new GamePlayer(player);
        if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        else if(e.getView().getTitle().contains("Select kit") && e.getRawSlot() <= 18){
            KitType type = KitType.valueOf(e.getCurrentItem().getItemMeta().getDisplayName());
            if(GameManager.hasKit(player) && GameManager.getKit(player).equals(type)){
                player.sendMessage("§8[§3Kits§8] §7You have already equipped this kit!");
            }else if(gamePlayer.getCoins() >= type.getPrice()){
                player.sendMessage("§8[§3Kits§8] §7You have equipped kit §b" + type.getDisplay());
                player.sendMessage("§8[§3Kits§8] §eCoins will be deducted after the start of the game");
                GameManager.setKit(player.getUniqueId(), type);
            }else{
                player.sendMessage("§8[§3Kits§8] §cYou dont have enough coins");
            }
            e.setCancelled(true);
            player.closeInventory();

        }
    }

    @EventHandler
    public void onVoteClick(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();

        if(e.getView().getTitle().contains("Vote for map") && e.getRawSlot() <= 9 && e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
            String k = e.getCurrentItem().getItemMeta().getDisplayName();

            for(GameMap map : GameMap.values()){
                if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

                if(k.equals(map.getCustomName())){
                    VotingManager.vote(map, player);
                }
            }
            e.setCancelled(true);
            player.closeInventory();

        }
    }

    @EventHandler
    public void onTrackerClick(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();

        if(e.getView().getTitle().equals("§7Teleport to player") && e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
            String name = e.getCurrentItem().getItemMeta().getDisplayName();

            for(UUID uuid : PlayerManager.alivePlayers){
                Player pl = Bukkit.getPlayer(uuid);
                if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

                if(name.equals(pl.getName()) && pl.isOnline()){
                    player.teleport(pl);
                }
            }
            e.setCancelled(true);
            player.closeInventory();

        }
    }

    @EventHandler
    public void onLeaveClick(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();

        if(e.getView().getTitle().equals("§cReturn to lobby!") && e.getCurrentItem() != null){
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lobby");
            e.setCancelled(true);
            player.closeInventory();
            }
        }
    }

