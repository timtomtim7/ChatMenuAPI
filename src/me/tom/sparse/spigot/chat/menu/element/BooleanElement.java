package me.tom.sparse.spigot.chat.menu.element;

import me.tom.sparse.spigot.chat.menu.ChatMenu;
import me.tom.sparse.spigot.chat.menu.ChatMenuAPI;
import me.tom.sparse.spigot.chat.util.State;
import me.tom.sparse.spigot.chat.util.Text;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BooleanElement extends Element
{
	//	protected boolean value;
	public final State<Boolean> value;
	
	protected ChatColor trueColor  = ChatColor.GREEN;
	protected ChatColor falseColor = ChatColor.RED;
	
	public BooleanElement(int x, int y, boolean value)
	{
		super(x, y);
		this.value = new State<>(value);
	}
	
	public BooleanElement colors(ChatColor trueColor, ChatColor falseColor)
	{
		setTrueColor(trueColor);
		setFalseColor(falseColor);
		return this;
	}
	
	public ChatColor getFalseColor()
	{
		return falseColor;
	}
	
	public void setFalseColor(ChatColor falseColor)
	{
		this.falseColor = falseColor == null ? ChatColor.RED : falseColor;
	}
	
	public ChatColor getTrueColor()
	{
		return trueColor;
	}
	
	public void setTrueColor(ChatColor trueColor)
	{
		this.trueColor = trueColor == null ? ChatColor.GREEN : trueColor;
	}
	
	public int getWidth()
	{
		return 8 + ChatMenuAPI.getWidth(" " + value);
	}
	
	public int getHeight()
	{
		return 1;
	}
	
	public List<Text> render(ChatMenu menu, int elementIndex)
	{
		String baseCommand = menu.getCommand() + elementIndex + " ";
		
		List<BaseComponent> components = new ArrayList<>();
		boolean current = value.current();
		TextComponent c = new TextComponent(current ? "\u2714" : "\u2718");
		c.setColor(current ? trueColor : falseColor);
		c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, baseCommand + !current));
		components.add(c);
		
		components.add(new TextComponent(" " + current));
		
		return Collections.singletonList(new Text(components));
	}
	
	public void edit(ChatMenu menu, String[] args)
	{
		value.set(Boolean.parseBoolean(args[0]));
	}
	
	public boolean getValue()
	{
		return value.current();
	}
	
	public void setValue(boolean value)
	{
		this.value.set(value);
	}
	
	public List<State<?>> getStates()
	{
		return Collections.singletonList(value);
	}
}
