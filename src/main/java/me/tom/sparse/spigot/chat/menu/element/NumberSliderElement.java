package me.tom.sparse.spigot.chat.menu.element;

import me.tom.sparse.spigot.chat.menu.ChatMenuAPI;
import me.tom.sparse.spigot.chat.menu.IElementContainer;
import me.tom.sparse.spigot.chat.util.NumberFormat;
import me.tom.sparse.spigot.chat.util.State;
import me.tom.sparse.spigot.chat.util.Text;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberSliderElement extends Element
{
	public static final int MIN_PRECISION = 0;
	public static final int MAX_PRECISION = 7;

	@Nonnull
	public final State<Integer> value;
	protected    int            length;

	@Nonnull
	protected ChatColor fullColor  = ChatColor.GREEN;
	@Nonnull
	protected ChatColor emptyColor = ChatColor.RED;

	@Nonnull
	protected NumberFormat numberFormat = NumberFormat.PERCENTAGE;
	
	protected int precision = 6;
	
	/**
	 * Constructs a {@code NumberSliderElement} with {@link NumberFormat#PERCENTAGE} formatting
	 *
	 * @param x      the x coordinate
	 * @param y      the y coordinate
	 * @param length the number of bars to display
	 * @param value  the number of bars that are full
	 */
	public NumberSliderElement(int x, int y, int length, int value)
	{
		super(x, y);
		this.length = length;
		this.value = new State<>(value, this::filter);
	}
	
	/**
	 * Constructs a {@code NumberSliderElement}
	 *
	 * @param x      the x coordinate
	 * @param y      the y coordinate
	 * @param length the number of bars to display
	 * @param value  the number of bars that are full
	 * @param format the format for the number
	 */
	public NumberSliderElement(int x, int y, int length, int value, @Nullable NumberFormat format)
	{
		super(x, y);
		this.length = length;
		this.value = new State<>(value, this::filter);
		this.numberFormat = format == null ? NumberFormat.NONE : format;
	}
	
	private int filter(int v)
	{
		return Math.max(Math.min(v, length), 0);
	}
	
	/**
	 * Sets the colors that should be used when displaying this.
	 *
	 * @param fullColor  the color for all of the full bars
	 * @param emptyColor the color for all of the empty bars
	 * @return this
	 */
	@Nonnull
	public NumberSliderElement colors(@Nonnull ChatColor fullColor, @Nonnull ChatColor emptyColor)
	{
		setFullColor(fullColor);
		setEmptyColor(emptyColor);
		return this;
	}
	
	/**
	 * Sets the number format to {@link NumberFormat#NONE}
	 *
	 * @return this
	 */
	public NumberSliderElement hideNumber()
	{
		return numberFormat(NumberFormat.NONE);
	}
	
	/**
	 * Sets the number format
	 *
	 * @param format the new number format
	 * @return this
	 */
	public NumberSliderElement numberFormat(@Nullable NumberFormat format)
	{
		setNumberFormat(format);
		return this;
	}
	
	/**
	 * @return the current number format
	 */
	@Nonnull
	public NumberFormat getNumberFormat()
	{
		return numberFormat;
	}
	
	/**
	 * @param format the new number format
	 */
	public void setNumberFormat(@Nullable NumberFormat format)
	{
		this.numberFormat = format == null ? NumberFormat.NONE : format;
	}
	
	/**
	 * @return the precision. Must be within (inclusive) {@link NumberSliderElement#MIN_PRECISION} and {@link NumberSliderElement#MAX_PRECISION}
	 */
	public int getPrecision()
	{
		return precision;
	}
	
	/**
	 * Sets the precision of this. Precision determines how wide the bars will be, higher precision means smaller bars.
	 *
	 * @param precision the new precision. Must be within (inclusive) {@link NumberSliderElement#MIN_PRECISION} and {@link NumberSliderElement#MAX_PRECISION}
	 */
	public void setPrecision(int precision)
	{
		if(precision < 0 || precision > 7)
			throw new IllegalArgumentException("Precision must be between (inclusive) 0-7");
		this.precision = precision;
	}
	
	/**
	 * Sets the precision of this. Precision determines how wide the bars will be, higher precision means smaller bars.
	 *
	 * @param precision the new precision. Must be within (inclusive) {@link NumberSliderElement#MIN_PRECISION} and {@link NumberSliderElement#MAX_PRECISION}
	 * @return this
	 */
	public NumberSliderElement precision(int precision)
	{
		setPrecision(precision);
		return this;
	}

//	 * @param fullColor the color for all of the full bars
//	 * @param emptyColor the color for all of the empty bars
	
	/**
	 * @return the color for all of the empty bars
	 */
	@Nonnull
	public ChatColor getEmptyColor()
	{
		return emptyColor;
	}
	
	/**
	 * @param emptyColor the new color for all of the empty bars
	 */
	public void setEmptyColor(@Nullable ChatColor emptyColor)
	{
		this.emptyColor = emptyColor == null ? ChatColor.RED : emptyColor;
	}
	
	/**
	 * @return the color for all of the full bars
	 */
	@Nonnull
	public ChatColor getFullColor()
	{
		return fullColor;
	}
	
	/**
	 * @param fullColor the new color for all of the full bars
	 */
	public void setFullColor(@Nullable ChatColor fullColor)
	{
		this.fullColor = fullColor == null ? ChatColor.GREEN : fullColor;
	}
	
	/**
	 * @return the number of bars that get displayed
	 */
	public int getLength()
	{
		return length;
	}
	
	/**
	 * @param length thew new number of bars to display
	 */
	public void setLength(int length)
	{
		this.length = length < 0 ? 10 : length;
	}
	
	/**
	 * Sets the length of this (based on the current precision) to attempt to make the width match as closely as possible to the target width.
	 *
	 * @param width the width to attempt to match
	 */
	public void setWidth(int width)
	{
		int charWidth = ChatMenuAPI.getCharacterWidth(getCharacter());
		length = width / charWidth;
	}
	
	/**
	 * Sets the length of this (based on the current precision) to attempt to make the width match as closely as possible to the target width.
	 *
	 * @param width the width to attempt to match
	 * @return this
	 */
	@Nonnull
	public NumberSliderElement width(int width)
	{
		setWidth(width);
		return this;
	}
	
	/**
	 * @return the current value
	 */
	public int getValue()
	{
		return value.getCurrent();
	}
	
	/**
	 * @param value the new value. Must not be less than 0 or more than {@code length}
	 */
	public void setValue(int value)
	{
		this.value.setCurrent(value);
	}
	
	/**
	 * @return the bar character used based on the current precision
	 */
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
		return numberFormat == null ? "" : " " + numberFormat.format(value.getCurrent(), length);
	}
	
	public int getHeight()
	{
		return 1;
	}

	public List<Text> render(IElementContainer context)
	{
		String baseCommand = context.getCommand(this);
		
		List<BaseComponent> components = new ArrayList<>();
		for(int i = 0; i < length; i++)
		{
//			double v = (double) (i + 1) / (double) length;
			TextComponent c = new TextComponent(String.valueOf((char) ('\u2588' + precision)));
			c.setColor(i <= value.getCurrent() ? isEnabled() ? fullColor : ChatColor.GRAY : isEnabled() ? emptyColor : ChatColor.DARK_GRAY);
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
	
	public boolean onClick(@Nonnull IElementContainer container, @Nonnull Player player)
	{
		return isEnabled() && super.onClick(container, player);
	}
	
	public void edit(@Nonnull IElementContainer container, @Nonnull String[] args)
	{
		if(!isEnabled())
			return;
		value.setCurrent(Integer.parseInt(args[0]));
	}
	
	@Nonnull
	public List<State<?>> getStates()
	{
		return Collections.singletonList(value);
	}
}
