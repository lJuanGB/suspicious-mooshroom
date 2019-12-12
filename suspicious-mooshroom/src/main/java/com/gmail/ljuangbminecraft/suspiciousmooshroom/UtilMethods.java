package com.gmail.ljuangbminecraft.suspiciousmooshroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;

public class UtilMethods {
	
	public static NamespacedKey keyData = new NamespacedKey(SuspiciousMooshroom.getInstance(), "cowData");
	public static NamespacedKey keyCool = new NamespacedKey(SuspiciousMooshroom.getInstance(), "milkCooldown");
	public static NamespacedKey keyItem = new NamespacedKey(SuspiciousMooshroom.getInstance(), "item");

	
	public static MooshroomData getMooshroomData(PersistentDataHolder holder)
	{
		PersistentDataContainer cont = holder.getPersistentDataContainer();
		
		if (!cont.has(keyData, PersistentDataType.STRING))
		{
			return new MooshroomData();
		}
		
		return new MooshroomData(cont.get(keyData, PersistentDataType.STRING));
	}
	
	public static void setMooshroomData(PersistentDataHolder holder, MooshroomData data)
	{
		holder.getPersistentDataContainer().set(keyData, PersistentDataType.STRING, data.toString());
	}
	
	public static Optional<String> getItemID(ItemMeta holder)
	{
		if (holder == null)
		{
			return Optional.empty();
		}
		
		PersistentDataContainer cont = holder.getPersistentDataContainer();
		
		if (!cont.has(keyItem, PersistentDataType.STRING))
		{
			return Optional.empty();
		}
		
		return Optional.of(cont.get(keyItem, PersistentDataType.STRING));
	}
	
	public static void setItemId(ItemMeta holder, String id)
	{
		holder.getPersistentDataContainer().set(keyItem, PersistentDataType.STRING, id);
	}
	
	public static boolean isInCooldown(Entity ent)
	{
		PersistentDataContainer cont = ent.getPersistentDataContainer();
		
		if (!cont.has(keyCool, PersistentDataType.LONG))
		{
			return false;
		}
		
		return System.currentTimeMillis() < cont.get(keyCool, PersistentDataType.LONG);
	}
	
	public static void setCooldown(Entity ent, int ticks)
	{
		long time = (long) (System.currentTimeMillis() + ((double) ticks)/20.0*1000.0);
		ent.getPersistentDataContainer().set(keyCool, PersistentDataType.LONG, time);
	}
	
	public static <T> T drawRandom(Map<T, ? extends Number> possibilities)
	{
		Validate.notNull(possibilities);
		
		if (possibilities.isEmpty())
		{
			return null;
		}
		
		double total = 0;
		for (T val : possibilities.keySet())
		{
			total += Math.max(0, possibilities.get(val).doubleValue());
		}
		
		if ( total == 0)
		{
			return null;
		}
		
		double random = Math.random() * total;
		
		for (T val : possibilities.keySet())
		{
			random -= Math.max(0, possibilities.get(val).doubleValue());
			
			if (random < 0)
			{
				return val;
			}
		}
		
		return null;
	}
	
	public static FlowerEffectType getRandomEffect(boolean treasure)
	{
		Map<FlowerEffectType, Double> chances = new HashMap<>();
		
		for (FlowerEffectType type : FlowerEffectType.values())
		{
			if (treasure != type.isTreasure())
			{
				continue;
			}
			
			chances.put(type, type.getWeight());
		}
		
		return UtilMethods.drawRandom(chances);
	}

	public static Optional<PotionEffectType> getPotionEffectType(String s)
	{
		PotionEffectType effect = PotionEffectType.getByName(s.toUpperCase());
		
		if(effect != null)
		{
			return Optional.of(effect);
		}
		
		switch (s.toLowerCase().replace(" ", "_"))
		{
		case "nausea": return Optional.of(PotionEffectType.CONFUSION);
		case "resistance": return Optional.of(PotionEffectType.DAMAGE_RESISTANCE);
		case "haste": return Optional.of(PotionEffectType.FAST_DIGGING);
		case "instant_damage": return Optional.of(PotionEffectType.HARM);
		case "instant_health": return Optional.of(PotionEffectType.HEAL);
		case "strength": return Optional.of(PotionEffectType.INCREASE_DAMAGE);
		case "jump_boost": return Optional.of(PotionEffectType.JUMP);
		case "slowness": return Optional.of(PotionEffectType.SLOW);
		case "mining_fatigue": return Optional.of(PotionEffectType.SLOW_DIGGING);
		default:
			return Optional.empty();
		}
	}

	public static List<String> wrapLore(String string)
	{
		if (string == null || string.isEmpty())
		{
			return null;
		}
		
		string = ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', string);
		ArrayList<String> result = Lists.newArrayList(WordUtils.wrap(string, 40).split(System.lineSeparator()));
		
		for (int i = 1; i < result.size(); i++)
		{
			result.set(i, ChatColor.getLastColors(result.get(i-1)) +  result.get(i));
		}
		
		return result;
	}
}
