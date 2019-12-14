package com.gmail.ljuangbminecraft.suspiciousmooshroom.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.ljuangbminecraft.suspiciousmooshroom.Config;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.MooshroomItems;

/**
 * Command to give players in creative the plugins items.
 * 
 * @author lJuanGB
 */
public class MooshroomConfigCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdName, String[] args) {
		
		if (!(sender instanceof Player)) return false;

		if (!cmd.getName().equals("mooshroomconfig")) return true;
				
		if (!sender.isOp())
		{
			return true;
		}
		
		Config.reload();
		MooshroomItems.registerItems();
		return true;
	}
}
