package net.timroden.signedit.utils;

import net.timroden.signedit.Config;
import net.timroden.signedit.SignEdit;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

public class SignEditUtils {
	private final SignEdit plugin;

	public SignEditUtils(SignEdit plugin) {
		this.plugin = plugin;
	}

	public boolean isInt(String check) {
		try {
			Integer.parseInt(check);
		} catch (NumberFormatException ex) {
			return false;
		}

		return true;
	}

	public static String strip(String in) {
		// return cleanColorCodes(ChatColor.stripColor(in));
		return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', in));
	}

	public Boolean throwSignChange(Block theBlock, Player thePlayer, String[] theLines) {
		if (Config.fireBlockBreakPlace()) {
			BlockBreakEvent b = new BlockBreakEvent(theBlock, thePlayer);
			plugin.pluginMan.callEvent(b);

			if (b.isCancelled()) {
				plugin.getLogger().info("[Blocked] Another plugin blocked the block break check.");
				return true;
			}

			BlockPlaceEvent p = new BlockPlaceEvent(theBlock, theBlock.getState(), theBlock, null, thePlayer, true);

			plugin.pluginMan.callEvent(p);

			if (p.isCancelled()) {
				plugin.getLogger().info("[Blocked] Another plugin blocked the block place check.");
				return true;
			}
		}

		String[] originalLines = theLines.clone();

		SignChangeEvent event = new SignChangeEvent(theBlock, thePlayer, theLines);
		plugin.pluginMan.callEvent(event);

		for (int i = 0; i < theLines.length; i++) {
			if (!strip(theLines[i]).equalsIgnoreCase(strip(originalLines[i]))) {
				plugin.getLogger().info(String.format("[Blocked] Another plugin modified line %s: %s to %s",
						i + 1, strip(originalLines[i]), strip(theLines[i])));
				return true;
			}
		}

		return Boolean.valueOf(event.isCancelled());
	}

	public boolean isSign(Block b) {
		return b.getType().equals(Material.SIGN) || b.getType().equals(Material.SIGN_POST) || b.getType().equals(Material.WALL_SIGN);
	}

	public boolean shouldCancel(Player player) {
		boolean ret = Config.ignoreCreative() && !plugin.config.invertMouse() && player.getGameMode().equals(GameMode.CREATIVE);
		return ret;
	}

	public String implode(String[] inputArray, String glue, int start, int end) {
		if (inputArray.length - 1 == 0) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		if (inputArray.length > 0) {
			for (int i = start; i < inputArray.length; i++) {
				sb.append(inputArray[i]);
				sb.append(glue);
			}
		}
		return sb.toString().trim();
	}

	public String capitalize(String toCaps) {
		return toCaps.substring(0, 1).toUpperCase() + toCaps.substring(1);
	}
}