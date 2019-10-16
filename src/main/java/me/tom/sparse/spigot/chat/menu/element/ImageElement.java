package me.tom.sparse.spigot.chat.menu.element;

import me.tom.sparse.spigot.chat.menu.IElementContainer;
import me.tom.sparse.spigot.chat.util.Text;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public class ImageElement extends Element
{
	public static final List<ChatColor> COLORS = Collections.unmodifiableList(Arrays.stream(org.bukkit.ChatColor.values()).filter(org.bukkit.ChatColor::isColor).map(org.bukkit.ChatColor::asBungee).collect(Collectors.toList()));

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
	
	public List<Text> render(IElementContainer context)
	{
		List<Text> result = new ArrayList<>();
		String baseCommand = context.getCommand(this);
		
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
	
	public void edit(@NotNull IElementContainer container, @NotNull String[] args)
	{
		int index = Integer.parseInt(args[0]);
		colors[index] = onPixelClick(index / 20, index % 20, colors[index]);
	}
	
	public interface PixelClickCallback
	{
		int onPixelClick(int x, int y, int currentColor);
	}
}
