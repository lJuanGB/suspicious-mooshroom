package com.gmail.ljuangbminecraft.suspiciousmooshroom.commands;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.ljuangbminecraft.suspiciousmooshroom.MooshroomItems;

/**
 * Command to give players in creative the plugins items.
 * 
 * @author lJuanGB
 */
public class MooshroomItemCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdName, String[] args) {
		
		if (!(sender instanceof Player)) return false;

		if (!cmd.getName().equals("mooshroomitem")) return true;
		if ( args.length == 0) return false;
		
		Player player = (Player) sender;
		
		if (!player.getGameMode().equals(GameMode.CREATIVE))
		{
			return true;
		}
		
		Optional<ItemStack> item = MooshroomItems.get(args[0]);
		
		if (item.isPresent())
		{
			player.getInventory().addItem( item.get() );
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Invalid item ID. Valid IDs:");
			for (String s : MooshroomItems.getKeys())
			{
				player.sendMessage(ChatColor.RED + "  - " + s);
			}
		}
		
		return true;
	}
}
