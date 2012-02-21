package com.hackhalo2.tp.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.hackhalo2.tp.TPToggle;

public class TPWorkerThread implements Runnable {

    private final String name;
    private final TPPlayer player;
    private String key;

    public TPWorkerThread(TPPlayer player, String name) {
	this.player = player;
	this.name = name;
    }

    public void setProcessKey(String key) {
	this.key = key;
    }

    public void run() {
	try {
	    if(player.getPlayerReference().isOnline() && Bukkit.getPlayerExact(name).isOnline()) {
		player.removeRequest(name);
		player.getPlayerReference().sendMessage(ChatColor.AQUA+"Player "+ChatColor.GREEN+name+ChatColor.AQUA+" was removed from your request queue");
	    }
	} catch(Exception e) {
	    Bukkit.getLogger().warning("[TPToggle] Exception Caught in Worker Thread!");
	    Bukkit.getLogger().warning(e.getCause().toString());
	    Bukkit.getLogger().warning("[TPToggle] Removing Process...");
	}
	TPToggle.removeProcess(this.key);
    }
}
