package net.timroden.signedit.commands;

import net.timroden.signedit.Config;
import net.timroden.signedit.SignEdit;
import net.timroden.signedit.data.SignEditDataPackage;
import net.timroden.signedit.data.SignFunction;
import net.timroden.signedit.utils.SignEditUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MinecraftFont;

public class CommandSignEdit implements CommandExecutor {
	private final SignEdit plugin;
	private final SignEditUtils utils;

	public CommandSignEdit(SignEdit plugin) {
		this.plugin = plugin;
		this.utils = plugin.utils;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(plugin.chatPrefix + plugin.localization.get("syntaxError"));
			help(sender);
			return true;
		}

		if (args[0].equalsIgnoreCase("help")) {
			help(sender);
			return true;
		}

		if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("signedit.admin")) {
				sender.sendMessage(plugin.chatPrefix + plugin.localization.get("noReloadPermission"));
				return true;
			}
			plugin.config.reload();
			plugin.localization.loadLocales();
			sender.sendMessage(plugin.chatPrefix + plugin.localization.get("reloaded"));
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(plugin.chatPrefix + plugin.localization.get("noPlayer"));
			return true;
		}

		Player player = (Player) sender;
		String line = null;

		if (!player.hasPermission("signedit.edit")) {
			player.sendMessage(plugin.chatPrefix + plugin.localization.get("noSignEditPermission"));
			return true;
		}

		if (args[0].equalsIgnoreCase("cancel")) {
			// plugin.log.logAll(player.getName(), "cancel", LogType.PLAYERCOMMAND, Level.INFO);
			if (!plugin.playerData.containsKey(player.getName())) {
				player.sendMessage(plugin.chatPrefix + plugin.localization.get("noRequests"));
				return true;
			}

			plugin.playerData.remove(player.getName());
			player.sendMessage(plugin.chatPrefix + plugin.localization.get("requestCancelled"));
			return true;
		}
		if (args[0].equalsIgnoreCase("copy")) {
			if (!plugin.pasteAmounts.containsKey(player.getName())) {
				plugin.pasteAmounts.put(player.getName(), Integer.valueOf(1));
			}
			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("persist")) {
					// plugin.log.logAll(player.getName(), "copy persist", LogType.PLAYERCOMMAND, Level.INFO);
					SignEditDataPackage tmp = new SignEditDataPackage(player.getName(), SignFunction.COPYPERSIST);
					plugin.playerData.put(player.getName(), tmp);
					player.sendMessage(plugin.chatPrefix + plugin.localization.get("persistEnabled", new Object[] {
							utils.capitalize(plugin.config.clickActionStr())
					}));
					return true;
				}
				if (args[1].equalsIgnoreCase("default")) {
					if (!utils.isInt(args[2])) {
						player.sendMessage(plugin.chatPrefix + plugin.localization.get("invalidNumber", new Object[] {
								args[2]
						}));
						return true;
					}
					// plugin.log.logAll(player.getName(), "default " + args[2], LogType.PLAYERCOMMAND, Level.INFO);
					plugin.pasteAmounts.put(player.getName(), Integer.valueOf(Integer.parseInt(args[2])));
					player.sendMessage(plugin.chatPrefix + plugin.localization.get("defaultPastesChanged", new Object[] {
							args[2]
					}));
					return true;
				}
				// plugin.log.logAll(player.getName(), "copy " + args[1], LogType.PLAYERCOMMAND, Level.INFO);
				if (!utils.isInt(args[1])) {
					SignEditDataPackage tmp = new SignEditDataPackage(player.getName(), SignFunction.COPY, plugin.pasteAmounts.get(player.getName()).intValue());
					plugin.playerData.put(player.getName(), tmp);
					player.sendMessage(plugin.chatPrefix + plugin.localization.get("invalidNumber", new Object[] {
							args[1]
					}));
					player.sendMessage(plugin.chatPrefix + plugin.localization.get("defaultCopyPunch", new Object[] {
							utils.capitalize(plugin.config.clickActionStr()), plugin.pasteAmounts.get(player.getName()), plugin.pasteAmounts.get(player.getName()).intValue() == 1 ? plugin.localization.get("pasteCopyStr") : plugin.localization.get("pasteCopiesStr")
					}));
					return true;
				}
				SignEditDataPackage tmp = new SignEditDataPackage(player.getName(), SignFunction.COPY, Integer.parseInt(args[1]));
				plugin.playerData.put(player.getName(), tmp);
				player.sendMessage(plugin.chatPrefix + plugin.localization.get("intCopyPunch", new Object[] {
						utils.capitalize(plugin.config.clickActionStr()), args[1], Integer.parseInt(args[1]) == 1 ? plugin.localization.get("pasteCopyStr") : plugin.localization.get("pasteCopiesStr")
				}));
				return true;
			}

			// plugin.log.logAll(player.getName(), "copy", LogType.PLAYERCOMMAND, Level.INFO);
			int amt = plugin.pasteAmounts.get(player.getName()).intValue();
			SignEditDataPackage tmp = new SignEditDataPackage(player.getName(), SignFunction.COPY, amt);
			plugin.playerData.put(player.getName(), tmp);
			player.sendMessage(plugin.chatPrefix + plugin.localization.get("defaultCopyPunch", new Object[] {
					utils.capitalize(plugin.config.clickActionStr()), plugin.pasteAmounts.get(player.getName()), plugin.pasteAmounts.get(player.getName()).intValue() == 1 ? plugin.localization.get("pasteCopyStr") : plugin.localization.get("pasteCopiesStr")
			}));
			return true;
		}

		if (args.length >= 1) {
			if (!utils.isInt(args[0])) {
				player.sendMessage(plugin.chatPrefix + plugin.localization.get("invalidNumber", new Object[] {
						args[0]
				}));
				return true;
			}

			if (Integer.parseInt(args[0]) > 4 || Integer.parseInt(args[0]) < 1) {
				player.sendMessage(plugin.chatPrefix + plugin.localization.get("invalidLine", new Object[] {
						args[0]
				}));
				return true;
			}

			line = utils.implode(args, " ", 1, args.length);

			if (line == null) {
				// plugin.log.logAll(player.getName(), args[0], LogType.PLAYERCOMMAND, Level.INFO);
				SignEditDataPackage tmp = new SignEditDataPackage(player.getName(), SignFunction.EDIT, "", Integer.parseInt(args[0]) - 1);
				plugin.playerData.put(player.getName(), tmp);
				player.sendMessage(plugin.chatPrefix + plugin.localization.get("punchToComplete", new Object[] {
						utils.capitalize(plugin.config.clickActionStr())
				}));
				return true;
			}

			if (Config.truncateLines) {
				boolean truncated = false;
				MinecraftFont font = MinecraftFont.Font;

				while (font.getWidth(SignEditUtils.strip(line)) > 90) {
					// Take the last character off until it's a suitable width
					line = line.substring(0, line.length() - 1);
					truncated = true;
				}

				if (truncated) {
					player.sendMessage(plugin.chatPrefix + plugin.localization.get("truncating"));
				}
			}

			// plugin.log.logAll(player.getName(), utils.implode(args, " ", 0, args.length), LogType.PLAYERCOMMAND, Level.INFO);
			SignEditDataPackage tmp = new SignEditDataPackage(player.getName(), SignFunction.EDIT, line, Integer.parseInt(args[0]) - 1);
			plugin.playerData.put(player.getName(), tmp);
			player.sendMessage(plugin.chatPrefix + plugin.localization.get("punchToComplete", new Object[] {
					utils.capitalize(plugin.config.clickActionStr())
			}));
			return true;
		}
		sender.sendMessage(plugin.chatPrefix + plugin.localization.get("syntaxError"));
		help(sender);
		return true;
	}

	public void help(CommandSender sender) {
		sender.sendMessage(plugin.chatPrefix + plugin.localization.get("commandsAvailable"));
		if (sender instanceof Player) {
			sender.sendMessage(plugin.chatPrefix + plugin.localization.get("commandsHeader", new Object[] {
					plugin.config.clickActionStr()
			}));
			sender.sendMessage(ChatColor.GRAY + " - " + plugin.localization.get("commandsDef"));
			sender.sendMessage(ChatColor.GRAY + " - " + plugin.localization.get("commandsCancel"));
			sender.sendMessage(ChatColor.GRAY + " - " + plugin.localization.get("commandsCopyAmount"));
			sender.sendMessage(ChatColor.GRAY + " - " + plugin.localization.get("commandsCopyPersist"));
			sender.sendMessage(ChatColor.GRAY + " - " + plugin.localization.get("commandsCopyDefault"));
		}
		if (sender.hasPermission("signedit.admin")) {
			sender.sendMessage(ChatColor.GRAY + " - " + plugin.localization.get("commandsReload"));
		}
		sender.sendMessage(ChatColor.GRAY + " - " + plugin.localization.get("commandsHelp"));
	}
}