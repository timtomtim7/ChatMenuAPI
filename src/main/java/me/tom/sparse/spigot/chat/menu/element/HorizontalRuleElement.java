package me.tom.sparse.spigot.chat.menu.element;

import me.tom.sparse.spigot.chat.menu.ChatMenuAPI;
import me.tom.sparse.spigot.chat.menu.IElementContainer;
import me.tom.sparse.spigot.chat.util.Text;
import me.tom.sparse.spigot.chat.util.TextUtil;

import java.util.Collections;
import java.util.List;

public class HorizontalRuleElement extends Element
{
	private static final String text  = "\u00a7m" + TextUtil.generateWidth(' ', 320, false);
	public static final  int    WIDTH = ChatMenuAPI.getWidth(text);
	
	/**
	 * Constructs an element at the given x and y coordinates.
	 *
	 * @param y the y coordinate to put this element at
	 */
	public HorizontalRuleElement(int y)
	{
		super(0, y);
	}
	
	public int getWidth()
	{
		return WIDTH;
	}
	
	public int getHeight()
	{
		return 1;
	}
	
	public List<Text> render(IElementContainer context)
	{
		return Collections.singletonList(new Text(text));
	}
	
	public void edit(IElementContainer container, String[] args)
	{
	
	}
}
