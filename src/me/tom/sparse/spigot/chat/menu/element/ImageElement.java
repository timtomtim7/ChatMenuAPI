package me.tom.sparse.spigot.chat.menu.element;

import me.tom.sparse.spigot.chat.menu.ChatMenu;
import me.tom.sparse.spigot.chat.util.Text;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Deprecated
public class ImageElement extends Element
{
	public static final List<ChatColor> COLORS = Collections.unmodifiableList(Arrays.asList(
			ChatColor.WHITE,
			ChatColor.DARK_RED,
			ChatColor.RED,
			ChatColor.GOLD,
			ChatColor.YELLOW,
			ChatColor.GREEN,
			ChatColor.DARK_GREEN,
			ChatColor.DARK_AQUA,
			ChatColor.BLUE,
			ChatColor.DARK_BLUE,
			ChatColor.DARK_PURPLE,
			ChatColor.LIGHT_PURPLE,
			ChatColor.BLACK
	));
	
	protected int[] colors = new int[20 * 20];
	protected PixelClickCallback callback;
	
	public ImageElement(int x, int y)
	{
		super(x, y);
		Arrays.fill(colors, 0);
	}
	
	public void setCallback(PixelClickCallback callback)
	{
		this.callback = callback;
	}
	
	public int onPixelClick(int x, int y, int currentColor)
	{
		return callback == null ? currentColor : callback.onPixelClick(x, y, currentColor);
	}
	
	public int getPixel(int x, int y)
	{
		if(x < 0 || y < 0 || x >= 20 || y >= 20)
			return -1;
		return colors[x * 20 + y];
	}
	
	public void setPixel(int x, int y, int color)
	{
		if(x < 0 || y < 0 || x >= 20 || y >= 20)
			return;
		colors[x * 20 + y] = color;
	}
	
	public int[] getColors()
	{
		return colors;
	}
	
	public int getWidth()
	{
		return 9 * 20;
	}
	
	public int getHeight()
	{
		return 20;
	}
	
	public boolean isEnabled()
	{
		return true;
	}
	
	public List<Text> render(ChatMenu menu, int elementIndex)
	{
		List<Text> result = new ArrayList<>();
		String baseCommand = menu.getCommand() + elementIndex + " ";
		
		for(int y = 0; y < 20; y++)
		{
			List<BaseComponent> line = new ArrayList<>();
			for(int x = 0; x < 20; x++)
			{
				TextComponent c = new TextComponent("\u2588");
				int colorIndex = x * 20 + y;
				c.setColor(COLORS.get(colors[colorIndex]));
				c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, baseCommand + colorIndex));
				line.add(c);
			}
			
			result.add(new Text(line));
		}
		
		return result;
	}
	
	public void edit(ChatMenu menu, String[] args)
	{
		int index = Integer.parseInt(args[0]);
		colors[index] = onPixelClick(index / 20, index % 20, colors[index]);
	}
	
	public interface PixelClickCallback
	{
		int onPixelClick(int x, int y, int currentColor);
	}
}
