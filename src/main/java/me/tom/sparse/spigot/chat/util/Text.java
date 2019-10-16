package me.tom.sparse.spigot.chat.util;

import me.tom.sparse.spigot.chat.menu.ChatMenuAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.jetbrains.annotations.NotNull;
import java.util.*;

/**
 * BaseComponent[] wrapper with cached width
 */
public class Text
{
	@NotNull
	protected List<BaseComponent> components = new ArrayList<>();
	protected int                 width      = 0;
	
	/**
	 * Constructs an empty {@code Text} object with 0 width and no components.
	 */
	public Text()
	{
	
	}
	
	/**
	 * ]
	 * Constructs a {@code Text} object with the provided text.
	 *
	 * @param text the starting text
	 */
	public Text(@NotNull String text)
	{
		if(text.contains("\n"))
			throw new IllegalArgumentException("Text cannot have newline characters");
		Collections.addAll(components, TextComponent.fromLegacyText(text));
		
		calculateWidth();
	}
	
	/**
	 * Constructs a {@code Text} object with the provided components
	 *
	 * @param components the starting components
	 */
	public Text(@NotNull BaseComponent... components)
	{
		this(Arrays.asList(components));
	}
	
	/**
	 * Constructs a {@code Text} object with the provided components
	 *
	 * @param components the starting components
	 */
	public Text(@NotNull Collection<BaseComponent> components)
	{
		this.components.addAll(components);
		if(toLegacyText().contains("\n"))
			throw new IllegalArgumentException("Text cannot have newline characters");
		calculateWidth();
	}
	
	/**
	 * @return the cached width
	 */
	public int getWidth()
	{
		return width;
	}
	
	/**
	 * Appends all of the components of the provided {@code Text} object to this.
	 *
	 * @param other the {@code Text} to append
	 */
	public void append(@NotNull Text other)
	{
		components.addAll(other.components);
		width += other.width;
	}
	
	/**
	 * Converts the provided text from legacy text to components and appends it
	 *
	 * @param text the text to append
	 */
	public void append(@NotNull String text)
	{
		if(text.contains("\n"))
			throw new IllegalArgumentException("Text cannot have newline characters");
		Collections.addAll(components, TextComponent.fromLegacyText(text));
		calculateWidth();
	}
	
	/**
	 * Appends all of the provided components
	 *
	 * @param components the components to append
	 */
	public void append(@NotNull BaseComponent... components)
	{
		Collections.addAll(this.components, components);
		calculateWidth();
	}
	
	/**
	 * Appends spaces to the end such that the width is as close as possible to the target width
	 * <br>
	 * The resulting width may be more or less than the target width by 1-2 pixels
	 *
	 * @param targetWidth the width to expand to
	 */
	public void expandToWidth(int targetWidth)
	{
		calculateWidth();
		
		if(width >= targetWidth)
			return;
		
		components.add(new TextComponent(TextUtil.generateSpaces((int) Math.round((targetWidth - width) / 4.0))));
	}
	
	/**
	 * Appends spaces to the end such that the width is as close as possible to the target width without going over.
	 * <br>
	 * The resulting width may be less than the target width 1-3 pixels
	 *
	 * @param targetWidth the width to expand to
	 */
	public void expandToWidthNoExceed(int targetWidth)
	{
		calculateWidth();
		
		if(width >= targetWidth)
			return;
		
		components.add(new TextComponent(TextUtil.generateSpaces((int) Math.floor((targetWidth - width) / 4.0))));
	}
	
	/**
	 * Recalculates the width of all the components
	 */
	public void calculateWidth()
	{
		width = ChatMenuAPI.getWidth(toLegacyText());
	}
	
	/**
	 * @return the components of this {@code Text} object converted to legacy.
	 */
	@NotNull
	public String toLegacyText()
	{
		return TextComponent.toLegacyText(components.toArray(new BaseComponent[components.size()]));
	}
	
	/**
	 * <b>If you make any changes to this list, call {@link Text#calculateWidth()}</b>
	 *
	 * @return the backing list of the components in this {@code Text} object.
	 */
	@NotNull
	public List<BaseComponent> getComponents()
	{
		return components;
	}
}
