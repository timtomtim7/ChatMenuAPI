package me.tom.sparse.spigot.chat.protocol;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatPacketInterceptor implements Listener
{
	public static final String NMS_VERSION    = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	public static final String NMS_PACKAGE    = "net.minecraft.server." + NMS_VERSION;
	public static final String BUKKIT_PACKAGE = "org.bukkit.craftbukkit." + NMS_VERSION;
	
	private final Field handleField;
	private final Field connectionField;
	private final Field networkManagerField;
	private final Field channelField;
	
	private final Class<?> chatPacketClass;
	private final Field    componentsField;
	private final Field    nmsComponentField;
	
	private final Gson gson;
	
	private Map<Player, PlayerChatIntercept> chats = new ConcurrentHashMap<>();
	
	public PlayerChatIntercept getChat(Player player)
	{
		if(player == null || !player.isOnline())
			return null;
		return chats.computeIfAbsent(player, this::intercept);
	}
	
	public ChatPacketInterceptor(Plugin plugin) throws ReflectiveOperationException
	{
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		Class<?> craftPlayerClass = Class.forName(BUKKIT_PACKAGE + ".entity.CraftEntity");
		Class<?> nmsPlayerClass = Class.forName(NMS_PACKAGE + ".EntityPlayer");
		Class<?> playerConnectionClass = Class.forName(NMS_PACKAGE + ".PlayerConnection");
		Class<?> networkManagerClass = Class.forName(NMS_PACKAGE + ".NetworkManager");
		
		Class<?> chatSerializer = Class.forName(NMS_PACKAGE + ".IChatBaseComponent$ChatSerializer");
		Field gsonField = chatSerializer.getDeclaredField("a");
		gsonField.setAccessible(true);
		gson = (Gson) gsonField.get(null);
		gsonField.setAccessible(false);
		
		
		handleField = craftPlayerClass.getDeclaredField("entity");
		connectionField = nmsPlayerClass.getDeclaredField("playerConnection");
		networkManagerField = playerConnectionClass.getDeclaredField("networkManager");
		channelField = networkManagerClass.getDeclaredField("channel");
		
		chatPacketClass = Class.forName(NMS_PACKAGE + ".PacketPlayOutChat");
		componentsField = chatPacketClass.getDeclaredField("components");
		nmsComponentField = chatPacketClass.getDeclaredField("a");
	}
	
	public void disable()
	{
		chats.keySet().forEach(this::removeIntercept);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		PlayerChatIntercept chat = chats.remove(e.getPlayer());
		if(chat != null)
			removeIntercept(chat.player);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		getChat(e.getPlayer());
	}
	
	private void removeIntercept(Player player)
	{
		Channel channel = getChannel(player);
		if(channel == null) return;
		channel.eventLoop().submit(() -> channel.pipeline().remove(player.getName()));
	}
	
	private PlayerChatIntercept intercept(Player player)
	{
		PlayerChatIntercept chat = new PlayerChatIntercept(this, player);
		
		Channel channel = getChannel(player);
		if(channel == null) return null;
		channel.pipeline().addBefore("packet_handler", player.getName(), chat);
		
		return chat;
	}
	
	private Channel getChannel(Player player)
	{
		handleField.setAccessible(true);
		try
		{
			return (Channel) channelField.get(networkManagerField.get(connectionField.get(handleField.get(player))));
		}catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public BaseComponent[] getComponents(Object chatPacket)
	{
		if(chatPacketClass.isInstance(chatPacket))
		{
			try
			{
				BaseComponent[] components = (BaseComponent[]) componentsField.get(chatPacket);
				if(components != null)
					return components;
				
				nmsComponentField.setAccessible(true);
				Object o = nmsComponentField.get(chatPacket);
				String json = gson.toJson(o);
				
				return ComponentSerializer.parse(json);
			}catch(IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
}
