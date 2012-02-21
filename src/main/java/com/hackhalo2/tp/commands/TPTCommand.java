package com.hackhalo2.tp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hackhalo2.tp.TPToggle;

public class TPTCommand implements CommandExecutor {

    private TPToggle plugin = null;

    public TPTCommand(TPToggle instance) {
	this.plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command c, String l, String[] args) {
	if(!(cs instanceof Player)) return true; //The Console can't TPToggle
	Player player = (Player)cs;
	if(player.hasPermission("tptoggle.tpt")) {
	    if(args.length == 0) {
		this.plugin.getTPPlayer(player).toggleTPT();
		player.sendMessage(ChatColor.AQUA+"TPToggle: "+ChatColor.GREEN+(this.plugin.getTPPlayer(player).canTP() ? "Disabled" : "Enabled"));
		return true;
	    } else if(args.length == 1) {
		if(args[0].equalsIgnoreCase("ban")) {
		    player.sendMessage(ChatColor.AQUA+"Correct Usage: "+ChatColor.GREEN+"/tpt ban <playername>");
		    return true;
		}
		if(args[0].equalsIgnoreCase("pardon")) {
		    player.sendMessage(ChatColor.AQUA+"Correct Usage: "+ChatColor.GREEN+"/tpt pardon <playername>");
		    return true;
		}
		return true;
	    } else if(args.length == 2) {
		if(Bukkit.getPlayer(args[1]) != null) {
		    Player target = Bukkit.getPlayer(args[1]);
		    if(args[0].equalsIgnoreCase("ban")) {
			if(this.plugin.getTPPlayer(player).isTPBanned(target.getName())) {
			    player.sendMessage(ChatColor.AQUA+"Player "+ChatColor.GREEN+target.getName()+ChatColor.AQUA+" already cannot teleport to you");
			    return true;
			} else {
			    this.plugin.getTPPlayer(player).addTPBan(target.getName());
			    player.sendMessage(ChatColor.AQUA+"Player "+ChatColor.GREEN+target.getName()+ChatColor.AQUA+" cannot teleport to you");
			    return true;
			}
		    }
		    if(args[0].equalsIgnoreCase("pardon")) {
			if(this.plugin.getTPPlayer(player).isTPBanned(target.getName())) {
			    this.plugin.getTPPlayer(player).removeTPBan(target.getName());
			    player.sendMessage(ChatColor.AQUA+"Player "+ChatColor.GREEN+target.getName()+ChatColor.AQUA+" can teleport to you");
			    return true;
			} else {
			    player.sendMessage(ChatColor.AQUA+"Player "+ChatColor.GREEN+target.getName()+ChatColor.AQUA+" wasn't listed for you");
			    return true;
			}
		    }
		    if(args[0].equalsIgnoreCase("override") && player.hasPermission("tptoggle.tpt.override")) {
			this.plugin.getTPPlayer(target).isTPT();
			player.sendMessage(ChatColor.AQUA+"Overriding "+ChatColor.GREEN+target.getName()+ChatColor.AQUA+" TPT settings");
			target.sendMessage(ChatColor.GRAY+"Your TPT settings have been overridden by "+player.getName()+" to "+ChatColor.GREEN+
				(this.plugin.getTPPlayer(target).canTP() ? "Disabled" : "Enabled"));
			return true;
		    }
		} else {
		    player.sendMessage(ChatColor.AQUA+"Unable to find a match for string "+ChatColor.GREEN+args[1]+ChatColor.AQUA+" (Was null)");
		    return true;
		}
	    } else {
		player.sendMessage(ChatColor.AQUA+"Usage: "+ChatColor.GREEN+"/tpt "+ChatColor.GOLD+"| "+ChatColor.GREEN+"/tpt [ban|pardon] [player]");
		return true;
	    }
	}
	return true;
    }
}
