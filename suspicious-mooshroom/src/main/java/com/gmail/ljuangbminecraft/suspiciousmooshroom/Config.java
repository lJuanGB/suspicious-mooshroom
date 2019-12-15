package com.gmail.ljuangbminecraft.suspiciousmooshroom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffectType;

import com.gmail.ljuangbminecraft.suspiciousmooshroom.Config.Entry.Type;
import com.google.common.io.Files;

/**
 * Handles config file reading and storing of variables
 * 
 * @author lJuanGB
 */
public class Config {

	private static List<Entry<?>> entries = new ArrayList<>();

	public static Entry<Boolean> allow_convert = new Entry<>("allow_convert", Type.BOOLEAN, true);
	public static Entry<Boolean> free_rename = new Entry<>("free_rename", Type.BOOLEAN, true);

	public static Entry<Double> chance_brown = new Entry<>("chance.brown", Type.DOUBLE, 1d/5);
	public static Entry<Double> chance_mutation = new Entry<>("chance.mutation", Type.DOUBLE, 1d/20);
	public static Entry<Double> chance_downgrade = new Entry<>("chance.downgrade", Type.DOUBLE, 1d);
	
	public static Entry<Integer> limit_total = new Entry<>("limits.total", Type.INTEGER, Integer.MAX_VALUE);
	public static Entry<Integer> limit_intensity = new Entry<>("limits.intensity", Type.INTEGER, Integer.MAX_VALUE);
	public static Entry<Integer> limit_duration = new Entry<>("limits.duration", Type.INTEGER, Integer.MAX_VALUE);
	public static Entry<Integer> limit_cooldown = new Entry<>("limits.milkCooldown", Type.INTEGER, 0);
	
	public static Entry<String> lang_baby = new Entry<>("language.baby", Type.STRING, "");
	public static Entry<String> lang_incorrect_flower = new Entry<>("language.incorrect_flower", Type.STRING, "");
	public static Entry<String> lang_not_fed = new Entry<>("language.not_fed", Type.STRING, "");
	public static Entry<String> lang_too_much = new Entry<>("language.too_much", Type.STRING, "");
	public static Entry<String> lang_cooldown = new Entry<>("language.cooldown", Type.STRING, "");
	public static Entry<String> lang_effect_potion_lore = new Entry<>("language.effect_potion_lore", Type.STRING, "");
	public static Entry<String> lang_fungi_powder_name = new Entry<>("language.fungi_powder_name", Type.STRING, "");
	public static Entry<String> lang_fungi_powder_lore = new Entry<>("language.fungi_powder_lore", Type.STRING, "");
	public static Entry<String> lang_cannibal_fungi_name = new Entry<>("language.cannibal_fungi_name", Type.STRING, "");
	public static Entry<String> lang_cannibal_fungi_lore = new Entry<>("language.cannibal_fungi_lore", Type.STRING, "");
	public static Entry<String> lang_floral_forage_name = new Entry<>("language.floral_forage_name", Type.STRING, "");
	public static Entry<String> lang_floral_forage_lore = new Entry<>("language.floral_forage_lore", Type.STRING, "");

	public static void load()
	{
		File configF = new File(SuspiciousMooshroom.getInstance().getDataFolder(), "config.yml");
		
		if (!configF.exists()) // If config is not present, place it from resources
		{
			SuspiciousMooshroom.getInstance().getDataFolder().mkdirs();
			InputStream confStream = SuspiciousMooshroom.getInstance().getResource("config.yml");
			
			try 
			{
				byte[] buffer = new byte[confStream.available()];
				confStream.read(buffer);
				Files.write(buffer, configF);
			} 
			catch (IOException e) 
			{
				SuspiciousMooshroom.log(Level.SEVERE, "Could not copy config file", e);
			}
		}
		
		reload();
	}
	
