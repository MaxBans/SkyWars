package me.MaxBans.SkyWars.Maps;

import me.MaxBans.SkyWars.SkyWarsPlugin;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum GameMap implements FileMap  {

    Atlantis(Material.PRISMARINE_SHARD, "Atlantis", "ChickenDevil, Nikiasek", "Atlantis", 0, SkyWarsPlugin.getInstance().getMapsFolder(), false),
    PumpkinKingdom(Material.PUMPKIN, "PumpkinKingdom", "ChickenDevil, Nikiasek", "PumpkinKingdom", 1, SkyWarsPlugin.getInstance().getMapsFolder(), false),
    Yggdrasil(Material.SAPLING, "Yggdrasil", "Demthel, Carraso", "Yggdrasil", 2, SkyWarsPlugin.getInstance().getMapsFolder(), false);


    private final Material material;
    private final String customName;
    private final String builder;
    private final Map<UUID, GameMap> voted = new HashMap<>();
    private final File sourceWorldFolder;
    private File activeWorldFolder;

    private World bukkitWorld;



    private World world;

    private final int slot;


     GameMap(Material material, String customName, String builder, String world, int slot, File worldFolder, boolean loadOnInit){
         this.material = material;
         this.customName = customName;
         this.builder = builder;
         this.slot = slot;
         this.world = Bukkit.getWorld(world);

         this.sourceWorldFolder = new File(worldFolder, world);

         if(loadOnInit) load();

     }

     public boolean load(){
         if(isLoaded()) return true;

         this.activeWorldFolder = new File(Bukkit.getWorldContainer().getParentFile(), sourceWorldFolder.getName() + "_active_" + System.currentTimeMillis());

         try{
             FileUtil.copy(sourceWorldFolder, activeWorldFolder);
         }catch (IOException e){
             Bukkit.getLogger().severe("Failed to laod Map from source folder " + sourceWorldFolder.getName());
             e.printStackTrace();
             return false;
         }

         this.bukkitWorld = Bukkit.createWorld(new WorldCreator(activeWorldFolder.getName()));
         this.bukkitWorld.setDifficulty(Difficulty.PEACEFUL);
         this.bukkitWorld.setAnimalSpawnLimit(0);
         if(bukkitWorld != null) this.bukkitWorld.setAutoSave(false);
         return isLoaded();
     }

     public void unload(){
         if(bukkitWorld != null) Bukkit.unloadWorld(bukkitWorld, false);
         if(activeWorldFolder != null){
             try {
                 FileUtil.delete(activeWorldFolder);
                 System.out.println(activeWorldFolder.getName() + " Was succesfully reset!");
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }


         bukkitWorld = null;
         activeWorldFolder = null;
     }

     public boolean restoreFromSource(){
         unload();
         return load();
     }
     public boolean isLoaded(){
         return this.bukkitWorld != null;
     }

    public Material getMaterial() {
        return material;
    }

    public String getCustomName() {
        return customName;
    }

    public String getBuilder() {
        return builder;
    }

    public World getWorld() {
        return world;
    }

    public World getBukkitWorld(){
         return bukkitWorld;
    }

    public int getSlot() {
        return slot;
    }

    public Map<UUID, GameMap> getVoted() {
        return voted;
    }

}
