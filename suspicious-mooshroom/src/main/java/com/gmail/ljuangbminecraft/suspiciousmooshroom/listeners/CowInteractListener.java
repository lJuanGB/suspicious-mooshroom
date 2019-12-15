package com.gmail.ljuangbminecraft.suspiciousmooshroom.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.MushroomCow.Variant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import com.gmail.ljuangbminecraft.suspiciousmooshroom.Config;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.FlowerEffect;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.FlowerEffectType;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.MooshroomData;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.UtilMethods;

public class CowInteractListener implements Listener{

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteractCow(PlayerInteractEntityEvent e)
	{
		if (e.getRightClicked().getType().equals(EntityType.MUSHROOM_COW))
		{
			PlayerInventory inv = e.getPlayer().getInventory();
			ItemStack item = e.getHand().equals(EquipmentSlot.HAND) ? inv.getItemInMainHand() : inv.getItemInOffHand();
			
			if (item.getType().equals(Material.BOWL) 
					&& !UtilMethods.getMooshroomData(e.getRightClicked()).isEmpty())
			{
				handleMilkingBowl(item, e);
				e.setCancelled(true);
				
				e.getPlayer().updateInventory();
			}
			else if (UtilMethods.getItemID(item.getItemMeta()).isPresent())
			{
				handleCustomItem(item, e);
				e.setCancelled(true);
			}
			else
			{
				boolean cancel = handleFeed(item, e);
				e.setCancelled( cancel );
			}
			
			if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
			{
				handleTesting(item, e);
			}
		}
		
		if (e.getRightClicked().getType().equals(EntityType.COW))
		{
			if (!Config.allow_convert.get())
			{
				return;
			}
			
			Cow cow = ((Cow) e.getRightClicked());

			if (!cow.hasPotionEffect(PotionEffectType.WEAKNESS))
			{
				return;
			}

			PlayerInventory inv = e.getPlayer().getInventory();
			ItemStack item = e.getHand().equals(EquipmentSlot.HAND) ? inv.getItemInMainHand() : inv.getItemInOffHand();

			if (!item.getType().equals(Material.MUSHROOM_STEW))
			{
				return;
			}

			Location loc = cow.getLocation();
			
			MushroomCow moo = loc.getWorld().spawn(loc, MushroomCow.class);
			moo.setAge( cow.getAge() );
			moo.setCollidable( cow.isCollidable() );
			moo.setCustomName(cow.getCustomName());
			moo.setCustomNameVisible( cow.isCustomNameVisible() );
			moo.setFireTicks( cow.getFireTicks() );
			moo.setHealth( cow.getHealth() );
			moo.setInvulnerable( cow.isInvulnerable() );
			moo.setSilent( cow.isSilent() );
			if (cow.isLeashed())
			{
				moo.setLeashHolder( cow.getLeashHolder() );
			}
			
			cow.remove();
			
			loc.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 1);
			loc.getWorld().spawnParticle(Particle.SPELL, loc, 100, 0.5, 0.5, 0.5, 0);

			if ( Math.random() < Config.chance_brown.get())
			{
				moo.setVariant(Variant.BROWN);
				CowSpawnListener.addRandomEffect(moo);
			}
		}
	}

	private void handleCustomItem(ItemStack item, PlayerInteractEntityEvent e) 
	{
		MushroomCow cow = (MushroomCow) e.getRightClicked();
		MooshroomData data = UtilMethods.getMooshroomData(cow);
		
		String id = UtilMethods.getItemID(item.getItemMeta()).get();
		
		switch (id)
		{
		case "fungi_powder":
			
			MushroomCow baby = cow.getWorld().spawn(cow.getLocation(), MushroomCow.class);
			baby.setBaby();
			data.resetFeedLevels();
			baby.setVariant( cow.getVariant() );
			UtilMethods.setMooshroomData(baby, data);
			
			item.setAmount( item.getAmount() - 1);
			cow.getWorld().spawnParticle(Particle.HEART, cow.getLocation().add(0, 0.5, 0), 20, 0.5,0.5,0.5);
			cow.getWorld().playSound(cow.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1f, 0.5f);
			break;
			
		case "cannibal_fungi":
			
			data.downgradeRandom();
			UtilMethods.setMooshroomData(cow, data);
			
			item.setAmount( item.getAmount() - 1);
			cow.getWorld().spawnParticle(Particle.SPELL_WITCH, cow.getLocation().add(0, 0.5, 0), 20, 0.5,0.5,0.5);
			cow.getWorld().playSound(cow.getLocation(), Sound.ENTITY_PHANTOM_HURT, 0.25f, 0.75f);
			break;
			
		case "floral_forage":
			
			for (FlowerEffect eff : data.getEffects())
			{
				data.setFeedLevel(eff.getType(), eff.getEffectPoints());
			}
			UtilMethods.setMooshroomData(cow, data);
			
			item.setAmount( item.getAmount() - 1);
			cow.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, cow.getLocation().add(0, 0.5, 0), 20, 0.5,0.5,0.5);
			cow.getWorld().playSound(cow.getLocation(), Sound.ENTITY_MOOSHROOM_EAT, 0.25f, 1f);
			break;
		}
		
		if (id.startsWith("potion_"))
		{
			try 
			{
				FlowerEffectType type = FlowerEffectType.valueOf(id.replace("potion_", ""));
				
				MooshroomData newData = new MooshroomData();
				newData.addEffect(new FlowerEffect(type, 1, 1));
				
				item.setAmount( item.getAmount() - 1);
				cow.getWorld().playSound(cow.getLocation(), Sound.ENTITY_WANDERING_TRADER_DRINK_POTION, 0.75f, 1);
				cow.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, cow.getLocation().add(0, 0.5, 0), 20, 0.5,0.5,0.5);
				UtilMethods.setMooshroomData(cow, newData);
			}
			catch (IllegalArgumentException ex)
			{
				//ignore
			}
		}
	}


	private void handleMilkingBowl(ItemStack item, PlayerInteractEntityEvent e) 
	{
		MushroomCow cow = (MushroomCow) e.getRightClicked();
		MooshroomData data = UtilMethods.getMooshroomData(cow);
		
		if (!cow.isAdult()) //Send message and particles if baby
		{
			failureInteraction(cow.getLocation(), e.getPlayer(), Config.lang_baby.get());
			return;
		}
		
		if (!data.isAllFull()) //Send message and particles if not completely fed
		{
			failureInteraction(cow.getLocation(), e.getPlayer(), Config.lang_not_fed.get());
			return;
		}
		
		if (UtilMethods.isInCooldown(cow))
		{
			failureInteraction(cow.getLocation(), e.getPlayer(), Config.lang_cooldown.get());
			return;
		}
		
		data.resetFeedLevels();
		UtilMethods.setMooshroomData(cow, data);
		
		int cooldown = Config.limit_cooldown.get() * data.getTotalPoints();
		System.out.println(Config.limit_cooldown.get() + " * " + data.getTotalPoints());
		UtilMethods.setCooldown(cow, cooldown);
		
		item.setAmount( item.getAmount() - 1);
		ItemStack stew = new ItemStack(Material.SUSPICIOUS_STEW);
		ItemMeta meta = stew.getItemMeta();
		UtilMethods.setMooshroomData(meta, data);
		stew.setItemMeta(meta);
		e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), stew).setPickupDelay(0);
	}
	
	private boolean handleFeed(ItemStack item, PlayerInteractEntityEvent e) 
	{		
		// Check that item is feeding item for SOME effect
		boolean isFeedingItem = false;
		for (FlowerEffectType type : FlowerEffectType.values())
		{
			if (type.getFeedingItem().equals(item.getType()))
			{
				isFeedingItem = true;
				break;
			}
		}
		
		if (!isFeedingItem)
		{
			switch (item.getType()) // If not feeding item but flower anyway, return true (cancel event to avoid eating)
			{
			case ALLIUM:
			case AZURE_BLUET:
			case BLUE_ORCHID:
			case DANDELION:
			case CORNFLOWER:
			case LILY_OF_THE_VALLEY:
			case OXEYE_DAISY:
			case POPPY:
			case RED_TULIP:
			case ORANGE_TULIP:
			case WHITE_TULIP:
			case PINK_TULIP:
			case WITHER_ROSE:
				return true;
			default: return false;
			}
		}
		
		MushroomCow cow = (MushroomCow) e.getRightClicked();
		MooshroomData data = UtilMethods.getMooshroomData(cow);
		
		boolean correctFlower = false;
		
		for (FlowerEffect effect : data.getEffects())
		{
			FlowerEffectType type = effect.getType();
			
			if (type.getFeedingItem().equals(item.getType()))
			{
				correctFlower = true;
				if (data.isFull(type)) // Try to overfeed
				{
					failureInteraction(cow.getLocation(), e.getPlayer(), Config.lang_too_much.get());
					return true;
				}
				else
				{
					data.setFeedLevel(type, data.getFeedLevel(type) + 1);
					UtilMethods.setMooshroomData(cow, data);
					cow.getWorld().playSound(cow.getLocation(), Sound.ENTITY_MOOSHROOM_EAT, 1, 1);
					cow.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, cow.getLocation().add(0, 0.5, 0), 20, 0.5,0.5,0.5);
				}
			}
		}
		
		if (!correctFlower)
		{
			failureInteraction(cow.getLocation(), e.getPlayer(), Config.lang_incorrect_flower.get());
		}
		
		item.setAmount(item.getAmount() - 1);
		return true;
	}


	private void handleTesting(ItemStack item, PlayerInteractEntityEvent e) {
		
		MushroomCow cow = (MushroomCow) e.getRightClicked();
		MooshroomData data = UtilMethods.getMooshroomData(cow);

		switch (item.getType())
		{
		case BLACK_WOOL: 
			e.getPlayer().sendMessage(data.toString());
			break;
		case RED_WOOL:
			e.getPlayer().sendMessage(cow.getAge() + "");
			break;
		case YELLOW_WOOL:
			cow.setBreed(true);
			break;
		case GREEN_WOOL:
			UtilMethods.setCooldown(cow, 0);
			break;
		case LIME_WOOL:
			Long l = cow.getPersistentDataContainer().get(UtilMethods.keyCool, PersistentDataType.LONG);
			e.getPlayer().sendMessage((l == null ? 0 : l) + "");
			break;
		default:
			break;
		}

	}
	
	private void failureInteraction(Location loc, Player p, String message)
	{
		loc.getWorld().playSound(loc, Sound.ENTITY_COW_HURT, 1f, 0.75f);
		loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc.add(0, 0.5, 0), 20, 0.5, 0.5, 0.5, 0.01);
		message = ChatColor.translateAlternateColorCodes('&', message);
		if (!message.isEmpty()) p.sendMessage( message );
	}
}
