package me.tom.sparse.spigot.chat.menu.element;

import me.tom.sparse.spigot.chat.menu.ChatMenuAPI;
import me.tom.sparse.spigot.chat.menu.IElementContainer;
import me.tom.sparse.spigot.chat.util.State;
import me.tom.sparse.spigot.chat.util.Text;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A boolean element. Basically a checkbox (without the box).
 */
public class BooleanElement extends Element
{
	//	protected boolean value;
	public final State<Boolean> value;
	
	protected ChatColor trueColor  = ChatColor.GREEN;
	protected ChatColor falseColor = ChatColor.RED;
	
	protected boolean showText = false;
	
	/**
	 * Constructs a {@code BooleanElement}
	 *
	 * @param x     the x coordinate
	 * @param y     the y coordinate
	 * @param value the starting value
	 */
	public BooleanElement(int x, int y, boolean value)
	{
		super(x, y);
		this.value = new State<>(value);
	}
	
	/**
	 * Shows the "true/false" text after the symbol.
	 *
	 * @return this
	 */
	public BooleanElement showText()
	{
		setShowText(true);
		return this;
	}
	
	/**
	 * @param showText whether or not to show the "true/false" text
	 */
	public void setShowText(boolean showText)
	{
		this.showText = showText;
	}
	
	/**
	 * @param trueColor  The color the symbol should be if the value is {@code true}
	 * @param falseColor The color the symbol should be if the value is {@code false}
	 * @return this
	 */
	public BooleanElement colors(ChatColor trueColor, ChatColor falseColor)
	{
		setTrueColor(trueColor);
		setFalseColor(falseColor);
		return this;
	}
	
	/**
	 * @return the color the text will be if the value is {@code false}
	 */
	public ChatColor getFalseColor()
	{
		return falseColor;
	}
	
	/**
	 * @param falseColor the color the symbol should be if the value is {@code false}
	 */
	public void setFalseColor(ChatColor falseColor)
	{
		this.falseColor = falseColor == null ? ChatColor.RED : falseColor;
	}
	
	/**
	 * @return the color the text will be if the value is {@code true}
	 */
	public ChatColor getTrueColor()
	{
		return trueColor;
	}
	
	/**
	 * @param trueColor The color the symbol should be if the value is {@code true}
	 */
	public void setTrueColor(ChatColor trueColor)
	{
		this.trueColor = trueColor == null ? ChatColor.GREEN : trueColor;
	}
	
	public int getWidth()
	{
		return 8 + (showText ? ChatMenuAPI.getWidth(" " + value) : 0);
	}
	
	public int getHeight()
	{
		return 1;
	}
	
	public List<Text> render(IElementContainer context)
	{
		String baseCommand = context.getCommand(this);
		
		List<BaseComponent> components = new ArrayList<>();
		boolean current = value.current();
		TextComponent c = new TextComponent(current ? "\u2714" : "\u2718");
		c.setColor(current ? trueColor : falseColor);
		c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, baseCommand + !current));
		components.add(c);
		
		if(showText)
			components.add(new TextComponent(" " + current));
		
		return Collections.singletonList(new Text(components));
	}
	
	public void edit(IElementContainer container, String[] args)
	{
		value.set(Boolean.parseBoolean(args[0]));
	}
	
	/**
	 * @return the current value
	 */
	public boolean getValue()
	{
		return value.current();
	}
	
	/**
	 * @param value the new value
	 */
	public void setValue(boolean value)
	{
		this.value.set(value);
	}
	
	public List<State<?>> getStates()
	{
		return Collections.singletonList(value);
	}
}
