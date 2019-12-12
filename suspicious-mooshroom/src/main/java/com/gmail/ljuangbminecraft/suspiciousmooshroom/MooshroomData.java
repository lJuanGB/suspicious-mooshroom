package com.gmail.ljuangbminecraft.suspiciousmooshroom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Stores the data a mooshroom may have.
 * Provides ways to serialize and deserialize this object.
 * 
 * Stores the FlowerEffect a mooshroom has and the amount of times it has been
 * fed that effect.
 * 
 * @author lJuanGB
 */
public class MooshroomData {
	
	private static String effSep = ";";
	private static String inSep = ":";
	
	private Map<FlowerEffect, Integer> effects = new HashMap<>();
	
	public MooshroomData(FlowerEffect...effects )
	{
		for (FlowerEffect eff : effects)
		{
			this.effects.put(eff, 0);
		}
	}
	
	public MooshroomData(Collection<FlowerEffect> effects)
	{
		for (FlowerEffect eff : effects)
		{
			this.effects.put(eff, 0);
		}
	}
	
	public MooshroomData(String serialized)
	{
		for (String s : serialized.split(effSep))
		{
			try
			{
				if(s.isEmpty())
				{
					continue;
				}
				
				String[] split = s.split(inSep);
				FlowerEffectType type = FlowerEffectType.valueOf(split[0]);
				int durationLevel = Integer.parseInt(split[1]);
				int intensityLevel = Integer.parseInt(split[2]);
				int feedLevel =  Integer.parseInt(split[3]);
				
				effects.put(new FlowerEffect(type, durationLevel, intensityLevel), feedLevel);
			}
			catch(Exception ex)
			{
				//ignore
				//throw new IllegalArgumentException("Incorrect format for mooshroom data", ex);
			}
		}
	}
	
	/**
	 * @return The addition of all the FlowerEffect.getEffectPoints() from all effects in data
	 */
	public int getTotalPoints()
	{
		int total = 0;
		
		for (FlowerEffect eff : effects.keySet())
		{
			total += eff.getEffectPoints();
		}
		
		return total;
	}
	
	/**
	 * @return a copy of the effects this cow has
	 */
	public List<FlowerEffect> getEffects()
	{
		return new ArrayList<>(effects.keySet());
	}
	
	/**
	 * @return true if no effects in this data, false otherwise
	 */
	public boolean isEmpty()
	{
		return effects.isEmpty();
	}
	
	/**
	 * Returns a FlowerEffect from this data that has specified type. In case of multiple effects with
	 * same type (which is an IllegalState) return first.
	 * 
	 * @param type Type to look for
	 * @return empty optional if no effect with same type was found, or first effect found with same type
	 */
	public Optional<FlowerEffect> getEffect(FlowerEffectType type)
	{
		for (FlowerEffect eff : getEffects())
		{
			if (eff.getType().equals(type))
			{
				return Optional.of(eff);
			}
		}
		
		return Optional.empty();
	}
	
	/**
	 * Adds an effect to the data
	 * @param effect
	 */
	public void addEffect(FlowerEffect effect)
	{
		if (getEffect(effect.getType()).isPresent())
		{
			throw new IllegalArgumentException("Cannot contain two of effects with same type. Use removeEffect(FlowerEffectType) first.");
		}
		
		effects.put(effect, 0);
	}
	
	/**
	 * Removes an effect from data
	 * @param effect type
	 */
	public void removeEffect(FlowerEffectType type)
	{
		for (FlowerEffect effect : getEffects())
		{
			if (effect.getType().equals(type))
			{
				effects.remove(effect);
			}
		}
	}
	
