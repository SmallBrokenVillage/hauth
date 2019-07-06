package com.seybox.hauth.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class dataStorageHelper {
    private JavaPlugin plugin;

    public dataStorageHelper(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public boolean createDataStorage(String fileName) {
        if (!this.plugin.getDataFolder().exists()) {
            try {
                this.plugin.getDataFolder().mkdirs();
            } catch (Exception e) {
                this.plugin.getLogger().log(Level.ALL, e.getMessage());
                return false;
            }
        }
        if (this.plugin.getDataFolder().exists()) {
            File file = new File(this.plugin.getDataFolder(), fileName);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (Exception e) {
                this.plugin.getLogger().log(Level.ALL, e.getMessage());
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean setDataStorage(String fileName,String key, Object val){
        File file = new File(this.plugin.getDataFolder(),fileName);
        if(file.exists()){
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set(key,val);
            try{
                config.save(file);
            }catch (Exception e){
                this.plugin.getLogger().log(Level.ALL,e.getMessage());
                return false;
            }
        }
        return true;
    }

    public Object getDataStorage(String fileName, String key){
        File file = new File(this.plugin.getDataFolder(),fileName);
        if(file.exists()){
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            return config.get(key);
        }
        return null;
    }
}

