package com.gmail.ljuangbminecraft.suspiciousmooshroom.listeners;

import java.util.Optional;
import java.util.function.BiFunction;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.MushroomCow.Variant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

import com.gmail.ljuangbminecraft.suspiciousmooshroom.Config;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.FlowerEffect;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.MooshroomData;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.UtilMethods;

public class CowBreedingListener implements Listener{

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onCowsBreedListener(EntityBreedEvent e)
	{
		// Check both result, mother and father are mooshroom cows
		if (   !e.getEntity().getType().equals(EntityType.MUSHROOM_COW)
			|| !e.getMother().getType().equals(EntityType.MUSHROOM_COW)
			|| !e.getFather().getType().equals(EntityType.MUSHROOM_COW)
		   )
		{
			return;
		}

		MooshroomData result = combine(e.getMother(), e.getFather());

		result.purgeEffects(); //For precaution we remove incompatible effects

		while (Config.limit_total.get() >= 0 && result.getTotalPoints() > Config.limit_total.get())
		{
			result.downgradeRandom();
		}

		// Random effect downgrade
		for (int m = 1; m < 5; m++)
		{
			int n = result.getTotalPoints();
			
			if (Math.random() < Config.chance_downgrade.get()*(n/10.0 - 0.2*m))
			{
				result.downgradeRandom();;
			}
			else
			{
				break;
			}
		}
				
		UtilMethods.setMooshroomData(e.getEntity(), result);
	}
	

	private MooshroomData combine(Entity mother, Entity father)
	{
		Variant varM = ((MushroomCow) mother).getVariant();
		Variant varF = ((MushroomCow) father).getVariant();
		
		BiFunction<FlowerEffect,FlowerEffect,FlowerEffect> mixFunc = null;
		if (varM.equals(Variant.RED) && varF.equals(Variant.RED))
		{
			mixFunc = (a,b) -> redBreed(a,b);
		}
		else if (varM.equals(Variant.BROWN) && varF.equals(Variant.BROWN))
		{
			mixFunc = (a,b) -> brownBreed(a,b);
		}
		else
		{
			mixFunc = (a,b) -> mixBreed(a,b);
		}
		
		MooshroomData dataM = UtilMethods.getMooshroomData(mother);
		MooshroomData dataF = UtilMethods.getMooshroomData(father);
		MooshroomData result = new MooshroomData();
		
		// Add effects that are going to get mixed & mother's
		for (FlowerEffect effect : dataM.getEffects())
		{
			Optional<FlowerEffect> effect2 = dataF.getEffect(effect.getType());
			
			if(effect2.isPresent()) // If mother's effect coincides with one of father
			{
				result.addEffect( mixFunc.apply(effect, effect2.get()) ); //Add the mix to result
				dataF.removeEffect(effect2.get().getType()); // remove from father's so that when adding at the end we don't repeat
			}
			else
			{
				result.addEffect(effect); //Non-mixed effects get added
			}
		}
		
		// Add non-mixed father effects
		for (FlowerEffect effect : dataF.getEffects())
		{
			result.addEffect(effect); //Remember effects that got mixed in previous loot got removed from dataF
		}
		
		return result;
	}
	
	private FlowerEffect redBreed(FlowerEffect effect1, FlowerEffect effect2)
	{
		Validate.isTrue(effect1.getType().equals(effect2.getType()));
		
		if (effect1.getIntensityLevel() == effect2.getIntensityLevel()) // If same intensity combine
		{
			int durLevel = Math.min(effect1.getDurationLevel(), effect2.getDurationLevel()); //Choose lowest duration
			int intLevel = Math.min(effect1.getIntensityLevel() + 1, Config.limit_intensity.get()); //Add 1 level to intensity (within limit)
			return new FlowerEffect(effect1.getType(), durLevel, intLevel);
		}
		
		// else return effect with highest level
		return effect1.getIntensityLevel() > effect2.getIntensityLevel() ? effect1 : effect2;
	}
	
	private FlowerEffect brownBreed(FlowerEffect effect1, FlowerEffect effect2)
	{
		Validate.isTrue(effect1.getType().equals(effect2.getType()));
		
		if (effect1.getIntensityLevel() == effect2.getIntensityLevel()) // If same intensity combine
		{
			int durLevel = effect1.getDurationLevel() + effect2.getDurationLevel(); //Combine duration
			durLevel = Math.min(durLevel, Config.limit_duration.get()); 			// within limit
			return new FlowerEffect(effect1.getType(), durLevel, effect1.getIntensityLevel());
		}
		
		// else return effect with highest level
		return effect1.getIntensityLevel() > effect2.getIntensityLevel() ? effect1 : effect2;
	}
	
	private FlowerEffect mixBreed(FlowerEffect effect1, FlowerEffect effect2)
	{
		Validate.isTrue(effect1.getType().equals(effect2.getType()));
		
		if (Math.random() < Config.chance_mutation.get()) //If random chance of mutation
		{
			return new FlowerEffect(UtilMethods.getRandomEffect(true, null), 1, 1);
		}
		
		// else return effect with highest level
		return effect1.getIntensityLevel() > effect2.getIntensityLevel() ? effect1 : effect2;
	}
	
}
