package com.gmail.ljuangbminecraft.suspiciousmooshroom.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.ljuangbminecraft.suspiciousmooshroom.Config;
import com.gmail.ljuangbminecraft.suspiciousmooshroom.MooshroomItems;

import net.md_5.bungee.api.ChatColor;

/**
 * Command to give players in creative the plugins items.
 * 
 * @author lJuanGB
 */
public class MooshroomConfigCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdName, String[] args) {
		
		if (!cmd.getName().equals("mooshroomconfig")) return true;
				
		if (!sender.isOp())
		{
			return true;
		}
		
		Config.reload();
		MooshroomItems.registerItems();
		sender.sendMessage(ChatColor.AQUA + "Config reloaded succesfully");
		return true;
	}
}
