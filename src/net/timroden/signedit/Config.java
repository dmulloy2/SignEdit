package net.timroden.signedit;

import org.bukkit.configuration.Configuration;
import org.bukkit.event.block.Action;

public class Config {
	private SignEdit plugin;
	private static Configuration config;
	
	public boolean ignoreCreative, invertMouse, notifyOnVersion, commandsLogConsole, commandsLogFile;
	public Action clickAction;
	public String logName, clickActionStr;
	
	public Config(SignEdit plugin) {
		this.plugin = plugin;
		config = plugin.getConfig().getRoot();
		config.options().copyDefaults(true);
		plugin.saveConfig();
		
		getOpts();
	}
	
	public void reload() {
		plugin.reloadConfig();
		config = plugin.getConfig().getRoot();
		
		getOpts();
	}
	
	public void getOpts() {
		ignoreCreative = config.getBoolean("signedit.ignorecreative");
		logName = config.getString("signedit.log.filename");
		invertMouse = config.getBoolean("signedit.invertmouse");
		notifyOnVersion = config.getBoolean("signedit.notifyversion");
		commandsLogConsole = config.getBoolean("signedit.commands.logtoconsole");
		commandsLogFile = config.getBoolean("signedit.commands.logtofile");
		
		if(invertMouse) {
			clickAction = Action.RIGHT_CLICK_BLOCK;
			clickActionStr = "right click";
		} else {
			clickAction = Action.LEFT_CLICK_BLOCK;
			clickActionStr = "left click";
		}
	}
}
