package me.tom.sparse.spigot.chat.protocol;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayerChatIntercept extends ChannelDuplexHandler
{
	public final ChatPacketInterceptor interceptor;
	public final Player                player;
	
	private Queue<BaseComponent[]> messageQueue    = new ConcurrentLinkedQueue<>();
	private Queue<BaseComponent[]> allowedMessages = null;
	
	private boolean paused;
	
	PlayerChatIntercept(ChatPacketInterceptor interceptor, Player player)
	{
		this.interceptor = interceptor;
		this.player = player;
		
		while(messageQueue.size() < 20)
			messageQueue.add(new BaseComponent[0]);
	}
	
	/**
	 * Sends a message to the player associated with this, regardless of chat being paused.
	 *
	 * @param message the message to send
	 */
	public void sendMessage(BaseComponent... message)
	{
		if(isPaused())
			allowedMessages.add(message);
		player.spigot().sendMessage(message);
	}
	
	public boolean isPaused()
	{
		return paused;
	}
	
	public void pause()
	{
		if(isPaused())
			return;
		paused = true;
		allowedMessages = new ConcurrentLinkedQueue<>();
	}
	
	public void resume()
	{
		if(!isPaused())
			return;
		paused = false;
		
		allowedMessages = null;
		Queue<BaseComponent[]> q = new ConcurrentLinkedQueue<>(messageQueue);
		for(BaseComponent[] components : q)
			player.spigot().sendMessage(components);
	}
	
	public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception
	{
		BaseComponent[] components = interceptor.getComponents(packet);
		if(components != null)
		{
			boolean allowed = isAllowed(components);
			boolean paused = isPaused();
			if(!paused || !allowed)
			{
				while(messageQueue.size() > 20)
					messageQueue.remove();
				messageQueue.add(components);
			}
			
			if(paused && !allowed)
				return;
		}
		
		super.write(context, packet, promise);
	}
	
	public boolean isAllowed(BaseComponent[] message)
	{
		return !isPaused() || allowedMessages.remove(message);
	}
}