	/**
	 * Removes all weaker effects
	 */
	public void purgeEffects()
	{
		Map<FlowerEffectType, Integer> maxIntensity = new HashMap<>();
		Map<FlowerEffectType, Integer> maxDuration = new HashMap<>();

		// Loop to find most powerful effect
		for (FlowerEffect eff : effects.keySet())
		{
			FlowerEffectType type = eff.getType();
			int intensity = eff.getIntensityLevel();
			int duration = eff.getDurationLevel();
			
			if (maxIntensity.containsKey(type)) // If previous record, test for bigger
			{
				if (maxIntensity.get(type) < intensity) // Intensity bigger than max set this intensity and duration as best
				{
					maxIntensity.put(type, intensity);
					maxDuration.put(type, duration);
				}
				else if (maxIntensity.get(type) == intensity && maxDuration.get(type) < duration) // If same intensity, set better duration as best
				{
					maxDuration.put(type, duration);
				}
			}
			else //if no previous max, set itself as max of both
			{
				maxIntensity.put(type, eff.getIntensityLevel());
				maxDuration.put(type, eff.getDurationLevel());
			}
		}
		
		// Loop to remove weaker effects
		for (FlowerEffect eff : new ArrayList<>(effects.keySet()))
		{
			FlowerEffectType type = eff.getType();
			int intensity = eff.getIntensityLevel();
			int duration = eff.getDurationLevel();
			
			if (duration < 1 || intensity < 1) // Remove illegal states
			{
				effects.remove(eff);
			}
			
			if (intensity == maxIntensity.get(type) && duration == maxDuration.get(type)) // If it's best keep
			{
				maxIntensity.put(type, Integer.MAX_VALUE); // Set an impossible new limit to remove repeated effects
			}
			else
			{
				effects.remove(eff);
			}
		}
	}
	
	/**
	 * Selects a random effect and "downgrades" it. If its intensity and duration level 1, remove it.
	 * If one of the levels is 1 and the other is not, lower level from non-1. If both intensity
	 * and duration levels are over one, lower value to either duration or intensity (random)
	 */
	public void downgradeRandom()
	{
		System.out.println(toString());
		List<FlowerEffect> all = getEffects();
		Collections.shuffle(all);
		FlowerEffect random = all.get(0);
		
		FlowerEffectType type = random.getType();
		int duration = random.getDurationLevel();
		int intensity = random.getIntensityLevel();
		
		removeEffect(random.getType()); // Remove all together, may re-add with weaker levels
		System.out.println(toString());

		if (duration == 1 && intensity > 1)
		{
			addEffect( new FlowerEffect(type, duration, intensity -1) );
		}
		else if (duration > 1 && intensity == 1)
		{
			addEffect( new FlowerEffect(type, duration -1, intensity) );
		}
		else if (duration > 1 && intensity > 1)
		{
			if (Math.random() < 0.5)
			{
				addEffect( new FlowerEffect(type, duration, intensity -1) );
			}
			else
			{
				addEffect( new FlowerEffect(type, duration -1, intensity) );
			}
		}
		System.out.println(toString());

	}

	/**
	 * @param type The effect type that we want to get the feed level of
	 * @return the feed level
	 * 
	 * @throws IllegalArgumentException if effect is not in data
	 */
	public int getFeedLevel(FlowerEffectType type)
	{
		Optional<FlowerEffect> effect = getEffect(type);
		
 		if (!effect.isPresent())
		{
			throw new IllegalArgumentException("That effect is not in the Mooshroom data");
		}
 		
		return effects.get(effect.get());
	}
	
	/**
	 * @param effect The effect where to change feed level
	 * @param level The new feed level
	 * 
	 * @throws IllegalArgumentException if effect is not in data or if level is bellow 0
	 */
	public void setFeedLevel(FlowerEffectType type, int level)
	{
		if (level < 0)
		{
			throw new IllegalArgumentException("Level must be bigger than 0");
		}
		
		Optional<FlowerEffect> effect = getEffect(type);
		
 		if (!effect.isPresent())
		{
			throw new IllegalArgumentException("That effect is not in the Mooshroom data");
		}
		
		effects.put(effect.get(), level);
	}
	
	/**
	 * Set all feed levels to 0
	 */
	public void resetFeedLevels()
	{
		for (FlowerEffect eff : effects.keySet())
		{
			effects.put(eff, 0);
		}
	}
	
	/**
	 * @return true iff the feed level is above or equal the effect points
	 * 
	 * @throws IllegalArgumentException if data does not contain effect of that type
	 */
	public boolean isFull(FlowerEffectType type)
	{
		return getFeedLevel(type) >= getEffect(type).get().getEffectPoints();
	}
	
	/**
	 * @return true if for all effects the feed level is above or equal the effect points. true when data has no effects
	 */
	public boolean isAllFull()
	{
		for (FlowerEffect effect : getEffects())
		{
			if (!isFull(effect.getType()))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		for (FlowerEffect f : effects.keySet())
		{
			builder.append(f.getType().getId());
			builder.append(inSep);
			builder.append(f.getDurationLevel());
			builder.append(inSep);
			builder.append(f.getIntensityLevel());
			builder.append(inSep);
			builder.append(effects.get(f));
			builder.append(effSep);
		}
		return builder.toString();
	}

}
