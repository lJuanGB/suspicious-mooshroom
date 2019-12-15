package com.gmail.ljuangbminecraft.suspiciousmooshroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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

	/**
	 * Extracts MooshroomData stored within a PersistentDataHolder (entity, item...)
	 * 
	 * @param holder
	 * @return
	 */
	public static MooshroomData getMooshroomData(PersistentDataHolder holder)
	{
		PersistentDataContainer cont = holder.getPersistentDataContainer();
		
		if (!cont.has(keyData, PersistentDataType.STRING))
		{
			return new MooshroomData();
		}
		
		return new MooshroomData(cont.get(keyData, PersistentDataType.STRING));
	}
	
	/**
	 * Stores MooshroomData so that it may be later retrieved by UtilMethods.getMooshroomData
	 * @param holder
	 * @param data
	 */
	public static void setMooshroomData(PersistentDataHolder holder, MooshroomData data)
	{
		if (data.isEmpty())
		{
			holder.getPersistentDataContainer().remove(keyData);
		}
		
		holder.getPersistentDataContainer().set(keyData, PersistentDataType.STRING, data.toString());
	}
	
	/**
	 * If the item is a custom SuspiciousMooshrom item, returns its id
	 * 
	 * @param holder 
	 * @return empty if item is not a custom SuspiciousMooshrom item
	 */
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
	
	/**
	 * Sets an identifier in the item meta of an item so that it may be
	 * extracted with UtilMethods.getItemID
	 * 
	 * @param holder
	 * @param id
	 */
	public static void setItemID(ItemMeta holder, String id)
	{
		holder.getPersistentDataContainer().set(keyItem, PersistentDataType.STRING, id);
	}
	
	/**
	 * @param ent
	 * @return true if the entity is in a cooldown defined by UtilMethods.setCooldown
	 */
	public static boolean isInCooldown(Entity ent)
	{
		PersistentDataContainer cont = ent.getPersistentDataContainer();
		
		if (!cont.has(keyCool, PersistentDataType.LONG))
		{
			return false;
		}
		
		return System.currentTimeMillis() < cont.get(keyCool, PersistentDataType.LONG);
	}
	
	/**
	 * Sets an entity into cooldown for a set amount of time, which can be checked with
	 * UtilMethods.isInCooldown
	 * 
	 * @param ent
	 * @param ticks The time to be in cooldown in minecraft ticks (assummin 20 millis = 1 tick,
	 * which may not be true for laggy servers)
	 */
	public static void setCooldown(Entity ent, int ticks)
	{
		long time = (long) (System.currentTimeMillis() + ((double) ticks)/20.0*1000.0);
		ent.getPersistentDataContainer().set(keyCool, PersistentDataType.LONG, time);
	}
	
	/**
	 * Returns a random value out of weighted collection.
	 * 
	 * @param possibilities Is a map with the keys corresponding to possible outcomes and the values
	 * corresponding to the weight the key should have in the random draw
	 * @param seed A seed for the RNG. null if random seed.
	 * @return
	 */
	public static <T> T drawRandom(Map<T, ? extends Number> possibilities, Long seed)
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
		
		seed = seed == null ? System.currentTimeMillis() : seed;
		Random random = new Random(seed);
		double rand = random.nextDouble() * total;
		
		for (T val : possibilities.keySet())
		{
			rand -= Math.max(0, possibilities.get(val).doubleValue());
			
			if (rand < 0)
			{
				return val;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a random FlowerEffectType.
	 * 
	 * @param treasure if false then the outcome will be drawn from the non-treasure effects.
	 * If true then it will be drawn exclusively from the tresure effects.
	 * @param seed A seed for the RNG. null if random seed.
	 * @return
	 */
	public static FlowerEffectType getRandomEffect(boolean treasure, Long seed)
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
		
		return UtilMethods.drawRandom(chances, seed);
	}

	/**
	 * A better parse of PotionEffectType that includes minecraft potion ids.
	 * @param s
	 * @return empty optional if the spring doesn't correspond to any effect
	 */
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

	/**
	 * Wraps a string into 40 character length text. Automatically makes
	 * it a neutral gray color. Respects color specificed with the character '&'
	 * @param string
	 * @return
	 */
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
