package com.gmail.ljuangbminecraft.suspiciousmooshroom.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import com.gmail.ljuangbminecraft.suspiciousmooshroom.FlowerEffect;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.MooshroomData;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.UtilMethods;

public class StewConsumeListener implements Listener{

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerConsumeStew(PlayerItemConsumeEvent e)
	{
		if (!e.getItem().hasItemMeta())
		{
			return;
		}
		
		MooshroomData data = UtilMethods.getMooshroomData(e.getItem().getItemMeta());
		
		for (FlowerEffect effect : data.getEffects())
		{
			e.getPlayer().addPotionEffect(effect.getPotionEffect());
		}
	}
}
