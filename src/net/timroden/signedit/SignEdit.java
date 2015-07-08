package net.timroden.signedit;

import java.util.HashMap;
import java.util.Map;

import net.timroden.signedit.commands.CommandSignEdit;
import net.timroden.signedit.data.SignEditDataPackage;
import net.timroden.signedit.localization.SignEditLocalization;
import net.timroden.signedit.utils.SignEditUtils;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.cyprias.YML;

public class SignEdit extends JavaPlugin {
	public String chatPrefix = ChatColor.RESET + "[" + ChatColor.AQUA + "SignEdit" + ChatColor.WHITE + "] " + ChatColor.RESET;
	public PluginManager pluginMan;
	public Map<String, SignEditDataPackage> playerData;
	public Map<String, Integer> pasteAmounts = new HashMap<String, Integer>();
	private SignEditPlayerListener listener;
	public SignEditUtils utils;
	public SignEditLocalization localization;
	public Config config;
	public YML yml;

	@Override
	public void onEnable() {
		playerData = new HashMap<String, SignEditDataPackage>();
		config = new Config(this);
		yml = new YML(this);
		localization = new SignEditLocalization(this);

		utils = new SignEditUtils(this);

		listener = new SignEditPlayerListener(this);

		pluginMan = getServer().getPluginManager();

		pluginMan.registerEvents(listener, this);

		getCommand("signedit").setExecutor(new CommandSignEdit(this));
	}

	@Override
	public void onDisable() {
		playerData.clear();
		playerData = null;
	}
}