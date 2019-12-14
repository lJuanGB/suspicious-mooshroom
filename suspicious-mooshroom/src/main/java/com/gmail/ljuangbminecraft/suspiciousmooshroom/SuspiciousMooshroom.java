package com.gmail.ljuangbminecraft.suspiciousmooshroom;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.ljuangbminecraft.suspiciousmooshroom.commands.MooshroomConfigCommand;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.commands.MooshroomItemCommand;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.listeners.CowBreedingListener;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.listeners.CowInteractListener;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.listeners.CowSpawnListener;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.listeners.StewConsumeListener;

/**
 * Main plugin class.
 * Loads a static instance of the plugin.
 * Registers all events.
 * Registers commands.
 * Sets static values to null on plugin disable.
 * Provides some log functions for quality of life.
 * 
 * @author lJuanGB
 */
public class SuspiciousMooshroom extends JavaPlugin{

	private static SuspiciousMooshroom instance; 
	
	@Override
	public void onEnable() {
		instance = this;
		
		Bukkit.getPluginManager().registerEvents(new CowBreedingListener(), this);
		Bukkit.getPluginManager().registerEvents(new CowInteractListener(), this);
		Bukkit.getPluginManager().registerEvents(new CowSpawnListener(), this);
		Bukkit.getPluginManager().registerEvents(new StewConsumeListener(), this);

		getCommand("mooshroomitem").setExecutor(new MooshroomItemCommand());
		getCommand("mooshroomconfig").setExecutor(new MooshroomConfigCommand());

		Config.load();
		MooshroomItems.registerItems();
	}
	
	@Override
	public void onDisable() {
		instance = null;
		FlowerEffectType.nullify();
		MooshroomItems.nullify();
	}
	
	public static SuspiciousMooshroom getInstance()
	{
		return instance;
	}
	
	public static void log(Level level, String message)
	{
		instance.getLogger().log(level, message);
	}
	
	public static void log(Level level, String message, Exception ex)
	{
		instance.getLogger().log(level, message, ex);
	}
}
