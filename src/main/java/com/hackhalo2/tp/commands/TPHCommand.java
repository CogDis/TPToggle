package com.hackhalo2.tp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.hackhalo2.tp.TPToggle;

public class TPHCommand implements CommandExecutor {

    private final TPToggle plugin;

    public TPHCommand(TPToggle instance) {
	this.plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command c, String l, String[] args) {
	if(!(cs instanceof Player)) return true; //The Console can't TPHere
	Player player = (Player)cs;
	if(player.hasPermission("tptoggle.request")) {
	    if(args.length == 0) { //This is to respond to /tpc requests
		String temp = this.plugin.getTPPlayer(player).getRequestName();
		if(temp != null) {
		    Player target = Bukkit.getPlayerExact(temp);
		    this.plugin.getTPPlayer(player).setRecallPosition(target.getLocation());
		    target.sendMessage(ChatColor.GRAY+"Teleporting "+ChatColor.GREEN+player.getName()+ChatColor.GRAY+" to you");
		    player.teleport(target, TeleportCause.COMMAND);
		    this.plugin.removeWorker(this.plugin.getTPPlayer(player), target.getName());
		    return true;
		} else {
		    player.sendMessage(ChatColor.AQUA+"Your request queue is empty");
		    return true;
		}
	    } else if(args.length == 1) {
		if(Bukkit.getPlayer(args[0]) != null) {
		    Player target = Bukkit.getPlayer(args[0]);
		    if(this.plugin.getTPPlayer(player).isRequesting(target.getName())) {
			this.plugin.getTPPlayer(player).setRecallPosition(target.getLocation());
			target.sendMessage(ChatColor.GRAY+"Teleporting "+ChatColor.GREEN+player.getName()+ChatColor.GRAY+" to you");
			player.teleport(target, TeleportCause.COMMAND);
			this.plugin.removeWorker(this.plugin.getTPPlayer(player), target.getName());
			return true;
		    } else if(this.plugin.getTPPlayer(player).getOverrideStatus()) {
			player.sendMessage(ChatColor.AQUA+"Teleporting "+ChatColor.GREEN+target.getName()+ChatColor.AQUA+" to you");
			if(target.canSee(player)) target.sendMessage(ChatColor.GRAY+"Force teleporting you to "+ChatColor.GREEN+player.getName());
			this.plugin.getTPPlayer(target).setRecallPosition(target.getLocation());
			target.teleport(player, TeleportCause.COMMAND);
			return true;
		    } else if(this.plugin.getTPPlayer(target).canBeTPHTo(player) && !this.plugin.getTPPlayer(target).isTPBanned(player.getName())) {
			if(this.plugin.getTPPlayer(target).alert() || !player.canSee(target)) {
			    this.plugin.getTPPlayer(target).addRequestName(player.getName());
			    player.sendMessage(ChatColor.AQUA+"Sending teleport call to "+ChatColor.GREEN+target.getName());
			    target.sendMessage(ChatColor.GRAY+player.getName()+" would like to TP to you. Type /tpc to bring them to you");
			    if(!target.canSee(player)) player.sendMessage(ChatColor.GRAY+"Remember to become visible first!");
			    this.plugin.addWorker(this.plugin.getTPPlayer(target), player.getName());
			} else {
			    player.sendMessage(ChatColor.AQUA+"Player "+ChatColor.GREEN+target.getName()+ChatColor.AQUA+" is currently in "+ChatColor.GREEN+"DND mode");
			}
			return true;
		    } else if(this.plugin.getTPPlayer(target).isTPBanned(player.getName())) {
			player.sendMessage(ChatColor.AQUA+"You are not allowed to teleport "+ChatColor.GREEN+target.getName());
			return true;
		    }
		} else {
		    player.sendMessage(ChatColor.AQUA+"Unable to find a match for string "+ChatColor.GREEN+args[0]+ChatColor.AQUA+" (Was null or invalid)");
		    return true;
		}
	    }
	}
	return true;
    }

}
