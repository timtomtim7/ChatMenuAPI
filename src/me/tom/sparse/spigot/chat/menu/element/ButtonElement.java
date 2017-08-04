package me.tom.sparse.spigot.chat.menu.element;

import me.tom.sparse.spigot.chat.menu.ChatMenu;
import me.tom.sparse.spigot.chat.menu.ChatMenuAPI;
import me.tom.sparse.spigot.chat.util.Text;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ButtonElement extends Element
{
	protected String                    text;
	protected Function<Player, Boolean> callback;
	
	public ButtonElement(int x, int y, String text)
	{
		this(x, y, text, (Function<Player, Boolean>) null);
	}
	
	public ButtonElement(int x, int y, String text, Consumer<Player> callback)
	{
		this(x, y, text, player -> {
			callback.accept(player);
			return true;
		});
	}
	
	public ButtonElement(int x, int y, String text, Function<Player, Boolean> callback)
	{
		super(x, y);
		if(text.contains("\n"))
			throw new IllegalArgumentException("Button text cannot contain newline");
		this.text = text;
		this.callback = callback;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		if(text.contains("\n"))
			throw new IllegalArgumentException("Button text cannot contain newline");
		this.text = text;
	}
	
	public int getWidth()
	{
		return ChatMenuAPI.getWidth(text);
	}
	
	public int getHeight()
	{
		return 1;
	}
	
	public boolean isEnabled()
	{
		return true;
	}
	
	public List<Text> render(ChatMenu menu, int elementIndex)
	{
		String baseCommand = menu.getCommand() + elementIndex + " ";
		
		BaseComponent[] components = TextComponent.fromLegacyText(text);
		ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, baseCommand);
		for(BaseComponent component : components)
			component.setClickEvent(click);
		
		return Collections.singletonList(new Text(components));
	}
	
	public boolean onClick(ChatMenu menu, Player player)
	{
		super.onClick(menu, player);
		return callback == null ? false : callback.apply(player);
	}
	
	public void edit(ChatMenu menu, String[] args)
	{
	}
}
