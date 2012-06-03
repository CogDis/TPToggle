package com.hackhalo2.tp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hackhalo2.tp.TPToggle;
import com.hackhalo2.tp.utils.TPPlayer;
import com.hackhalo2.tp.utils.TPUtil;

public class TPCommand implements CommandExecutor {

	private final TPToggle plugin;

	public TPCommand(TPToggle instance) {
		this.plugin = instance;
	}

	public boolean onCommand(CommandSender cs, Command c, String l, String[] args) {
		if(!(cs instanceof Player)) return true;
		Player origin = (Player)cs;

		boolean tpo = false;
		
		if(c.getName().equals("tpo")) {
			if(origin.hasPermission("tptoggle.tpo")) tpo = true;
			else {
				origin.sendMessage(ChatColor.AQUA+"You do not have permission to "+ChatColor.GREEN+"override teleports");
				return true;
			}
		}

		if(origin.hasPermission("tptoggle.tp")) {
			if(args.length == 0) {
				origin.sendMessage(ChatColor.AQUA+"Correct Usage: "+ChatColor.GREEN+"/tp <playername>");
				return true;
			} else if(args.length == 1) {
				if(Bukkit.getPlayer(args[0]) != null) {
					Player target = Bukkit.getPlayer(args[0]);
					TPUtil.TPOriginToTarget(origin, target, tpo);
				} else if(args[0].contains(",") && origin.hasPermission("tptoggle.tp.coords")) {
					String[] split = args[0].split(",", -1);
					if(split.length != 3) {
						origin.sendMessage(ChatColor.AQUA+"Usage: "+ChatColor.GREEN+"/tp [x],[y],[z]");
						return true;
					} else {
						double x, y, z; //the coords
						Location loc; //the location
						try {
							if(split[0].length() == 0)
								x = origin.getLocation().getX();
							else
								x = Double.parseDouble(split[0]);

							if(split[1].length() == 0)
								y = origin.getLocation().getY();
							else
								y = Double.parseDouble(split[1]);

							if(split[2].length() == 0)
								z = origin.getLocation().getZ();
							else
								z = Double.parseDouble(split[2]);

							loc = new Location(origin.getWorld(), x, y, z);

							if(loc.getBlock().getType().equals(Material.AIR)) {
								origin.sendMessage(ChatColor.AQUA+"Teleporting to "+ChatColor.GREEN+x+","+y+","+z);
								this.plugin.getTPPlayer(origin).setRecallPosition(origin.getLocation());
								origin.teleport(loc);
								return true;
							} else {
								origin.sendMessage(ChatColor.AQUA+"Block at location wasn't Air! Was "+ChatColor.GREEN+loc.getBlock().getType().name());
								return true;
							}
						} catch(NumberFormatException e) {
							origin.sendMessage(ChatColor.AQUA+"Usage: "+ChatColor.GREEN+"/tp [x],[y],[z]");
							return true;
						}
					}
				} else if(args[0].contentEquals("#recall") && this.plugin.getTPPlayer(origin).canRecall()) { //TP Recall
					origin.sendMessage(ChatColor.AQUA+"Teleporting back to your "+ChatColor.GREEN+"previous location");
					origin.teleport(this.plugin.getTPPlayer(origin).recall());
					return true;
				} else if(args[0].contentEquals("#alert")) {
					this.plugin.getTPPlayer(origin).toggleAlert();
					origin.sendMessage(ChatColor.AQUA+"Do Not Disturb mode is "+ChatColor.GREEN+(this.plugin.getTPPlayer(origin).alert() ? "Disabled" : "Enabled"));
					return true;
				} else if(args[0].contentEquals("#override") && origin.hasPermission("tptoggle.override")) {
					this.plugin.getTPPlayer(origin).toggleOverride();
					origin.sendMessage(ChatColor.AQUA+"Force Override mode is "+ChatColor.GREEN+(this.plugin.getTPPlayer(origin).override() ? "Off" : "On"));
				} else if((args[0].contentEquals("#answer") || args[0].contentEquals("#yes")) && origin.hasPermission("tptoggle.request")) {
					TPPlayer tpOrigin = this.plugin.getTPPlayer(origin);
					String temp = tpOrigin.getRequestName();
					if(temp != null) {
						Player target = Bukkit.getPlayerExact(temp);
						this.plugin.removeWorker(tpOrigin, target.getName());
						return TPUtil.TPTargetToOrigin(target, origin);
					} else {
						origin.sendMessage(ChatColor.AQUA+"Your request queue is empty");
						return true;
					}
				} else if(args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("pardon")) {
					if(args[0].equalsIgnoreCase("ban")) {
						origin.sendMessage(ChatColor.AQUA+"Correct Usage: "+ChatColor.GREEN+"/tp ban <playername>");
						return true;
					} else if(args[0].equalsIgnoreCase("pardon")) {
						origin.sendMessage(ChatColor.AQUA+"Correct Usage: "+ChatColor.GREEN+"/tp pardon <playername>");
						return true;
					}
				} else {
					origin.sendMessage(ChatColor.AQUA+"Unable to find a match for string "+ChatColor.GREEN+args[0]+ChatColor.AQUA+" (Was null or invalid)");
					return true;
				}
			} else if(args.length == 2) {
				if(Bukkit.getPlayer(args[1]) != null) {
					Player target = Bukkit.getPlayer(args[1]);
					if(args[0].equalsIgnoreCase("ban")) {
						if(this.plugin.getTPPlayer(origin).isTPBanned(target.getName())) {
							origin.sendMessage(ChatColor.AQUA+"Player "+ChatColor.GREEN+target.getName()+ChatColor.AQUA+" already cannot teleport to you");
							return true;
						} else {
							this.plugin.getTPPlayer(origin).addTPBan(target.getName());
							origin.sendMessage(ChatColor.AQUA+"Player "+ChatColor.GREEN+target.getName()+ChatColor.AQUA+" cannot teleport to you");
							return true;
						}
					}
					if(args[0].equalsIgnoreCase("pardon")) {
						if(this.plugin.getTPPlayer(origin).isTPBanned(target.getName())) {
							this.plugin.getTPPlayer(origin).removeTPBan(target.getName());
							origin.sendMessage(ChatColor.AQUA+"Player "+ChatColor.GREEN+target.getName()+ChatColor.AQUA+" can teleport to you");
							return true;
						} else {
							origin.sendMessage(ChatColor.AQUA+"Player "+ChatColor.GREEN+target.getName()+ChatColor.AQUA+" wasn't listed for you");
							return true;
						}
					}
				}
			}
		}
		return true;
	}

}
