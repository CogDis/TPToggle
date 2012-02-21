package com.hackhalo2.tp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.hackhalo2.tp.utils.TPPlayer;

public class TPTListener implements Listener {
    
    private TPToggle plugin = null;

    protected TPTListener(TPToggle instance) {
	this.plugin = instance;
    }
    
    @EventHandler
    public void loginHandler(PlayerJoinEvent e) {
	if(!this.plugin.playerReferences.containsKey(e.getPlayer().getName())) {
	    TPPlayer temp = new TPPlayer(e.getPlayer().getName(), this.plugin.playerDataFolder);
	    this.plugin.playerReferences.put(e.getPlayer().getName(), temp);
	    temp = null;
	} else {
	    this.plugin.log.warning("[TPToggle] Player "+e.getPlayer().getName()+" already had a TPPlayer reference loaded!");
	}
    }
    
    @EventHandler
    public void partHandler(PlayerQuitEvent e) {
	if(this.plugin.playerReferences.containsKey(e.getPlayer().getName())) {
	    TPPlayer temp = this.plugin.playerReferences.get(e.getPlayer().getName());
	    temp.save();
	    this.plugin.playerReferences.remove(e.getPlayer().getName());
	    temp = null;
	} else {
	    this.plugin.log.warning("[TPToggle] Player "+e.getPlayer().getName()+" didn't have a TPPlayer reference!");
	}
    }
    
    @EventHandler
    public void teleportHandler(PlayerTeleportEvent e) {
	this.plugin.getTPPlayer(e.getPlayer()).setRecallPosition(e.getFrom());
    }
}
