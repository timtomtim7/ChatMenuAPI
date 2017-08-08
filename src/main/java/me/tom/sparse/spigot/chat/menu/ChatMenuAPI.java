package me.tom.sparse.spigot.chat.menu;

import me.tom.sparse.spigot.chat.protocol.ChatPacketInterceptor;
import me.tom.sparse.spigot.chat.protocol.PlayerChatIntercept;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapFont;
import org.bukkit.map.MinecraftFont;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public final class ChatMenuAPI
{
	private static final Map<String, ChatMenu> MENUS        = new ConcurrentHashMap<>();
	private static final Map<Player, ChatMenu> OPENED_MENUS = new ConcurrentHashMap<>();
	
	private static Plugin                plugin;
	private static ChatPacketInterceptor interceptor;
	
	private ChatMenuAPI() {}
	
	/**
	 * @param player the player whose current menu should be returned
	 * @return the menu the player currently has open, or {@code null} if no menu is open.
	 */
	@Nullable
	public static ChatMenu getCurrentMenu(@Nonnull Player player)
	{
		return OPENED_MENUS.get(player);
	}
	
	/**
	 * @param player the player whose current menu should be returned
	 * @param menu   the menu to set as current, or {@code null} if you want to close the current menu.
	 */
	public static void setCurrentMenu(@Nonnull Player player, @Nullable ChatMenu menu)
	{
		ChatMenu old = OPENED_MENUS.remove(player);
		if(old != null && old != menu) old.onClosed(player);
		if(menu != null) OPENED_MENUS.put(player, menu);
	}

	@Nonnull
	static String registerMenu(ChatMenu menu)
	{
		String id = generateIdentifier();
		MENUS.put(id, menu);
		return id;
	}

	static void unregisterMenu(@Nonnull ChatMenu menu)
	{
		MENUS.values().remove(menu);
	}

	@Nonnull
	private static String generateIdentifier()
	{
		String result = null;
		while(result == null || MENUS.containsKey(result))
		{
			int[] ints = ThreadLocalRandom.current().ints(4, 0x2700, 0x27BF).toArray();
			result = new String(ints, 0, ints.length);
		}
		
		return result;
	}
	
	/**
	 * Gets the current {@link PlayerChatIntercept} associated with the provided player.
	 * If the player does not have one, it will be created.
	 *
	 * @param player the player to get/create the {@link PlayerChatIntercept} for
	 * @return the {@link PlayerChatIntercept} associated with the provided player
	 */
	@Nonnull
	public static PlayerChatIntercept getChatIntercept(@Nonnull Player player)
	{
		return interceptor.getChat(player);
	}
	
	/**
	 * Calculates the width of the provided text.
	 * <br>
	 * Works with formatting codes such as bold.
	 *
	 * @param text the text to calculate the width for
	 * @return the number of pixels in chat the text takes up
	 */
	public static int getWidth(@Nonnull String text)
	{
		if(text.contains("\n"))
			throw new IllegalArgumentException("Cannot get width of text containing newline");
		
		int width = 0;
		
		boolean isBold = false;
		
		char[] chars = text.toCharArray();
		for(int i = 0; i < chars.length; i++)
		{
			char c = chars[i];
			int charWidth = getCharacterWidth(c);
			
			if(c == ChatColor.COLOR_CHAR && i < chars.length - 1)
			{
				c = chars[++i];
				
				if(c != 'l' && c != 'L')
				{
					if(c == 'r' || c == 'R')
					{
						isBold = false;
					}
				}else
				{
					isBold = true;
				}
				
				charWidth = 0;
			}
			
			if(isBold && c != ' ' && charWidth > 0)
			{
				width++;
			}
			
			width += charWidth;
		}
		
		return width;
	}
	
	/**
	 * @param c the character to get the width of
	 * @return the width of the provided character in pixels
	 */
	public static int getCharacterWidth(char c)
	{
		if(c >= '\u2588' && c <= '\u258F')
		{
			return ('\u258F' - c) + 2;
		}
		
		switch(c)
		{
			case ' ':
				return 4;
			case '\u2714':
				return 8;
			case '\u2718':
				return 7;
			default:
				MapFont.CharacterSprite mcChar = MinecraftFont.Font.getChar(c);
				if(mcChar != null)
					return mcChar.getWidth() + 1;
				return 0;
		}
	}
	
	static ChatMenu getMenu(String id)
	{
		return MENUS.get(id);
	}
	
	/**
	 * <b>This method should only be called by you if you're including this API inside your plugin.</b>
	 * <br>
	 * Initializes all the necessary things for the ChatMenuAPI to function. This method can only be called once.
	 *
	 * @param plugin the plugin to initialize everything with, including listeners and scheduled tasks
	 */
	public static void init(@Nonnull Plugin plugin)
	{
		if(ChatMenuAPI.plugin != null)
			return;
		
		ChatMenuAPI.plugin = plugin;
//		Bukkit.getPluginCommand("cmapi").setExecutor(new CMCommand());
		CMCommand.setLoggerFilter();
		new CMListener(plugin);
		
		try
		{
			interceptor = new ChatPacketInterceptor(plugin);
		}catch(ReflectiveOperationException e)
		{
			plugin.getLogger().severe("Unable to create ChatPacketInterceptor! The ChatMenuAPI will not function properly!");
			e.printStackTrace();
		}
	}
	
	/**
	 * <b>This method should only be called by you if you're including this API inside your plugin.</b>
	 * <br>
	 * Disables everything necessary for this API to be reloaded properly without restarting.
	 */
	public static void disable()
	{
		if(plugin == null)
			return;
		
		CMCommand.restoreLoggerFilter();
		plugin = null;
		interceptor.disable();
	}
}
