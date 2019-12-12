package com.gmail.ljuangbminecraft.suspiciousmooshroom;

import org.bukkit.potion.PotionEffect;

/**
 * Represents an effect a mooshroom may have. 
 * This contain information about duration and intensity level, as well as FlowerEffectType.
 * 
 * Immutable.
 * 
 * @author lJuanGB
 */
public class FlowerEffect {

	private final FlowerEffectType type;
	private final int durationLevel;
	private final int intensityLevel;
	
	public FlowerEffect(FlowerEffectType type, int durationLevel, int intensityLevel) {
		this.type = type;
		this.durationLevel = durationLevel;
		this.intensityLevel = intensityLevel;
	}

	public FlowerEffectType getType() {
		return type;
	}
	
	public int getDurationLevel() {
		return durationLevel;
	}
	
	public int getIntensityLevel() {
		return intensityLevel;
	}
	
	/**
	 * Formula is 2^(intensityLevel - 1) * durationLevel;
	 * 
	 * @return how many spots this effect takes out of the total the cow may have.
	 */
	public int getEffectPoints()
	{
		return (int) (Math.pow(2, intensityLevel - 1) * durationLevel);
	}
	
	/**
	 * @return the potion effect that would be applied from putting this effect on a stew
	 */
	public PotionEffect getPotionEffect()
	{
		int duration = type.getBaseDuration() * durationLevel / intensityLevel;
		return new PotionEffect(type.getPotionType(), duration, intensityLevel -1);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof FlowerEffect))
		{
			return false;
		}
		
		FlowerEffect eff2 = (FlowerEffect) obj;
		
		if (!eff2.getType().equals(this.getType())) return false;
		if (eff2.getDurationLevel() != this.getDurationLevel()) return false;
		if (eff2.getIntensityLevel() != this.getIntensityLevel()) return false;
		
		return true;
	}
}
