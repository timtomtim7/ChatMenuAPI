package me.tom.sparse.spigot.chat.menu.element;

import me.tom.sparse.spigot.chat.menu.ChatMenu;
import me.tom.sparse.spigot.chat.menu.ChatMenuAPI;
import me.tom.sparse.spigot.chat.util.NumberFormat;
import me.tom.sparse.spigot.chat.util.State;
import me.tom.sparse.spigot.chat.util.Text;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberSliderElement extends Element
{
	public static final int MIN_PRECISION = 0;
	public static final int MAX_PRECISION = 7;
	
	public final State<Integer> value;
	protected    int            length;
	
	protected ChatColor fullColor  = ChatColor.GREEN;
	protected ChatColor emptyColor = ChatColor.RED;
	
	protected NumberFormat numberFormat = NumberFormat.PERCENTAGE;
	
	protected int precision = 6;
	
	public NumberSliderElement(int x, int y, int length, int value)
	{
		super(x, y);
		this.length = length;
		this.value = new State<>(value, this::filter);
	}
	
	public NumberSliderElement(int x, int y)
	{
		super(x, y);
		this.length = 5;
		this.value = new State<>(0, this::filter);
	}
	
	private int filter(int v)
	{
		return Math.max(Math.min(v, length), 0);
	}
	
	public NumberSliderElement colors(ChatColor fullColor, ChatColor emptyColor)
	{
		setFullColor(fullColor);
		setEmptyColor(emptyColor);
		return this;
	}
	
	public NumberSliderElement hideNumber()
	{
		return numberFormat(NumberFormat.NONE);
	}
	
	public NumberSliderElement numberFormat(NumberFormat format)
	{
		setNumberFormat(format);
		return this;
	}
	
	public NumberFormat getNumberFormat()
	{
		return numberFormat;
	}
	
	public void setNumberFormat(NumberFormat numberFormat)
	{
		this.numberFormat = numberFormat;
	}
	
	public int getPrecision()
	{
		return precision;
	}
	
	public void setPrecision(int precision)
	{
		if(precision < 0 || precision > 7)
			throw new IllegalArgumentException("Precision must be between (inclusive) 0-7");
		this.precision = precision;
	}
	
	public NumberSliderElement precision(int precision)
	{
		setPrecision(precision);
		return this;
	}
	
	public ChatColor getEmptyColor()
	{
		return emptyColor;
	}
	
	public void setEmptyColor(ChatColor emptyColor)
	{
		this.emptyColor = emptyColor == null ? ChatColor.RED : emptyColor;
	}
	
	public ChatColor getFullColor()
	{
		return fullColor;
	}
	
	public void setFullColor(ChatColor fullColor)
	{
		this.fullColor = fullColor == null ? ChatColor.GREEN : fullColor;
	}
	
	public int getLength()
	{
		return length;
	}
	
	public void setLength(int length)
	{
		this.length = length < 0 ? 10 : length;
	}
	
	public void setWidth(int width)
	{
		int charWidth = ChatMenuAPI.getCharacterWidth(getCharacter());
		length = width / charWidth;
	}
	
	public NumberSliderElement width(int width)
	{
		setWidth(width);
		return this;
	}
	
	public int getValue()
	{
		return value.current();
	}
	
	public void setValue(int value)
	{
		this.value.set(value);
	}
	
	public char getCharacter()
	{
		return (char) ('\u2588' + precision);
	}
	
	public int getWidth()
	{
		return ChatMenuAPI.getWidth(String.valueOf(getCharacter())) * length + ChatMenuAPI.getWidth(getFormattedNumber());
	}

//	private String getPercentageString()
//	{
//		return String.format(" %.1f%%", value * 100);
//	}
	
	private String getFormattedNumber()
	{
		return numberFormat == null ? "" : " " + numberFormat.format(value.current(), length);
	}
	
	public int getHeight()
	{
		return 1;
	}
	
	public List<Text> render(ChatMenu menu, int elementIndex)
	{
		String baseCommand = menu.getCommand() + elementIndex + " ";
		
		List<BaseComponent> components = new ArrayList<>();
		for(int i = 0; i < length; i++)
		{
//			double v = (double) (i + 1) / (double) length;
			TextComponent c = new TextComponent(String.valueOf((char) ('\u2588' + precision)));
			c.setColor(i <= value.current() ? isEnabled() ? fullColor : ChatColor.GRAY : isEnabled() ? emptyColor : ChatColor.DARK_GRAY);
			c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, baseCommand + i));
			components.add(c);
		}
		components.add(new TextComponent(getFormattedNumber()));
		
		return Collections.singletonList(new Text(components));
	}
	
	public boolean isEnabled()
	{
		return true;
	}
	
	public boolean onClick(ChatMenu menu, Player player)
	{
		return isEnabled() && super.onClick(menu, player);
	}
	
	public void edit(ChatMenu menu, String[] args)
	{
		if(!isEnabled())
			return;
		value.set(Integer.parseInt(args[0]));
	}
	
	public List<State<?>> getStates()
	{
		return Collections.singletonList(value);
	}
}
