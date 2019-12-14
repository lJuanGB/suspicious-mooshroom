package com.gmail.ljuangbminecraft.suspiciousmooshroom.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.ljuangbminecraft.suspiciousmooshroom.UtilMethods;

public class AnvilRenameListener implements Listener{

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void prepareAnvilListener(PrepareAnvilEvent e)
	{
		AnvilInventory inv = e.getInventory();
		ItemStack item = inv.getItem(0);
		
		if (item == null || !item.hasItemMeta() 
				|| UtilMethods.getMooshroomData(item.getItemMeta()).isEmpty()
				|| inv.getItem(1) != null)
		{
			return;
		}
				
		inv.setRepairCost(0);
	}
}
