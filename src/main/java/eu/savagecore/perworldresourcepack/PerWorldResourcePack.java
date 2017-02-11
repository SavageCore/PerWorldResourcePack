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

import eu.savagecore.perworldresourcepack.utils.Logger;

public class PerWorldResourcePack extends JavaPlugin implements Listener {

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		this.getCommand("pwrp").setExecutor(new PerWorldResourcePackCommandExecutor(this));
		saveDefaultConfig();
		if (!getConfig().isSet("debug")) {
			this.getConfig().set("debug", false);
			saveConfig();
		}
		Logger.Log(this, String.format("Version %s - Enabled!", this.getDescription().getVersion()));
	}

	public void onDisable() {
		Logger.Log(this, String.format("Version %s - Disabled.", this.getDescription().getVersion()));
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
		Logger.Log(this, String.format("%s%s%s (%s%s%s): %s", ChatColor.RED, event.getPlayer().getName(), ChatColor.RESET, ChatColor.YELLOW, event.getPlayer().getWorld().getName(), ChatColor.RESET, event.getStatus().name()));
	}

	public void updateResourcePack(String world, Player player) {
		if (player.hasPermission("perworldresourcepack.bypass.world." + world)
				|| player.hasPermission("perworldresourcepack.bypass.world.*")) {
			Logger.Log(this, String.format("Bypass: %s%s%s in %s%s%s", ChatColor.BLUE, player.getName(),
					ChatColor.RESET, ChatColor.YELLOW, world, ChatColor.RESET));
			return;
		}
		String worldPermissionString = "worlds." + world;
		if (getConfig().isSet(worldPermissionString)) {
			player.setResourcePack(getConfig().getString(worldPermissionString));
		} else {
			player.setResourcePack(getConfig().getString("default"));
		}
	}

	public String getChatPrefix() {
		return "[" + ChatColor.GREEN + "PerWorldResourcePack" + ChatColor.RESET + "]";
	}
}
