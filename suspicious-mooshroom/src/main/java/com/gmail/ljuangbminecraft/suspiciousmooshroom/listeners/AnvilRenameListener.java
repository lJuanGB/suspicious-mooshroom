package com.gmail.ljuangbminecraft.suspiciousmooshroom.listeners;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.ljuangbminecraft.suspiciousmooshroom.Config;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.UtilMethods;

public class AnvilRenameListener implements Listener{

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void anvilRecipeListener(InventoryClickEvent e)
	{
		if (!Config.free_rename.get())
		{
			return;
		}
		
		Inventory inv = e.getInventory();
		
		if (!(inv instanceof AnvilInventory) || e.getRawSlot() != 2)
		{
			return;
		}
		
		ItemStack item = inv.getItem(2);
		
		if (item == null || !item.hasItemMeta() 
				|| UtilMethods.getMooshroomData(item.getItemMeta()).isEmpty()
				|| inv.getItem(1) != null)
		{
			return;
		}
			
		HumanEntity ent = e.getWhoClicked();
		
		if (ent instanceof Player)
		{
			((Player) ent).giveExpLevels(1);
		}
	}
}
