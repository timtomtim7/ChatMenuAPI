package me.tom.sparse.spigot.chat.menu;

import me.tom.sparse.spigot.chat.menu.element.Element;
import org.bukkit.entity.Player;

import java.util.List;

public interface IElementContainer
{
	<T extends Element> T add(T element);
	
	boolean remove(Element element);
	
	List<Element> getElements();
	
	String getCommand(Element element);
	
	void openFor(Player player);
}
