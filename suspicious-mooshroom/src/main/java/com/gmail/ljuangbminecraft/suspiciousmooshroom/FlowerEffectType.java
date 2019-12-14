package com.gmail.ljuangbminecraft.suspiciousmooshroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

/**
 * Represents a type of FlowerEffectType, as defined in config. Contains information about unique id, item used
 * to feed cow with this type, weight of the effect appearing, whether it's a treasure effect (does not appear at spawn)
 * and the base duration in ticks of the effect.
 * 
 * @author lJuanGB
 */
public class FlowerEffectType {

	private static Map<String, FlowerEffectType> map = new HashMap<String, FlowerEffectType>();	
	
	private final String id;
	private final Material feedingItem;
	private final double weight;
	private final boolean treasure;
	private final PotionEffectType potionType;
	private final int baseDuration;
	private final String lang_potion;
	

	public FlowerEffectType(String id, Material feedingItem, double weight, boolean treasure,
			PotionEffectType potionType, int baseDuration, String lang_potion) {
		
		Validate.notNull(id);
		Validate.notNull(feedingItem);
		Validate.notNull(potionType);
		Validate.isTrue(weight >= 0);
		Validate.isTrue(baseDuration >= 0);
		Validate.notNull(lang_potion);
		
		this.id = id;
		this.feedingItem = feedingItem;
		this.weight = weight;
		this.treasure = treasure;
		this.potionType = potionType;
		this.baseDuration = baseDuration;
		this.lang_potion = lang_potion;
	}

	/**
	 * Registers the effect within the internal map. Will throw an exception for repeated ids.
	 */
	public void register() 
	{		
		if(map.containsKey(id))
		{
			throw new IllegalArgumentException("ID is already registered");
		}
		
		map.put(id, this);
	}

	public String getId() {
		return id;
	}

	public Material getFeedingItem() {
		return feedingItem;
	}

	public double getWeight() {
		return weight;
	}

	public boolean isTreasure() {
		return treasure;
	}

	public int getBaseDuration() {
		return baseDuration;
	}
	
	public PotionEffectType getPotionType() {
		return potionType;
	}

	public String getLangPotion() {
		return lang_potion;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof FlowerEffectType))
		{
			return false;
		}
		
		return ((FlowerEffectType) obj).getId().equals(id);
	}
	
	public static FlowerEffectType valueOf(String id)
	{		
		if (!map.containsKey(id))
		{
			throw new IllegalArgumentException("No such FlowerEffectType such as :" + id);
		}
		
		return map.get(id);
	}
	
	public static List<FlowerEffectType> values()
	{
		return new ArrayList<FlowerEffectType>(map.values());
	}
	
	/**
	 * Set's internal static map to null, to ensure no memory leaks.
	 * DO NOT CALL UNLESS YOU KNOW WHAT YOU ARE DOING
	 */
	public static void nullify()
	{
		map = null;
	}
	
	/**
	 * Resets internal static map, so that we can re-register effects.
	 * DO NOT CALL UNLESS YOU KNOW WHAT YOU ARE DOING
	 */
	public static void empty()
	{
		map = new HashMap<>();
	}
}
