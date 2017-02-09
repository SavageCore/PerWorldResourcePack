package eu.savagecore.perworldresourcepack;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PerWorldResourcePack extends JavaPlugin implements Listener {

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getCommand("pwrp").setExecutor(new PerWorldResourcePackCommandExecutor(this));
        saveDefaultConfig();
        if (!getConfig().isSet("debug")) {
            this.getConfig().set("debug", false);
        }
        saveConfig();
        if (getConfig().getBoolean("debug")) {
            getLogger().info("PerWorldResourcePack enabled.");
        }
    }

    public void onDisable() {
        if (getConfig().getBoolean("debug")) {
            getLogger().info("PerWorldResourcePack disabled.");
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        updateResourcePack(event.getPlayer().getWorld().getName(), event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        updateResourcePack(event.getPlayer().getWorld().getName(), event.getPlayer());
    }

    // Debug event
    @EventHandler
    public void onPlayerResourcePackStatusEvent(PlayerResourcePackStatusEvent event) {
        if (getConfig().getBoolean("debug")) {
            getLogger().info(event.getStatus().name());
        }
    }

    public void updateResourcePack(String world, Player player) {
        String worldPermissionString = "worlds." + world;
        if (getConfig().isSet(worldPermissionString)) {
            if (getConfig().getBoolean("debug")) {
                getLogger().info("Set resource pack to: " + getConfig().getString(worldPermissionString));
            }
            player.setResourcePack(getConfig().getString(worldPermissionString));
        } else {
            if (getConfig().getBoolean("debug")) {
                getLogger().info("Set resource pack to default (" + getConfig().getString("default") + ")");
            }
            player.setResourcePack(getConfig().getString("default"));
        }
    }
    
    public void saveDefaultConfig() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", false);
        }
    }    
}
