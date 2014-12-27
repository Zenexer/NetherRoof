package com.earth2me.netherroof;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NetherRoofPlugin extends JavaPlugin implements Listener
{
	private static final int THRESHOLD = 128;

	@Override
	public void onEnable()
	{
		PluginManager pluginManager = getServer().getPluginManager();

		pluginManager.registerEvents(this, this);

		super.onEnable();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		onPlayerMove(event);
	}

	public boolean isValid(Location location)
	{
		return location.getY() < THRESHOLD || location.getWorld().getEnvironment() != World.Environment.NETHER;
	}

	@SuppressWarnings("UnnecessaryReturnStatement")
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}

		Player player = event.getPlayer();
		Location to = event.getTo();
		Location from = event.getFrom();

		if (isValid(to) || player.isOp())
		{
			// Player is in valid zone or is an operator
			return;
		}

		event.setCancelled(true);

		if (isValid(from))
		{
			// Player was previously in valid zone
			return;
		}

		Location spawn = to.getWorld().getSpawnLocation();
		if (isValid(spawn))
		{
			// Spawn location is valid
			event.getPlayer().teleport(spawn);
			return;
		}

		// Just ignore it for now; player can't move or place blocks anyway.  They'll need to use a command to leave.
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}

		Player player = event.getPlayer();
		Location location = event.getBlockPlaced().getLocation();

		if (isValid(location) || player.isOp())
		{
			// Block is in valid zone or player is an operator
			return;
		}

		event.setCancelled(false);
	}
}
