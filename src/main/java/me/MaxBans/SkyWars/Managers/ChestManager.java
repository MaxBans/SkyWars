package me.MaxBans.SkyWars.Managers;

import me.MaxBans.SkyWars.GameState.GameState;
import me.MaxBans.SkyWars.Objects.LootItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ChestManager implements Listener {

    public static List<Location> middleChests = new ArrayList<>();
    private final Set<Location> openedChests = new HashSet<>();
    private final List<LootItem> lootItems = new ArrayList<>();
    private final List<LootItem> middleItems = new ArrayList<>();
    private final List<LootItem> ringItems = new ArrayList<>();
    private final ConfigManager configManager = new ConfigManager();

    public ChestManager(FileConfiguration lootConfig) {
        ConfigurationSection itemSection = lootConfig.getConfigurationSection("lootItems");
        ConfigurationSection middleSection = lootConfig.getConfigurationSection("middleItems");
        ConfigurationSection ringSection = lootConfig.getConfigurationSection("ringItems");


        if (itemSection == null) {
            Bukkit.getLogger().severe("Please setup 'loot items' in config.yml");
        }
        if (middleSection == null) {
            Bukkit.getLogger().severe("Please setup 'middle items' in config.yml");
        }
        if (ringSection == null) {
            Bukkit.getLogger().severe("Please setup 'ring items' in config.yml");
        }

        for (String key : itemSection.getKeys(false)) {
            ConfigurationSection section = itemSection.getConfigurationSection(key);
            lootItems.add(new LootItem(section));
        }

        for (String key : middleSection.getKeys(false)) {
            ConfigurationSection section = middleSection.getConfigurationSection(key);
            middleItems.add(new LootItem(section));
        }

        for (String key : ringSection.getKeys(false)) {
            ConfigurationSection section = ringSection.getConfigurationSection(key);
            ringItems.add(new LootItem(section));
        }
    }

    public static int getAmount(Inventory inventory) {
        int i = 0;
        for (ItemStack is : inventory.getContents()) {
            if (is != null) {
                i++;
            }
        }
        return i;
    }

    public static boolean isMiddleChest(Location loc) {
        return middleChests.contains(loc);
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof Chest) {
            String map = VotingManager.getWinner().getCustomName();
            if (map == null) return;

            Chest chest = (Chest) holder;
            if (!GameState.isState(GameState.PLAYING)) {
                e.setCancelled(true);
            }
            if (hasBeenOpened(chest.getLocation())) return;
            chest.getBlockInventory().clear();

            markAsOpened(chest.getLocation());
            for (int i = 0; i < configManager.getChestList(map).size(); i++) {
                String[] coords = getMiddleCoords(map, i).split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                int z = Integer.parseInt(coords[2]);
                Location wantedLocation = new Location(Bukkit.getWorld(VotingManager.getWinner().getBukkitWorld().getName()), x, y, z);
                if (chest.getLocation().equals(wantedLocation)) {
                    System.out.println("Middle Chest found! at" + chest.getLocation().getX() + "," + chest.getLocation().getY() + "," + chest.getLocation().getZ());
                    middleFill(chest.getBlockInventory());
                    middleChests.add(chest.getLocation());
                } else if (!isMiddleChest(chest.getLocation())) {
                    if (isInvEmpty(chest.getBlockInventory())) {
                        fill(chest.getBlockInventory());
                    }
                }
            }
            if (chest.getType() == Material.TRAPPED_CHEST) {
                ringFill(chest.getBlockInventory());
            }

        }
    }

    public void fill(Inventory inventory) {
        inventory.clear();

        ThreadLocalRandom random = ThreadLocalRandom.current();
        Set<LootItem> used = new HashSet<>();
        while (getAmount(inventory) < 4) {
            for (int slotIndex = 0; slotIndex < inventory.getSize(); slotIndex++) {
                for (int i = 0; i < 10; i++) {
                    LootItem randomItem = lootItems.get(random.nextInt(lootItems.size()));
                    if (getAmount(inventory) >= 6) return;

                    if (used.contains(randomItem)) continue;
                    used.add(randomItem);
                    if (randomItem.shouldFill(random)) {
                        ItemStack itemStack = randomItem.make(random);
                        int randomSlot = (int) Math.floor(Math.random() * (inventory.getSize()));
                        inventory.setItem(randomSlot, itemStack);
                    }
                }
            }
        }
    }


    public void middleFill(Inventory inventory) {
        inventory.clear();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Set<LootItem> used = new HashSet<>();
        while (getAmount(inventory) < 4) {
            for (int slotIndex = 0; slotIndex < inventory.getSize(); slotIndex++) {
                for (int i = 0; i < 10; i++) {
                    LootItem randomItem = middleItems.get(random.nextInt(middleItems.size()));
                    if (getAmount(inventory) >= 6) return;

                    if (used.contains(randomItem)) continue;
                    used.add(randomItem);

                    if (randomItem.shouldFill(random)) {
                        ItemStack itemStack = randomItem.make(random);
                        int randomSlot = (int) Math.floor(Math.random() * (inventory.getSize()));
                        inventory.setItem(randomSlot, itemStack);
                    }
                }
            }
        }

    }

    public void ringFill(Inventory inventory) {
        inventory.clear();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Set<LootItem> used = new HashSet<>();
        while (getAmount(inventory) < 4) {
            for (int slotIndex = 0; slotIndex < inventory.getSize(); slotIndex++) {
                for (int i = 0; i < 10; i++) {
                    LootItem randomItem = ringItems.get(random.nextInt(ringItems.size()));
                    if (getAmount(inventory) >= 6) return;

                    if (used.contains(randomItem)) continue;
                    used.add(randomItem);
                    if (randomItem.shouldFill(random)) {
                        ItemStack itemStack = randomItem.make(random);
                        int randomSlot = (int) Math.floor(Math.random() * (inventory.getSize()));
                        inventory.setItem(randomSlot, itemStack);
                    }
                }
            }

        }
    }

    public String getMiddleCoords(String map, int i) {
        return configManager.getChestList(map).get(i);
    }

    public void markAsOpened(Location location) {
        openedChests.add(location);
    }

    public boolean hasBeenOpened(Location location) {
        return openedChests.contains(location);
    }

    public void resetChests() {
        openedChests.clear();
    }

    public boolean isInvEmpty(Inventory inv) {
        for (ItemStack item : inv.getContents()) {
            if (item != null) {
                return false;
            }
        }
        return true;
    }

    public Location getCenter(Location loc) {
        return new Location(loc.getWorld(),
                getRelativeCoord(loc.getBlockX()),
                getRelativeCoord(loc.getBlockY()),
                getRelativeCoord(loc.getBlockZ()));
    }

    private double getRelativeCoord(int i) {
        double d = i;
        d = d < 0 ? d - .5 : d + .5;
        return d;
    }
}
