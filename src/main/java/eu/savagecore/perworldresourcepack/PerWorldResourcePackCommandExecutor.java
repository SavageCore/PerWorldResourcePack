package eu.savagecore.perworldresourcepack;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PerWorldResourcePackCommandExecutor implements CommandExecutor {
	private final PerWorldResourcePack plugin;

	private String ChatPrefix = "[" + ChatColor.GREEN + "PerWorldResourcePack" + ChatColor.RESET + "]";

	public PerWorldResourcePackCommandExecutor(PerWorldResourcePack plugin) {
		this.plugin = plugin; // Store the plugin in situations where you need
								// it.
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("pwrp")) {
			// If no arguments set return
			if (args.length < 1 || args.length > 4) {
				sender.sendMessage(ChatPrefix + " Usage:");
				sender.sendMessage("/pwrp help");
				sender.sendMessage("/pwrp clear <" + ChatColor.YELLOW + "world_name" + ChatColor.RESET + ">");
				sender.sendMessage("/pwrp debug <true|false>");
				sender.sendMessage("/pwrp set default " + ChatColor.RED + "url" + ChatColor.RESET);
				sender.sendMessage("/pwrp set world " + ChatColor.RED + "url" + ChatColor.RESET + " <"
						+ ChatColor.YELLOW + "world_name" + ChatColor.RESET + ">");
				return false;
			}

			// Reload configuration before commands run
			plugin.reloadConfig();

			// If default resource pack not set or contains default value then
			// return unless trying to set
			if ((plugin.getConfig().getString("default").equals("http://example.com/default_resourcepack.zip")
					|| !plugin.getConfig().isSet("default")) && !args[0].equalsIgnoreCase("set")) {
				sender.sendMessage(ChatPrefix + " You must set default Resource Pack");
				sender.sendMessage("/pwrp set default " + ChatColor.RED + "url" + ChatColor.RESET);
				return false;
			}

			// Clear command
			if (args[0].equalsIgnoreCase("clear")) {
				if (!sender.hasPermission("perworldresourcepack.clear")) {
					sender.sendMessage(ChatPrefix + " You do not have permission -");
					sender.sendMessage(ChatColor.YELLOW + "perworldresourcepack.clear");
					return false;
				}
				if (args.length < 1 || args.length > 2) {
					sender.sendMessage(ChatPrefix + " Usage:");
					sender.sendMessage("/pwrp clear <" + ChatColor.YELLOW + "world_name" + ChatColor.RESET + ">");
					return false;
				}
				String WorldToClear;
				if (args.length == 2) {
					WorldToClear = args[1];
				} else {
					WorldToClear = Bukkit.getServer().getPlayer(sender.getName()).getWorld().getName();
				}
				if (Bukkit.getWorld(WorldToClear) == null) {
					sender.sendMessage(ChatPrefix + " World: " + ChatColor.YELLOW + WorldToClear + ChatColor.RESET
							+ " does not exist");
					sender.sendMessage("Usage:");
					sender.sendMessage("/pwrp clear <" + ChatColor.YELLOW + "world_name" + ChatColor.RESET + ">");
					return false;
				}
				plugin.getConfig().set("worlds." + WorldToClear, null);
				plugin.saveConfig();
				sender.sendMessage(ChatPrefix + " Cleared resource pack of world " + ChatColor.YELLOW + WorldToClear);

				// If in same world as cleared then update resource pack
				if (sender instanceof Player) {
					if (Bukkit.getServer().getPlayer(sender.getName()).getWorld().getName().equals(WorldToClear)) {
						plugin.updateResourcePack(Bukkit.getServer().getPlayer(sender.getName()).getWorld().getName(),
								Bukkit.getServer().getPlayer(sender.getName()));
					}
				}
				return true;
			}

			// Debug command
			if (args[0].equalsIgnoreCase("debug")) {
				if (!sender.hasPermission("perworldresourcepack.debug")) {
					sender.sendMessage(ChatPrefix + " You do not have permission -");
					sender.sendMessage(ChatColor.YELLOW + "perworldresourcepack.debug");
					return false;
				}
				Boolean DebugToggle;
				if (args.length == 2) {
					if (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
						sender.sendMessage(ChatPrefix + " Invalid argument (" + ChatColor.YELLOW + args[1]
								+ ChatColor.RESET + ") -");
						sender.sendMessage("/pwrp debug <true|false>");
						return false;
					}
					DebugToggle = Boolean.valueOf(args[1]);
				} else {
					DebugToggle = !plugin.getConfig().getBoolean("debug");
				}
				sender.sendMessage(ChatPrefix + " Set debug mode to " + ChatColor.YELLOW + DebugToggle);
				plugin.getConfig().set("debug", DebugToggle);
				plugin.saveConfig();
				return true;
			}

			// Help command
			if (args[0].equalsIgnoreCase("help")) {
				if (!sender.hasPermission("perworldresourcepack.help")) {
					sender.sendMessage(ChatPrefix + " You do not have permission -");
					sender.sendMessage(ChatColor.YELLOW + "perworldresourcepack.help");
					return false;
				}
				sender.sendMessage(ChatPrefix + " Usage:");
				sender.sendMessage("/pwrp help");
				sender.sendMessage("/pwrp clear <" + ChatColor.YELLOW + "world_name" + ChatColor.RESET + ">");
				sender.sendMessage("/pwrp debug <true|false>");
				sender.sendMessage("/pwrp set default " + ChatColor.RED + "url" + ChatColor.RESET);
				sender.sendMessage("/pwrp set world " + ChatColor.RED + "url" + ChatColor.RESET + " <"
						+ ChatColor.YELLOW + "world_name" + ChatColor.RESET + ">");
				return false;
			}

			// Set command
			if (args[0].equalsIgnoreCase("set")) {
				// Less than 3 arguments return
				if (args.length < 3) {
					sender.sendMessage(ChatPrefix + " Usage:");
					sender.sendMessage("/pwrp set default " + ChatColor.RED + "url" + ChatColor.RESET);
					sender.sendMessage("/pwrp set world " + ChatColor.RED + "url" + ChatColor.RESET + " <"
							+ ChatColor.YELLOW + "world_name" + ChatColor.RESET + ">");
					return false;
				}

				// Set default command
				if (args[1].equalsIgnoreCase("default")) {
					if (!sender.hasPermission("perworldresourcepack.set.default")) {
						sender.sendMessage(ChatPrefix + " You do not have permission -");
						sender.sendMessage(ChatColor.YELLOW + "perworldresourcepack.set.default");
						return false;
					}
					try {
						URL url = new URL(args[2]);
						URLConnection conn = url.openConnection();
						conn.connect();
					} catch (MalformedURLException e) {
						sender.sendMessage(ChatPrefix + " url invalid!");
						sender.sendMessage("Usage:");
						sender.sendMessage("/pwrp set default " + ChatColor.RED + "url" + ChatColor.RESET + "");
						return false;
					} catch (IOException e) {
						sender.sendMessage(ChatPrefix + " Connection could not be established!");
						sender.sendMessage("Usage:");
						sender.sendMessage("/pwrp set default " + ChatColor.RED + "url" + ChatColor.RESET + "");
						return false;
					}
					plugin.getConfig().set("default", args[2]);
					plugin.saveConfig();
					if (plugin.getConfig().getBoolean("debug")) {
						plugin.getLogger().info("Set default to " + args[2]);
					}
					sender.sendMessage(ChatPrefix + " Set default resource pack to " + ChatColor.GREEN + args[2]);

					// If current world using default resource pack then
					// updateResourcePack for user
					if (sender instanceof Player) {
						String currentWorld = Bukkit.getServer().getPlayer(sender.getName()).getWorld().getName();
						String worldPermissionString = "worlds." + currentWorld;

						if (!plugin.getConfig().isSet(worldPermissionString) && Bukkit.getServer()
								.getPlayer(sender.getName()).getWorld().getName().equals(currentWorld)) {
							sender.sendMessage(ChatPrefix + " Set resource pack to " + ChatColor.GREEN + args[2]);
							plugin.updateResourcePack(args[2], Bukkit.getServer().getPlayer(sender.getName()));
						}
					}
					return true;
				}
				// Set world command
				if (args[1].equalsIgnoreCase("world")) {
					String WorldToSet;
					if (args.length == 4) {
						WorldToSet = args[3];
					} else {
						WorldToSet = Bukkit.getServer().getPlayer(sender.getName()).getWorld().getName();
					}
					if ((!sender.hasPermission("perworldresourcepack.set.world.*")
							&& !sender.hasPermission("perworldresourcepack.set.world." + WorldToSet))
							|| (sender.hasPermission("perworldresourcepack.set.world.*")
									&& !sender.hasPermission("perworldresourcepack.set.world." + WorldToSet))) {
						sender.sendMessage(ChatPrefix + " You do not have permission -");
						sender.sendMessage(ChatColor.YELLOW + "perworldresourcepack.set.world.*");
						sender.sendMessage(ChatColor.YELLOW + "perworldresourcepack.set.world." + WorldToSet);
						return false;
					}
					if (args.length < 3 || args.length > 4) {
						sender.sendMessage(ChatPrefix + " Usage:");
						sender.sendMessage("/pwrp set world " + ChatColor.RED + "url" + ChatColor.RESET + " <"
								+ ChatColor.YELLOW + "world_name" + ChatColor.RESET + ">");
						return false;
					}
					if (Bukkit.getWorld(WorldToSet) == null) {
						sender.sendMessage(ChatPrefix + " World: " + ChatColor.YELLOW + WorldToSet + ChatColor.RESET
								+ " does not exist");
						sender.sendMessage("Usage:");
						sender.sendMessage(
								"/pwrp set world url <" + ChatColor.YELLOW + "world_name" + ChatColor.RESET + ">");
						return false;
					}
					try {
						URL url = new URL(args[2]);
						URLConnection conn = url.openConnection();
						conn.connect();
					} catch (MalformedURLException e) {
						sender.sendMessage(ChatPrefix + " url invalid!");
						sender.sendMessage("Usage:");
						sender.sendMessage(
								"/pwrp set world url <" + ChatColor.YELLOW + "world_name" + ChatColor.RESET + ">");
						return false;
					} catch (IOException e) {
						sender.sendMessage(ChatPrefix + " Connection could not be established!");
						sender.sendMessage("Usage:");
						sender.sendMessage(
								"/pwrp set world url <" + ChatColor.YELLOW + "world_name" + ChatColor.RESET + ">");
						return false;
					}
					plugin.getConfig().set("worlds." + WorldToSet, args[2]);
					plugin.saveConfig();
					if (plugin.getConfig().getBoolean("debug")) {
						plugin.getLogger().info("Set world (" + WorldToSet + ") to " + args[2]);
					}
					sender.sendMessage(ChatPrefix + " Set resource pack of world " + ChatColor.YELLOW + WorldToSet
							+ ChatColor.RESET + " to " + ChatColor.GREEN + args[2]);

					// If in same world as set then update resource pack
					if (Bukkit.getServer().getPlayer(sender.getName()).getWorld().getName().equals(WorldToSet)) {
						plugin.updateResourcePack(Bukkit.getServer().getPlayer(sender.getName()).getWorld().getName(),
								Bukkit.getServer().getPlayer(sender.getName()));
					}
					return true;
				}
			}
		}
		return false;
	}
}