package me.tom.sparse.spigot.chat.menu.element;

import me.tom.sparse.spigot.chat.menu.ChatMenuAPI;
import me.tom.sparse.spigot.chat.menu.IElementContainer;
import me.tom.sparse.spigot.chat.util.State;
import me.tom.sparse.spigot.chat.util.Text;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A vertical list of options. Similar to a column of radio buttons.
 */
public class VerticalSelectorElement extends Element
{
	protected static final int SELECTED_PREFIX_WIDTH = ChatMenuAPI.getWidth("> ");

	@Nonnull
	protected String[] options;
	protected int      width;
	
	//	protected int selectedIndex;
	@Nonnull
	public final State<Integer> value;

	@Nonnull
	protected ChatColor selectedColor = ChatColor.GREEN;
	
	/**
	 * Constructs a {@code VerticalSelectorElement}
	 *
	 * @param x               the x coordinate
	 * @param y               the y coordinate
	 * @param defaultSelected the selected option index
	 * @param options         the list of options. Options may not contain {@code \n}
	 */
	public VerticalSelectorElement(int x, int y, int defaultSelected, @Nonnull String... options)
	{
		super(x, y);
		for(String option : options)
		{
			if(option.contains("\n"))
				throw new IllegalArgumentException("Option cannot contain newline");
			
			int w = ChatMenuAPI.getWidth(option);
			if(w > width)
				width = w;
		}
		
		this.options = options;
		this.value = new State<>(defaultSelected, this::filter);
	}
	
	private int filter(int v)
	{
		return Math.max(Math.min(v, options.length), 0);
	}
	
	/**
	 * @param selectedColor the new color for the selected element. Can be {@code null}
	 */
	public void setSelectedColor(@Nonnull ChatColor selectedColor)
	{
		this.selectedColor = selectedColor;
	}
	
	/**
	 * @return the color for the selected element
	 */
	@Nonnull
	public ChatColor getSelectedColor()
	{
		return selectedColor;
	}
	
	/**
	 * @param value the new selected option index
	 */
	public void setSelectedIndex(int value)
	{
		this.value.setCurrent(value);
	}
	
	/**
	 * @return the currently selected option index
	 */
	public int getSelectedIndex()
	{
		return value.getCurrent();
	}
	
	/**
	 * @return the currently selected option
	 */
	public String getSelectedOption()
	{
		int selectedIndex = value.getCurrent();
		return selectedIndex >= 0 && selectedIndex < options.length ? options[selectedIndex] : null;
	}
	
	public int getWidth()
	{
		return SELECTED_PREFIX_WIDTH + width;
	}
	
	public int getHeight()
	{
		return options.length;
	}
	
	public boolean isEnabled()
	{
		return true;
	}
	
	public List<Text> render(IElementContainer context)
	{
		String baseCommand = context.getCommand(this);
		
		List<Text> result = new ArrayList<>();
		for(int i = 0; i < options.length; i++)
		{
			Text text = new Text();
			BaseComponent[] components = TextComponent.fromLegacyText(options[i]);
			
			if(i == value.getCurrent())
			{
				text.append("> ");
				if(selectedColor != null)
					for(BaseComponent component : components)
						component.setColor(selectedColor);
			}else
			{
				text.expandToWidth(SELECTED_PREFIX_WIDTH);
				ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, baseCommand + i);
				for(BaseComponent component : components)
					component.setClickEvent(click);
			}
			text.append(components);
			result.add(text);
		}
		return result;
	}
	
	public void edit(@Nonnull IElementContainer container, @Nonnull String[] args)
	{
		value.setCurrent(Integer.parseInt(args[0]));
	}
	
	@Nonnull
	public List<State<?>> getStates()
	{
		return Collections.singletonList(value);
	}
}
