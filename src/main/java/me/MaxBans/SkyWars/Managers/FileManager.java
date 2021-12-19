package me.MaxBans.SkyWars.Managers;

import me.MaxBans.SkyWars.SkyWarsPlugin;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private SkyWarsPlugin plugin;
    public FileManager(SkyWarsPlugin plugin){
        this.plugin = plugin;
    }

    public void createFile(String name){
        File file = new File(plugin.getDirectory(), name + ".yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getFile(String name) {
        File file = new File(plugin.getDirectory(), name + ".yml");
        return file;
    }
}
