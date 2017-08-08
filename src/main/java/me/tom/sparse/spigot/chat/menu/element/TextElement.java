package me.tom.sparse.spigot.chat.menu.element;

import me.tom.sparse.spigot.chat.menu.ChatMenuAPI;
import me.tom.sparse.spigot.chat.menu.IElementContainer;
import me.tom.sparse.spigot.chat.util.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic text.
 */
public class TextElement extends Element
{
	protected static final int BORDER_WIDTH = ChatMenuAPI.getWidth("|  |");

	@Nonnull
	protected String[] lines;
	protected int      width;

	@Nonnull
	protected TextAlignment alignment = TextAlignment.LEFT;
	protected boolean border;
	
	/**
	 * Constructs a {@code TextElement} with {@link TextAlignment#LEFT} and no border
	 *
	 * @param text the starting text. May contain {@code \n}
	 * @param x    the x coordinate
	 * @param y    the y coordinate
	 */
	public TextElement(@Nonnull String text, int x, int y)
	{
		super(x, y);
		
		if(text.contains("\n"))
		{
			lines = text.split("\n");
		}else
		{
			lines = new String[]{text};
		}
		for(String line : lines)
		{
			int w = ChatMenuAPI.getWidth(line);
			if(w > width)
				width = w;
		}
	}
	
	/**
	 * Constructs a {@code TextElement} with {@link TextAlignment#LEFT} and no border
	 *
	 * @param x    the x coordinate
	 * @param y    the y coordinate
	 * @param text the lines of text. Lines may not contain {@code \n}
	 */
	public TextElement(int x, int y, @Nonnull String... text)
	{
		super(x, y);
		
		this.lines = text;
		for(String line : lines)
		{
			if(line.contains("\n"))
				throw new IllegalArgumentException("Cannot use TextElement line constructor with newline characters.");
			int w = ChatMenuAPI.getWidth(line);
			if(w > width)
				width = w;
		}
	}
	
	public void setText(String text)
	{
		setLines(text.split("\n"));
	}
	
	public void setLines(@Nonnull String... lines)
	{
		int newWidth = 0;
		for(String line : lines)
		{
			if(line.contains("\n"))
				throw new IllegalArgumentException("Cannot use TextElement line constructor with newline characters.");
			int w = ChatMenuAPI.getWidth(line);
			if(w > newWidth)
				newWidth = w;
		}
		this.lines = lines;
		this.width = newWidth;
	}
	
	/**
	 * Adds a border around the text
	 *
	 * @return this
	 */
	@Nonnull
	public TextElement border()
	{
		this.border = true;
		return this;
	}
	
	/**
	 * @param border whether there should be a border around the text
	 */
	public void setBorder(boolean border)
	{
		this.border = border;
	}
	
	/**
	 * @return true if there is a border around the text
	 */
	public boolean isBordered()
	{
		return border;
	}
	
	/**
	 * Sets the text alignment
	 *
	 * @param alignment the new text alignment
	 * @return this
	 */
	@Nonnull
	public TextElement align(@Nonnull TextAlignment alignment)
	{
		setAlignment(alignment);
		return this;
	}
	
	/**
	 * @return the current text alignment
	 */
	@Nonnull
	public TextAlignment getAlignment()
	{
		return alignment;
	}
	
	/**
	 * @param alignment the new text alignment
	 */
	public void setAlignment(@Nullable TextAlignment alignment)
	{
		this.alignment = alignment == null ? TextAlignment.LEFT : alignment;
	}
	
	/**
	 * Sets the width of this element, excluding the border
	 *
	 * @param width the new width
	 * @deprecated because the width can be set to less than the actual width of the text
	 */
	@Deprecated
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	public int getWidth()
	{
		return border ? width + BORDER_WIDTH : width;
	}
	
	public int getHeight()
	{
		return border ? lines.length + 2 : lines.length;
	}
	
	public List<Text> render(IElementContainer context)
	{
		List<Text> result = new ArrayList<>();
		if(alignment == TextAlignment.LEFT)
		{
			for(String lineString : lines)
			{
				if(border)
				{
					Text text = new Text("| ");
					text.append(lineString);
					text.expandToWidth(width + (BORDER_WIDTH / 2));
					text.append(" |");
					result.add(text);
				}else
				{
					result.add(new Text(lineString));
				}
			}
		}else if(alignment == TextAlignment.CENTERED)
		{
			for(String lineString : lines)
			{
				Text current = new Text(lineString);
				int middle = width / 2 - current.getWidth() / 2;
				current = new Text();
				current.expandToWidth(middle);
				current.append(lineString);
				if(border)
				{
					Text text = new Text("| ");
					text.append(current);
					text.expandToWidth(width + (BORDER_WIDTH / 2));
					text.append(" |");
					result.add(text);
				}else
				{
					result.add(current);
				}
			}
		}else if(alignment == TextAlignment.RIGHT)
		{
			for(String lineString : lines)
			{
				Text current = new Text(lineString);
				int middle = width - current.getWidth();
				current = new Text();
				current.expandToWidth(middle);
				current.append(lineString);
				if(border)
				{
					Text text = new Text("| ");
					text.append(current);
					text.expandToWidth(width + (BORDER_WIDTH / 2));
					text.append(" |");
					result.add(text);
				}else
				{
					result.add(current);
				}
			}
		}
		if(border)
		{
			String border = "+";
			while(ChatMenuAPI.getWidth(border) < getWidth())
				border += "-";
			if(border.length() > 1)
				border = border.substring(0, border.length() - 1) + "+";
			
			Text text = new Text(border);
			result.add(0, text);
			result.add(text);
		}
		return result;
	}
	
	public void edit(@Nonnull IElementContainer container, @Nonnull String[] args)
	{
	
	}
	
	public enum TextAlignment
	{
		LEFT, CENTERED, RIGHT
	}
}
