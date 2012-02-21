package com.hackhalo2.tp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.hackhalo2.tp.TPToggle;

public class TPCommand implements CommandExecutor {

    private final TPToggle plugin;

    public TPCommand(TPToggle instance) {
	this.plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command c, String l, String[] args) {
	if(!(cs instanceof Player)) return true; //The Console can't TP
	Player player = (Player)cs;
	boolean override = false;
	if(c.getName().equalsIgnoreCase("tpo") && player.hasPermission("tptoggle.override"))
	    override = true;
	if(player.hasPermission("tptoggle.tp")) {
	    if(args.length == 0) {
		player.sendMessage(ChatColor.AQUA+"Correct Usage: "+ChatColor.GREEN+"/tp <playername>");
		return true;
	    } else if(args.length == 1) {
		if(Bukkit.getPlayer(args[0]) != null) {
		    Player target = Bukkit.getPlayer(args[0]);
		    if(this.plugin.getTPPlayer(player).getOverrideStatus() || override) {
			player.sendMessage(ChatColor.AQUA+"Teleporting to "+ChatColor.GREEN+target.getName());
			if(target.canSee(player)) target.sendMessage(ChatColor.GREEN+player.getName()+ChatColor.GRAY+" force teleported to you.");
			this.plugin.getTPPlayer(player).setRecallPosition(player.getLocation());
			player.teleport(target, TeleportCause.COMMAND);
			return true;
		    } else {
			if(this.plugin.getTPPlayer(target).canBeTPTo(player) && !this.plugin.getTPPlayer(target).isTPBanned(player.getName())) {
			    player.sendMessage(ChatColor.AQUA+"Teleporting to "+ChatColor.GREEN+target.getName());
			    if(target.canSee(player)) target.sendMessage(ChatColor.GREEN+player.getName()+ChatColor.GRAY+" teleported to you.");
			    this.plugin.getTPPlayer(player).setRecallPosition(player.getLocation());
			    player.teleport(target, TeleportCause.COMMAND);
			    return true;
			} else if(this.plugin.getTPPlayer(target).isTPBanned(player.getName())) {
			    player.sendMessage(ChatColor.AQUA+"You are not allowed to teleport to "+ChatColor.GREEN+target.getName());
			    return true;
			} else if(this.plugin.getTPPlayer(target).isTPT()) {
			    if(this.plugin.getTPPlayer(target).alert() || !player.canSee(target)) {
				this.plugin.getTPPlayer(target).addRequestName(player.getName());
				player.sendMessage(ChatColor.AQUA+"Sending teleport request to "+ChatColor.GREEN+target.getName());
				target.sendMessage(ChatColor.GRAY+player.getName()+" would like to TP to you. Type /tph to bring them");
				if(!player.canSee(target)) target.sendMessage(ChatColor.GRAY+"Remember to become visible first!");
				this.plugin.addWorker(this.plugin.getTPPlayer(target), player.getName());
			    } else
				player.sendMessage(ChatColor.AQUA+"Player "+ChatColor.GREEN+target.getName()+ChatColor.AQUA+" is currently in "+ChatColor.GREEN+"DND mode");
			    return true;
			}
		    }
		    //TODO: Add a TP to world location
		} else if(args[0].contains(",") && player.hasPermission("tptoggle.tp.coords")) {
		    String[] split = args[0].split(",", -1);
		    if(split.length != 3) {
			player.sendMessage(ChatColor.AQUA+"Usage: "+ChatColor.GREEN+"/tp [x],[y],[z]");
			return true;
		    } else {
			double x, y, z; //the coords
			Location loc; //the location
			try {
			    if(split[0].length() == 0)
				x = player.getLocation().getX();
			    else
				x = Double.parseDouble(split[0]);

			    if(split[1].length() == 0)
				y = player.getLocation().getY();
			    else
				y = Double.parseDouble(split[1]);

			    if(split[2].length() == 0)
				z = player.getLocation().getZ();
			    else
				z = Double.parseDouble(split[2]);

			    loc = new Location(player.getWorld(), x, y, z);

			    if(loc.getBlock().getType().equals(Material.AIR)) {
				player.sendMessage(ChatColor.AQUA+"Teleporting to "+ChatColor.GREEN+x+","+y+","+z);
				this.plugin.getTPPlayer(player).setRecallPosition(player.getLocation());
				player.teleport(loc);
				return true;
			    } else {
				player.sendMessage(ChatColor.AQUA+"Block at location wasn't Air! Was "+ChatColor.GREEN+loc.getBlock().getType().name());
				return true;
			    }
			} catch(NumberFormatException e) {
			    player.sendMessage(ChatColor.AQUA+"Usage: "+ChatColor.GREEN+"/tp [x],[y],[z]");
			    return true;
			}
		    }
		} else if(args[0].contentEquals("#recall") && this.plugin.getTPPlayer(player).canRecall()) { //TP Recall
		    player.sendMessage(ChatColor.AQUA+"Teleporting back to your "+ChatColor.GREEN+"previous location");
		    player.teleport(this.plugin.getTPPlayer(player).recall());
		    return true;
		} else if(args[0].contentEquals("#alert")) {
		    this.plugin.getTPPlayer(player).toggleAlert();
		    player.sendMessage(ChatColor.AQUA+"Do Not Disturb mode is "+ChatColor.GREEN+(this.plugin.getTPPlayer(player).alert() ? "Disabled" : "Enabled"));
		    return true;
		} else if(args[0].contentEquals("#override") && player.hasPermission("tptoggle.override")) {
		    this.plugin.getTPPlayer(player).toggleOverride();
		    player.sendMessage(ChatColor.AQUA+"Force Override mode is "+ChatColor.GREEN+(this.plugin.getTPPlayer(player).override() ? "Off" : "On"));
		} else {
		    player.sendMessage(ChatColor.AQUA+"Unable to find a match for string "+ChatColor.GREEN+args[0]+ChatColor.AQUA+" (Was null or invalid)");
		    return true;
		}
	    }
	}
	return true;
    }

}
