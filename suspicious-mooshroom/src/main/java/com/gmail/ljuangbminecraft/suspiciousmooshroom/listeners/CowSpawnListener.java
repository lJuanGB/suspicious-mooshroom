package com.gmail.ljuangbminecraft.suspiciousmooshroom.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.MushroomCow.Variant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;
import org.bukkit.event.world.ChunkLoadEvent;

import com.gmail.ljuangbminecraft.suspiciousmooshroom.Config;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.FlowerEffect;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.MooshroomData;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.UtilMethods;

/**
 * Listens to when a cows spawn naturally and applies chance to make them brown.
 * Additionally, give flower effect to brown mushrooms.
 * 
 * @author lJuanGB
 */
public class CowSpawnListener implements Listener{

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCowHitLightning(EntityTransformEvent e) 
	{
		if (!e.getTransformReason().equals(TransformReason.LIGHTNING))
		{
			return;
		}
		
		if (!e.getEntity().getType().equals(EntityType.MUSHROOM_COW)) // Changing color doesn't change effects
		{
			return;
		}
		
		MooshroomData data = UtilMethods.getMooshroomData(e.getEntity());
		UtilMethods.setMooshroomData(e.getTransformedEntities().get(0), data);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCowSpawnNaturally(CreatureSpawnEvent e) 
	{
		if (!e.getEntityType().equals(EntityType.MUSHROOM_COW))
		{
			return;
		}
		
		MushroomCow mCow = (MushroomCow) e.getEntity();	

		switch (e.getSpawnReason())
		{
			case BREEDING:
			case LIGHTNING: //Breeding and lightning we don't want to interfere
				return; 
				
			case NATURAL: //If cow spawned naturally, chance that they turn brown
				if (mCow.getVariant().equals(Variant.RED) && Math.random() < Config.chance_brown.get())
				{
					mCow.setVariant(Variant.BROWN);
				}				
			default:
				break;
		}
				
		if (mCow.getVariant().equals(Variant.BROWN))
		{
			addRandomEffect(mCow);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChunkGenerate(ChunkLoadEvent e) 
	{
		if (!e.isNewChunk())
		{
			return; //Only care about newly populated chunks, since they have natural spawned entities
		}
		
		for (Entity ent : e.getChunk().getEntities())
		{
			if (!ent.getType().equals(EntityType.MUSHROOM_COW))
			{
				return;
			}
			
			MushroomCow mCow = (MushroomCow) ent;
			
			if (mCow.getVariant().equals(Variant.RED) && Math.random() < Config.chance_brown.get())
			{
				mCow.setVariant(Variant.BROWN);
			}	
			addRandomEffect((MushroomCow) ent); // If brown, add effect
		}
	}
	
	public static void addRandomEffect(Entity cow)
	{
		long seed = cow.getUniqueId().getLeastSignificantBits();
		FlowerEffect effect = new FlowerEffect( UtilMethods.getRandomEffect(false, seed), 1, 1);
		MooshroomData data = new MooshroomData(effect);
		data.setFeedLevel(effect.getType(), 1);
		UtilMethods.setMooshroomData(cow, data);
	}
}
