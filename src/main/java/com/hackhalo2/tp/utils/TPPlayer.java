package com.hackhalo2.tp.utils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TPPlayer {

	private final File playerData; //the plugin data folder
	public final String playerName;
	private List<String> banned;
	private boolean TPT; //TPToggle, False = can | True = cannot
	private boolean override; //TP Override
	private final LinkedList<String> request; //TPR names
	private Location previousLocation; //previous location to TP back to
	private boolean recall = false; //default to false if location isn't valid
	private boolean alert = true; //alert the user about requests and stuff

	public TPPlayer(String name, File dataFolder) {
		this.request = new LinkedList<String>();
		this.playerData = new File(dataFolder, name+".yml");
		this.playerName = name;
		this.load();
	}

	public Player getPlayerReference() {
		return Bukkit.getPlayerExact(playerName);
	}

	public boolean getOverrideStatus() {
		return this.override;
	}

	public void toggleOverride() {
		this.override = !this.override;
	}

	public boolean override() {
		return this.override;
	}

	public boolean alert() {
		return this.alert;
	}

	public void toggleAlert() {
		this.alert = !this.alert;
	}

	public void setAlert(boolean flag) {
		this.alert = flag;
	}

	public boolean canRecall() {
		return this.recall;
	}

	public void setRecallPosition(Location loc) {
		this.recall = true; //make sure that recall is set
		this.previousLocation = loc;
	}

	public Location recall() {
		return this.previousLocation;
	}

	private boolean checkVisibility(Player origin, Player target) {
		if(!(origin.canSee(target)) && target.canSee(origin)) { //if origin cannot see target, fail
			return false;
		} else { //anything other then that, allow
			return true;
		}
	}

	public boolean canBeTPTo(Player target) {
		return (this.canTP()) && this.checkVisibility(this.getPlayerReference(), target);
	}

	public boolean canBeTPHTo(Player origin) {
		return this.checkVisibility(origin, this.getPlayerReference());
	}

	public boolean isTPT() {
		return this.TPT;
	}

	public boolean canTP() {
		return !this.TPT;
	}

	public void toggleTPT() {
		this.TPT = !(this.TPT);
	}

	public void addTPBan(String name) {
		name = name.toLowerCase();
		if(!(this.banned.contains(name))) {
			this.banned.add(name);
		}
	}

	public void removeTPBan(String name) {
		name = name.toLowerCase();
		if(this.banned.contains(name) && (this.banned.indexOf(name) != -1)) {
			this.banned.remove(this.banned.indexOf(name));
		}
	}

	public boolean isTPBanned(String name) {
		name = name.toLowerCase();
		return this.banned.contains(name);
	}

	public String[] getAllRequests() {
		String[] temp = this.request.toArray(new String[0]);
		this.request.clear();
		return temp;
	}

	public synchronized void removeRequest(String name) {
		if(this.request.contains(name)) {
			Bukkit.getLogger().info("[TPToggle] Player "+name+" removed from "+this.playerName+"'s Request Queue");
			this.request.remove(name);
		} else {
			Bukkit.getLogger().info("[TPToggle] Player "+name+" doesn't exist in "+this.playerName+"'s Request Queue");
		}
	}

	public String getRequestName() { //THIS CAN BE NULL!
		String temp = this.request.poll();
		if(temp != null && this.request.contains(temp)) {
			this.request.remove(temp);
		}
		return temp;
	}

	public boolean isRequesting(String name) {
		return this.request.contains(name);
	}

	public void addRequestName(String name) {
		this.request.add(name);
	}

	public void clearRequests() {
		this.request.clear();
	}

	private void load() {
		YamlConfiguration config = new YamlConfiguration();
		double x, y, z;
		String worldname;

		//Check to see if the file exists before trying to load it
		if(this.playerData != null && this.playerData.exists()) {
			try {
				config.load(playerData);
			} catch (Exception e) {
				Bukkit.getLogger().warning("[TPToggle] Error loading "+this.playerName+".yml! Using default values...");
			}
		}

		this.TPT = config.getBoolean("tptoggle.tpt", false);
		this.override = config.getBoolean("tptoggle.override", false);
		//banned user list
		this.banned = config.getStringList("tptoggle.banned");
		//TP Recall
		x = config.getDouble("tptoggle.recall.x", 0);
		y = config.getDouble("tptoggle.recall.y", 0);
		z = config.getDouble("tptoggle.recall.z", 0);
		worldname = config.getString("tptoggle.recall.world", "world");

		this.previousLocation = new Location(Bukkit.getWorld(worldname), x, y, z);

		if(x == 0 && y == 0 && z == 0 && worldname == "world")
			this.recall = false;
		else
			this.recall = true;

		return;
	}

	public void save() {
		YamlConfiguration config = new YamlConfiguration();

		config.set("tptoggle.tpt", this.TPT);
		config.set("tptoggle.override", this.override);
		//banned list
		config.set("tptoggle.banned", this.banned);
		//TP Recall
		config.set("tptoggle.recall.x", this.previousLocation.getX());
		config.set("tptoggle.recall.y", this.previousLocation.getY());
		config.set("tptoggle.recall.z", this.previousLocation.getZ());
		config.set("tptoggle.recall.world", this.previousLocation.getWorld().getName());

		try {
			config.save(playerData);
		} catch (Exception e) {
			Bukkit.getLogger().warning("[TPToggle] Error saving "+this.playerName+".yml! Aborting save!");
			Bukkit.getLogger().info(e.getCause().getMessage());
		}
	}

}
