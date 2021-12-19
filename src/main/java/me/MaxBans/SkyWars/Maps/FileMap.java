package me.MaxBans.SkyWars.Maps;

import org.bukkit.World;

public interface FileMap {

    boolean load();
    void unload();
    boolean restoreFromSource();

    boolean isLoaded();
    World getWorld();
}
