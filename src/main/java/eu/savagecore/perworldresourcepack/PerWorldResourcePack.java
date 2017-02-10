package eu.savagecore.perworldresourcepack;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
			saveConfig();
		}
		if (getConfig().getBoolean("debug")) {
			getLogger().info("Version " + this.getDescription().getVersion() + " - Enabled!");
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
		if (player.hasPermission("perworldresourcepack.bypass.world." + world)
				|| player.hasPermission("perworldresourcepack.bypass.world.*")) {
			if (getConfig().getBoolean("debug")) {
				getServer().getConsoleSender().sendMessage(
						this.getChatPrefix() + " Bypass resource pack [" + ChatColor.GREEN + player.getName()
								+ ChatColor.RESET + "/" + ChatColor.YELLOW + world + ChatColor.RESET + "]");
			}
			return;
		}
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

	public String getChatPrefix() {
		return "[" + ChatColor.GREEN + "PerWorldResourcePack" + ChatColor.RESET + "]";
	}
}
