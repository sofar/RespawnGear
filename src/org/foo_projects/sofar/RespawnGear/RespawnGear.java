package org.foo_projects.sofar.RespawnGear;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class RespawnGear extends JavaPlugin {
	
	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;
	private ItemStack bag[];

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new RespawnGearListener(this), this);
		getCommand("respawngear").setExecutor(new RespawnGearCommand(this));
		
		this.bag = new ItemStack[36];

		this.saveDefaultConfig();

		for (int i = 0; i < 36; i++)
			bag[i] = getConfig().getItemStack(String.format("bag%d", i));

		helmet = getConfig().getItemStack("helmet");
		chestplate = getConfig().getItemStack("chestplate");
		leggings = getConfig().getItemStack("leggings");
		boots = getConfig().getItemStack("boots");
	}
	
	public boolean emptygear(Player player) {
		for (ItemStack item : player.getInventory().getContents())
			if (item != null)
				if (item.getAmount() != 0)
					return false;
		for (ItemStack item : player.getInventory().getArmorContents())
			if (item != null)
				if (item.getAmount() != 0)
					return false;
		return true;
	}
	
	public void GiveGear(Player player) {
		if (emptygear(player) == false)
			return;
		
		for (int i = 0; i < 36; i++) {
			ItemStack is = getConfig().getItemStack(String.format("bag%d", i));
			player.getInventory().setItem(i, is);
		}

		player.getInventory().setHelmet(helmet);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setBoots(boots);
	}
	
	public void SaveGear(Player player) {
		for (int i = 0; i < 36; i++) {
			bag[i] = player.getInventory().getItem(i);
			getConfig().set(String.format("bag%d", i), bag[i]);
		}

		helmet = player.getInventory().getHelmet();
		getConfig().set("helmet", helmet);
		chestplate = player.getInventory().getChestplate();
		getConfig().set("chestplate", chestplate);
		leggings = player.getInventory().getLeggings();
		getConfig().set("leggings", leggings);
		boots = player.getInventory().getBoots();
		getConfig().set("boots", boots);

		saveConfig();
		player.sendMessage("Inventory saved...");
	}
}

class RespawnGearListener implements Listener {
	private final RespawnGear plugin;

	public RespawnGearListener(RespawnGear plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void RespawnEvent(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		plugin.GiveGear(player);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void JoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		plugin.GiveGear(player);
	}
}

class RespawnGearCommand implements CommandExecutor {
	private final RespawnGear plugin;

	public RespawnGearCommand(RespawnGear plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;
		plugin.SaveGear(player);
		return true;
	}
}
