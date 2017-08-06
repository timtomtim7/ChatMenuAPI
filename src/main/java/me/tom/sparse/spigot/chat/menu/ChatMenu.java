package me.tom.sparse.spigot.chat.menu;

import io.netty.util.internal.ConcurrentSet;
import me.tom.sparse.spigot.chat.menu.element.ButtonElement;
import me.tom.sparse.spigot.chat.menu.element.Element;
import me.tom.sparse.spigot.chat.protocol.PlayerChatIntercept;
import me.tom.sparse.spigot.chat.util.Text;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.util.*;

public class ChatMenu
{
	protected final String  id;
	protected       boolean registered;
	
	protected List<Element> elements;
	
	protected Set<Player> viewers   = new ConcurrentSet<>();
	protected boolean     pauseChat = false;
	
	protected boolean autoUnregister = true;
	
	/**
	 * Constructs a chat menu with the elements provided.
	 *
	 * @param elements the elements to start the menu with.
	 */
	public ChatMenu(Element... elements)
	{
		this(Arrays.asList(elements));
	}
	
	/**
	 * Constructs a chat menu with the elements provided.
	 *
	 * @param elements the elements to start the menu with.
	 */
	public ChatMenu(Collection<Element> elements)
	{
		this.elements = new ArrayList<>();
		this.elements.addAll(elements);
		this.id = ChatMenuAPI.registerMenu(this);
		registered = true;
	}
	
	/**
	 * Unregisters this menu, all the elements, and closes the menu for all viewers.
	 */
	public void destroy()
	{
		unregister();
		elements.clear();
		viewers.forEach(this::close);
	}
	
	/**
	 * Unregister this menu.
	 * <br>
	 * An unregistered menu cannot be interacted with by a player.
	 * If you attempt to build this menu with elements that don't support unregistered menus, you will get an {@link IllegalStateException}.
	 * <br>
	 * Be sure to unregister all menus once you're done with them!
	 *
	 * @throws IllegalStateException if this menu is not registered.
	 */
	public void unregister()
	{
		if(!registered)
			throw new IllegalStateException("Menu not registered");
		ChatMenuAPI.unregisterMenu(this);
		registered = false;
	}
	
	/**
	 * Adds the provided element to this menu.
	 *
	 * @param element the element to add to this menu
	 * @throws IllegalArgumentException if the element is null
	 */
	public void addElement(Element element)
	{
		if(element == null)
			throw new IllegalArgumentException("Cannot add null element");
		elements.add(element);
		elements.sort(Comparator.comparingInt(Element::getX));
	}
	
	/**
	 * Adds the provided element to this menu.
	 *
	 * @param t   the element to add to this menu
	 * @param <T> the type of element
	 * @return the element added
	 */
	public <T extends Element> T add(T t)
	{
		addElement(t);
		return t;
	}
	
	/**
	 * @return an unmodifiable list of all the elements in this menu.
	 */
	public List<Element> getElements()
	{
		return Collections.unmodifiableList(elements);
	}
	
	/**
	 * Called when a player edits something in the menu.
	 *
	 * @param player       the player that edited something
	 * @param elementIndex the index of the element that was edited
	 * @param args         the data to be parsed by the element
	 */
	public void edit(Player player, int elementIndex, String[] args)
	{
		if(elementIndex < 0 || elementIndex >= elements.size())
			return;
		
		Element element = elements.get(elementIndex);
		element.edit(this, args);
		if(element.onClick(this, player))
			openFor(player);
	}
	
	/**
	 * Builds and sends this menu to the provided player.
	 *
	 * @param player the player to send the menu to.
	 */
	public void openFor(Player player)
	{
		PlayerChatIntercept chat = ChatMenuAPI.getChatIntercept(player);
		if(viewers.add(player) && pauseChat)
			chat.pause();
		for(BaseComponent[] line : build())
			chat.sendMessage(line);
		ChatMenuAPI.setCurrentMenu(player, this);
	}
	
	public List<BaseComponent[]> build()
	{
		if(anyOverlap())
		{
//			System.err.println("WARNING! Potential overlap detected.");
			throw new IllegalStateException("Overlapping elements!");
		}
		
		List<Text> lines = new ArrayList<>(20);
		for(int i = 0; i < 20; i++)
			lines.add(new Text());
		
		for(int elementIndex = 0; elementIndex < elements.size(); elementIndex++)
		{
			Element element = elements.get(elementIndex);
			
			List<Text> elementTexts = element.render(this, elementIndex);
			for(int j = 0; j < elementTexts.size(); j++)
			{
				int lineY = element.getY() + j;
				
				if(lineY < 0 || lineY >= 20)
					continue;
				
				Text text = lines.get(lineY);
				text.expandToWidth(element.getX());
				
				Text toAdd = elementTexts.get(j);
				toAdd.expandToWidth(element.getWidth());
				text.append(toAdd);
			}
		}
		
		//TODO: Compress to as little packets as possible.
		
		List<BaseComponent[]> result = new ArrayList<>();
		for(Text text : lines)
		{
			List<BaseComponent> components = text.getComponents();
			result.add(components.toArray(new BaseComponent[components.size()]));
		}
		return result;
	}
	
	/**
	 * @return true if any elements overlap
	 */
	public boolean anyOverlap()
	{
		return elements.stream().anyMatch(a -> elements.stream().anyMatch(b -> a != b && a.overlaps(b)));
	}
	
	/**
	 * Sets the currently opened menu of the provided player to null.
	 *
	 * @param player the player that closed the menu
	 */
	public void close(Player player)
	{
		if(viewers.remove(player))
		{
			ChatMenuAPI.setCurrentMenu(player, null);
			ChatMenuAPI.getChatIntercept(player).resume();
		}
		if(viewers.size() == 0 && autoUnregister)
			unregister();
	}
	
	void onClosed(Player player)
	{
		if(viewers.remove(player))
			ChatMenuAPI.getChatIntercept(player).resume();
	}
	
	/**
	 * @return the command used to interact with this menu
	 */
	public String getCommand()
	{
		if(!isRegistered())
			throw new IllegalStateException("Unregistered menus can't be interacted with.");
		return "/cmapi " + id + " ";
	}
	
	/**
	 * @return true if this menu is registered
	 */
	public boolean isRegistered()
	{
		return registered;
	}
	
	/**
	 *
	 * @return true if this menu will pause chat when it is opened
	 */
	public boolean doesPauseChat()
	{
		return pauseChat;
	}
	
	/**
	 * Makes this menu pause chat when it is opened
	 * @return this
	 */
	public ChatMenu pauseChat()
	{
		setPauseChat(true);
		return this;
	}
	
	/**
	 * Makes this menu pause chat when it is opened and adds a close button.
	 *
	 * @param x    the x coordinate of the close button
	 * @param y    the y coordinate of the close button
	 * @param text the text of the close button
	 * @return this
	 */
	public ChatMenu pauseChat(int x, int y, String text)
	{
		setPauseChat(true);
		addElement(ButtonElement.createCloseButton(x, y, text, this));
		return this;
	}
	
	/**
	 *
	 * @param pauseChat true if this menu should pause chat when it is opened
	 */
	public void setPauseChat(boolean pauseChat)
	{
		this.pauseChat = pauseChat;
	}
	
	public int hashCode()
	{
		return id.hashCode();
	}
	
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof ChatMenu)) return false;
		
		ChatMenu chatMenu = (ChatMenu) o;
		
		return id.equals(chatMenu.id);
	}
}
