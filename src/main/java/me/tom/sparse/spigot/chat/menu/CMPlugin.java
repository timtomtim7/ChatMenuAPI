package me.tom.sparse.spigot.chat.menu;

import org.bukkit.plugin.java.JavaPlugin;

public class CMPlugin extends JavaPlugin
{
	public void onEnable()
	{
		ChatMenuAPI.init(this);
	}
	
	public void onDisable()
	{
		ChatMenuAPI.disable();
	}
}
