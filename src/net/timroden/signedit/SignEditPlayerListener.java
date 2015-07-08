package net.timroden.signedit;

import net.timroden.signedit.data.SignEditDataPackage;
import net.timroden.signedit.data.SignFunction;
import net.timroden.signedit.utils.SignEditUtils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignEditPlayerListener implements Listener {
	private final SignEdit plugin;
	private final SignEditUtils utils;

	public SignEditPlayerListener(SignEdit plugin) {
		this.plugin = plugin;
		this.utils = plugin.utils;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		SignEditDataPackage dataPack = null;
		if (!event.getAction().equals(Config.clickAction())) {
			return;
		}

		if (!plugin.playerData.containsKey(player.getName())) {
			return;
		}

		if (block == null || !utils.isSign(block)) {
			return;
		}

		Sign sign = (Sign) block.getState();
		dataPack = plugin.playerData.get(player.getName());

		SignFunction function = dataPack.getFunction();

		if (function.equals(SignFunction.COPY)) {
			if (utils.shouldCancel(player)) {
				event.setCancelled(true);
				sign.update();
			}
			SignEditDataPackage tmp = new SignEditDataPackage(player.getName(), sign.getLines(), dataPack.getAmount(), SignFunction.PASTE);
			plugin.playerData.put(player.getName(), tmp);
			player.sendMessage(plugin.chatPrefix + plugin.localization.get("copySignAdded", new Object[] {
					plugin.config.clickActionStr()
			}));
		} else if (function.equals(SignFunction.COPYPERSIST)) {
			if (utils.shouldCancel(player)) {
				event.setCancelled(true);
				sign.update();
			}
			SignEditDataPackage tmp = new SignEditDataPackage(player.getName(), SignFunction.PASTEPERSIST, sign.getLines());
			plugin.playerData.put(player.getName(), tmp);
			player.sendMessage(plugin.chatPrefix + plugin.localization.get("copySignAdded", new Object[] {
					plugin.config.clickActionStr()
			}));
		} else if (function.equals(SignFunction.PASTE)) {
			if (utils.shouldCancel(player)) {
				event.setCancelled(true);
			}
			String[] lines = dataPack.getLines();

			if (!player.hasPermission("signedit.override")) {
				if (utils.throwSignChange(block, player, lines).booleanValue()) {
					player.sendMessage(plugin.chatPrefix + plugin.localization.get("pasteError"));
					plugin.playerData.remove(player.getName());
					return;
				}
			}

			for (int i = 0; i < lines.length; i++) {
				sign.setLine(i, lines[i]);
			}
			sign.update();

			int amount = dataPack.getAmount();

			amount--;
			if (amount == 0) {// Why are we doing this again at 0? who knows.
				utils.throwSignChange(block, player, lines);
				player.sendMessage(plugin.chatPrefix + plugin.localization.get("pasted") + " " + plugin.localization.get("pasteEmpty"));
				plugin.playerData.remove(player.getName());
				return;
			}
			SignEditDataPackage tmp = new SignEditDataPackage(player.getName(), lines, amount, SignFunction.PASTE);
			plugin.playerData.put(player.getName(), tmp);
			player.sendMessage(plugin.chatPrefix + plugin.localization.get("pasted") + " " + plugin.localization.get("pasteCopiesLeft", new Object[] {
					Integer.valueOf(amount), amount == 1 ? plugin.localization.get("pasteCopyStr") : plugin.localization.get("pasteCopiesStr")
			}));
		} else if (function.equals(SignFunction.PASTEPERSIST)) {
			if (utils.shouldCancel(player)) {
				event.setCancelled(true);
			}
			String[] lines = dataPack.getLines();

			if (!player.hasPermission("signedit.override")) {
				if (utils.throwSignChange(block, player, lines).booleanValue()) {
					player.sendMessage(plugin.chatPrefix + plugin.localization.get("pasteError"));
					plugin.playerData.remove(player.getName());
					return;
				}
			}

			for (int i = 0; i < lines.length; i++) {
				sign.setLine(i, lines[i]);
			}
			sign.update();
			player.sendMessage(plugin.chatPrefix + plugin.localization.get("pasteCopiesLeft", new Object[] {
					"∞", "copies"
			}));
		} else if (function.equals(SignFunction.EDIT)) {
			if (utils.shouldCancel(player)) {
				event.setCancelled(true);
			}
			int line = dataPack.getLineNum();
			String originalLine = sign.getLine(line);

			String[] existingLines = sign.getLines();
			String newText = dataPack.getLine();
			existingLines[line] = newText;

			if (!player.hasPermission("signedit.override")) {
				if (utils.throwSignChange(block, player, existingLines).booleanValue()) {
					player.sendMessage(plugin.chatPrefix + plugin.localization.get("editError"));
					plugin.playerData.remove(player.getName());
					return;
				}
			}

			sign.setLine(line, ChatColor.translateAlternateColorCodes('&', newText));

			Location loc = sign.getLocation();
			plugin.getLogger().info(String.format("[SignChange] %s (%s, %s, %s, %s) changed \"%s\" to \"%s\"",
					player.getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(),
					originalLine, newText));

			sign.update();
			player.sendMessage(plugin.chatPrefix + plugin.localization.get("editChanged"));
			plugin.playerData.remove(player.getName());
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (plugin.config.colorsOnPlace()) {
			if (plugin.config.useCOPPermission() && !e.getPlayer().hasPermission("signedit.colorsonplace")) {
				return;
			}

			String[] lines = e.getLines();
			for (int i = 0; i < 4; i++) {
				String line = lines[i];
				line = ChatColor.translateAlternateColorCodes('&', line);
				e.setLine(i, line);
			}
		}
	}
}