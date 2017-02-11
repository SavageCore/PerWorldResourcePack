package eu.savagecore.perworldresourcepack.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import eu.savagecore.perworldresourcepack.PerWorldResourcePack;

public class Logger extends PerWorldResourcePack {

	public static void Log(PerWorldResourcePack plugin, String msg) {
		
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

		if (!plugin.getConfig().getBoolean("debug")) {
			return;
		}
		console.sendMessage(plugin.getChatPrefix() + " [DEBUG] " + msg);
	}

}
