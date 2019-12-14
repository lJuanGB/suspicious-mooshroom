package com.gmail.ljuangbminecraft.suspiciousmooshroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

/**
 * Stores custom items defined by this plugin and handles their registration.
 * 
 * @author lJuanGB
 */
public class MooshroomItems {

	private static Map<String, ItemStack> items = new HashMap<>();
	
	public static Optional<ItemStack> get(String id)
	{
		Validate.notNull(id);
		
		ItemStack item = items.get(id);
		if (item == null)
		{
			return Optional.empty();
		}
		else
		{
			return Optional.ofNullable(item.clone());
		}
	}
	
	public static List<String> getKeys()
	{
		return new ArrayList<>(items.keySet());
	}
	
	/**
	 * Called once to load items into the static map.
	 * Calling get() method before this has been called will always
	 * return empty optionals.
	 */
	public static void registerItems()
	{
		registerSimpleItem("fungi_powder", Material.SUGAR, Config.lang_fungi_powder_name.get(), Config.lang_fungi_powder_lore.get());
		registerSimpleItem("cannibal_fungi", Material.FERMENTED_SPIDER_EYE, Config.lang_cannibal_fungi_name.get(), Config.lang_cannibal_fungi_lore.get());
	
		for (FlowerEffectType effect : FlowerEffectType.values())
		{
			registerEffectPotion(effect);
		}
	}
	
	/**
	 * Registers an item with name, lore and an id stored in the PersistentDataContainer.
	 * 
	 * @param id to identify the item. Can be retrieved with UtilMethods.getItemID().
	 * @param mat
	 * @param name Name is given color AQUA
	 * @param lore Lore of item. It's automatically wrapped for appropiate length. The character '&' is translated
	 * to color char when appropiate and all the text is given a neutral color if non specified.
	 */
	private static void registerSimpleItem(String id, Material mat, String name, String lore) {
		
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + name);
		meta.setLore(UtilMethods.wrapLore( lore ));
		UtilMethods.setItemID(meta, id);
		
		item.setItemMeta(meta);
		items.put(id, item);
	}

	/**
	 * Creates the potion item for that effect.
	 * When mooshroom are right clicked with this item their effect change to the effect of the potion.
	 * The name changes with the type, but the lore is kept the same thorough all potions.
	 * 
	 * @param type
	 */
	public static void registerEffectPotion(FlowerEffectType type)
	{
		Validate.notNull(type);
		
		String itemName = type.getLangPotion();
		String id = "potion_" + type.getId();
		
		ItemStack pot = new ItemStack(Material.POTION);
		PotionMeta meta = (PotionMeta) pot.getItemMeta();
		
		meta.setDisplayName(ChatColor.AQUA + itemName);
		meta.setLore(UtilMethods.wrapLore( Config.lang_effect_potion_lore.get() ));
		meta.setColor( type.getPotionType().getColor() );
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		UtilMethods.setItemID(meta, id);
		
		pot.setItemMeta(meta);
		items.put(id, pot);
	}

	/**
	 * Set's internal static map to null, to ensure no memory leaks.
	 * DO NOT CALL UNLESS YOU KNOW WHAT YOU ARE DOING
	 */
	public static void nullify() 
	{
		items = null;
	}
}
