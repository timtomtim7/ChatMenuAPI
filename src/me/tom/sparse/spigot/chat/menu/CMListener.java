package me.tom.sparse.spigot.chat.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class CMListener implements Listener
{
	private static Map<Player, BiFunction<Player, String, Boolean>> chatListeners = new ConcurrentHashMap<>();
	
	public static void cancelExpectation(Player player)
	{
		chatListeners.remove(player);
	}
	
	public static void expectPlayerChat(Player player, BiFunction<Player, String, Boolean> function)
	{
		if(player == null || !player.isOnline())
			throw new IllegalArgumentException("Cannot wait for chat for a null/offline player.");
		if(function == null)
			throw new IllegalArgumentException("Cannot call null function.");
		
		chatListeners.put(player, function);
	}
	
	private CMCommand command;
	
	CMListener(Plugin plugin)
	{
		command = new CMCommand();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e)
	{
		Player player = e.getPlayer();
		BiFunction<Player, String, Boolean> listener = chatListeners.get(player);
		if(listener != null)
		{
			e.setCancelled(true);
			if(listener.apply(player, e.getMessage()))
				chatListeners.remove(player);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		cancelExpectation(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent e)
	{
		String cmd = e.getMessage().substring(1);
		if(cmd.length() <= 0) return;
		String[] unprocessedArgs = cmd.split(" ");
		
		String label = unprocessedArgs[0];
		String[] args = new String[unprocessedArgs.length - 1];
		System.arraycopy(unprocessedArgs, 1, args, 0, args.length);
		
		if(label.equalsIgnoreCase("cmapi"))
		{
			e.setCancelled(true);
			command.onCommand(e.getPlayer(), null, label, args);
		}
	}
}
