package net.TheDgtl.iChatMessages;

/**
 * iChatMessages - A join/quit/kick message formatting plugin for Bukkit.
 * Copyright (C) 2012 Steven "Drakia" Scott <Drakia@Gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import net.TheDgtl.iChat.iChat;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class iChatMessages extends JavaPlugin {
	private Server server;
	private FileConfiguration newConfig;
	private PluginManager pm;
	private iChat ichat;
	
	// Option stuff
	private boolean enableJoin;
	private boolean enableQuit;
	private boolean enableKick;
	private String joinFormat;
	private String quitFormat;
	private String kickFormat;

	public void onEnable() {
		// Load stuffs
		this.server = getServer();
		this.pm = server.getPluginManager();
		this.ichat = (iChat)pm.getPlugin("iChat");
		
		loadConfig();
		
		pm.registerEvents(new playerListener(), this);
	}
	
	private void loadConfig() {
		reloadConfig();
		newConfig = getConfig();
		newConfig.options().copyDefaults(true);
		
		enableJoin = newConfig.getBoolean("enableJoin");
		enableQuit = newConfig.getBoolean("enableQuit");
		enableKick = newConfig.getBoolean("enableKick");
		
		joinFormat = newConfig.getString("joinFormat");
		quitFormat = newConfig.getString("quitFormat");
		kickFormat = newConfig.getString("kickFormat");
		saveConfig();
	}
	
	@SuppressWarnings("unused")
	private class playerListener implements Listener {
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPlayerQuit(PlayerQuitEvent event) {
			if (!enableQuit) return;
			Player p = event.getPlayer();
			// I'm not supposed to do this, but shhh
			ichat.info.addPlayer(p);
			String msg = ichat.API.parseChat(p, "", quitFormat);
			event.setQuitMessage(msg);
		}
		
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPlayerJoin(PlayerJoinEvent event) {
			if (!enableJoin) return;
			Player p = event.getPlayer();
			ichat.info.addPlayer(p);
			String msg = ichat.API.parseChat(p, "", joinFormat);
			event.setJoinMessage(msg);
		}
		
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPlayerKick(PlayerKickEvent event) {
			if (!enableKick) return;
			Player p = event.getPlayer();
			ichat.info.addPlayer(p);
			String msg = ichat.API.parseChat(p, "", kickFormat);
			msg = ichat.API.addColor(msg.replace("+reason", event.getReason()));
			event.setLeaveMessage(msg);
		}
	}
	
	/*
	 * Command Handler
	 */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!command.getName().equalsIgnoreCase("ichatm")) return false;
		if (sender instanceof Player && !sender.hasPermission("ichatm.reload")) {
			sender.sendMessage("[iChat] Permission Denied");
			return true;
		}
		if (args.length != 1) return false;
		
		if (args[0].equalsIgnoreCase("reload")) {
			loadConfig();
			sender.sendMessage("[iChatMessages] Config Reloaded");
			if (sender instanceof Player)
				server.getConsoleSender().sendMessage("[iChatMessages] Config Reloaded");
			return true;
		}
		return false;
	}
	
}