	public static void reload()
	{
		SuspiciousMooshroom.getInstance().reloadConfig();
		FileConfiguration file = SuspiciousMooshroom.getInstance().getConfig();
		
		for (Entry<?> entry : entries)
		{
			entry.set(file);
		}
		
		if (!file.isConfigurationSection("effects"))
		{
			SuspiciousMooshroom.log(Level.SEVERE, "config.yml does not contain section 'effects'. Delete config.yml file to reset to default settings and solve this issue.");
			Bukkit.getPluginManager().disablePlugin( SuspiciousMooshroom.getInstance() );
			return;
		}
			
		FlowerEffectType.empty();
		
		ConfigurationSection effects = file.getConfigurationSection("effects");
		for (String key : effects.getKeys(false))
		{
			ConfigurationSection sect = effects.getConfigurationSection(key);
			Optional<FlowerEffectType> type = getFlowerEffectType(sect, key);
			
			if (type.isPresent()) type.get().register();
		}
	}
	
	private static Optional<FlowerEffectType> getFlowerEffectType(ConfigurationSection sect, String key) {
		
		Material mat = Material.BARRIER;
		if(sect.isString("feedingItem"))
		{
			String matS = sect.getString("feedingItem").toUpperCase();
			try
			{
				mat = Material.valueOf(matS);
			}
			catch (Exception ex)
			{
				SuspiciousMooshroom.log(Level.WARNING, "Invalid item for effect " + key + ": " + matS + ". Place here item id.");
				return Optional.empty();
			}
		}
		else
		{
			SuspiciousMooshroom.log(Level.WARNING, "Missing 'feedingItem' (must be a string) for effect " + key);
		}

		double weight = 0;
		if(sect.isDouble("weight") || sect.isInt("weight"))
		{
			weight = sect.getDouble("weight");
		}
		else
		{
			SuspiciousMooshroom.log(Level.WARNING, "Missing 'weight' (must be a double) for effect " + key);
		}
		
		boolean treasure = sect.getBoolean("treasure", false);
		
		int baseDuration = 0;
		if(sect.isInt("baseDuration"))
		{
			baseDuration = sect.getInt("baseDuration");
		}
		else
		{
			SuspiciousMooshroom.log(Level.WARNING, "Missing 'baseDuration' (must be an integer) for effect " + key);
		}
		
		PotionEffectType type = PotionEffectType.SPEED;
		if(sect.isString("potionType"))
		{
			String typS = sect.getString("potionType");
			Optional<PotionEffectType> optType = UtilMethods.getPotionEffectType(typS);
			if(!optType.isPresent())
			{
				SuspiciousMooshroom.log(Level.WARNING, "Invalid potionType for effect " + key + ": " + typS + ". Place here effect id.");
				return Optional.empty();
			}
			
			type = optType.get();
		}
		else
		{
			SuspiciousMooshroom.log(Level.WARNING, "Missing 'potionType' (must be a string) for effect " + key);
		}
		
		String lang = "null";
		if(sect.isString("lang_potion"))
		{
			lang = sect.getString("lang_potion");
		}
		else
		{
			SuspiciousMooshroom.log(Level.WARNING, "Missing 'lang_potion' (must be a string) for effect " + key);
		}
		
		FlowerEffectType effectType = new FlowerEffectType(key, mat, weight, treasure, type, baseDuration, lang);
		
		return Optional.of(effectType);
	}

	
	public static class Entry<T> {
		
		protected static class Type<T> {
			
			public static Type<String> STRING = new Type<>((c,s) -> c.getString(s));
			public static Type<Double> DOUBLE = new Type<>((c,s) -> c.getDouble(s));
			public static Type<Integer> INTEGER = new Type<>((c,s) -> c.getInt(s));
			public static Type<Boolean> BOOLEAN = new Type<>((c,s) -> c.getBoolean(s));
			
			private final BiFunction<ConfigurationSection,String, T> func;
			
			private Type(BiFunction<ConfigurationSection,String, T> func)
			{
				this.func = func;
			}
			
			public T get(ConfigurationSection sec, String path)
			{
				return func.apply(sec, path);
			}
		}
		
		protected T value;
		protected final T defaultValue;
		
		protected final Type<T> type;
		
		protected final String path;
				
		private Entry(String path, Type<T> type, T defaultValue)
		{
			Validate.notNull(path);
			Validate.notNull(type);
			Validate.notNull(defaultValue);
			
			this.path = path;
			this.defaultValue = defaultValue;
			this.type = type;
			
			entries.add(this);
		}

		public T get() {
			return value;
		}
		
		public void set(ConfigurationSection conf)
		{
			T obj = type.get(conf, path);
			value = obj == null ? defaultValue : obj;
		}
	}
	
}
