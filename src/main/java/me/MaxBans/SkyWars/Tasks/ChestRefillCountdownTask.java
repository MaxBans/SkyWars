/**
 * Copyright (c) 2021 Filip Crhonek
 */

package me.MaxBans.SkyWars.Tasks;

import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.Managers.ChestManager;
import me.MaxBans.SkyWars.Managers.ConfigManager;
import me.MaxBans.SkyWars.Managers.VotingManager;
import me.MaxBans.SkyWars.SkyWarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChestRefillCountdownTask extends BukkitRunnable {

    public static int timeLeft = 120;
    private final GameState gameState;
    private final ChestManager chestManager = new ChestManager(SkyWarsPlugin.getInstance().getConfig());
    private final ConfigManager configManager = new ConfigManager();

    public ChestRefillCountdownTask(GameState gameState) {
        this.gameState = gameState;
    }

    public static String getTitle() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        String str = String.format("%d:%02d", minutes, seconds);
        return str;
    }

    @Override
    public void run() {
        if (GameState.isState(GameState.STARTING) || GameState.isState(GameState.LOBBY) || GameState.isState(GameState.WINNER)) {

            return;
        }
        timeLeft--;
        if (timeLeft == 0) {
            chestManager.resetChests();
            for (Chunk c : Bukkit.getWorld(configManager.getGameWorld().getName()).getLoadedChunks()) {
                for (BlockState b : c.getTileEntities()) {
                    if (b instanceof Chest) {
                        Chest chest = (Chest) b;
                        if (chestManager.isMiddleChest(chest.getLocation())) {
                            for (int i = 0; i < configManager.getChestList(VotingManager.getWinner().getCustomName()).size(); i++) {
                                String[] coords = chestManager.getMiddleCoords(VotingManager.getWinner().getCustomName(), i).split(",");
                                int x = Integer.parseInt(coords[0]);
                                int y = Integer.parseInt(coords[1]);
                                int z = Integer.parseInt(coords[2]);
                                Location wantedLocation = new Location(Bukkit.getWorld(configManager.getGameWorld().getName()), x, y, z);
                                if (chest.getLocation().equals(wantedLocation)) {
                                    System.out.println("Middle Chest found! at" + chest.getLocation().getX() + "," + chest.getLocation().getY() + "," + chest.getLocation().getZ());
                                    chestManager.middleFill(chest.getBlockInventory());
                                    chestManager.middleChests.add(chest.getLocation());
                                }
                            }
                        } else if (!chestManager.isMiddleChest(chest.getLocation())) {
                            if(chest.getType() != Material.TRAPPED_CHEST) {
                                chestManager.fill(chest.getBlockInventory());
                            }else{
                                chestManager.ringFill(chest.getBlockInventory());
                                System.out.println("This is ring chest!");
                            }
                        }
                    }

                }

            }
            timeLeft = 120;

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage("§8[§3SkyWars§8] §bAll chests were refilled");
            }
            return;
        }


    }


}
