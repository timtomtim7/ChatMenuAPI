package me.tom.sparse.spigot.chat.menu;

import io.netty.util.internal.ConcurrentSet;
import me.tom.sparse.spigot.chat.menu.element.ButtonElement;
import me.tom.sparse.spigot.chat.menu.element.Element;
import me.tom.sparse.spigot.chat.protocol.PlayerChatIntercept;
import me.tom.sparse.spigot.chat.util.Text;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ChatMenu implements IElementContainer
{
	@Nonnull
	protected final String  id;
	protected       boolean registered;

	@Nonnull
	protected List<Element> elements;

	@Nonnull
	protected Set<Player> viewers   = new ConcurrentSet<>();
	protected boolean     pauseChat = false;
	
	protected boolean autoUnregister = true;
	
	/**
	 * Constructs a chat menu with the elements provided.
	 *
	 * @param elements the elements to start the menu with.
	 */
	public ChatMenu(@Nonnull Element... elements)
	{
		this(Arrays.asList(elements));
	}
	
	/**
	 * Constructs a chat menu with the elements provided.
	 *
	 * @param elements the elements to start the menu with.
	 */
	public ChatMenu(@Nonnull Collection<Element> elements)
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
	 * @param autoUnregister true if this menu should automatically be unregistered after all players close it.
	 */
	public void setAutoUnregister(boolean autoUnregister)
	{
		this.autoUnregister = autoUnregister;
	}
	
	/**
	 * Adds the provided element to this menu.
	 *
	 * @param element the element to add to this menu
	 * @throws IllegalArgumentException if the element is null
	 */
	@Deprecated
	public void addElement(@Nonnull Element element)
	{
		add(element);
	}
	
	/**
	 * Adds the provided element to this menu.
	 *
	 * @param t   the element to add to this menu
	 * @param <T> the type of element
	 * @return the element added
	 */
	public <T extends Element> T add(@Nonnull T t)
	{
		Objects.requireNonNull(t);
		elements.add(t);
		elements.sort(Comparator.comparingInt(Element::getX));
		return t;
	}
	
	/**
	 * Removes the specified element from this menu.
	 *
	 * @param element the element to remove
	 * @return true if the element was removed
	 */
	public boolean remove(@Nonnull Element element)
	{
		return elements.remove(element);
	}
	
	/**
	 * @return an unmodifiable list of all the elements in this menu.
	 */
	@Nonnull
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
	public void edit(@Nonnull Player player, int elementIndex, @Nonnull String[] args)
	{
		if(elementIndex < 0 || elementIndex >= elements.size())
			return;
		
		Element element = elements.get(elementIndex);
		element.edit(this, args);
		if(element.onClick(this, player))
			refresh();
	}
	
	/**
	 * Builds and sends this menu to the provided player.
	 *
	 * @param player the player to send the menu to.
	 */
	public void openFor(@Nonnull Player player)
	{
		PlayerChatIntercept chat = ChatMenuAPI.getChatIntercept(player);
		if(viewers.add(player) && pauseChat)
			chat.pause();
		for(BaseComponent[] line : build())
			chat.sendMessage(line);
		ChatMenuAPI.setCurrentMenu(player, this);
	}
	
	/**
	 * Sends this menu again to all of the players currently viewing it
	 */
	public void refresh()
	{
		viewers.removeIf(it -> !it.isOnline());
		for(Player viewer : viewers)
			openFor(viewer);
	}

	@Nonnull
	public List<BaseComponent[]> build()
	{
		Element overlapping = findOverlap();
		if(overlapping != null)
		{
//			System.err.println("WARNING! Potential overlap detected.");
			throw new IllegalStateException("Overlapping element(s)! "+overlapping);
		}
		
		List<Text> lines = new ArrayList<>(20);
		for(int i = 0; i < 20; i++)
			lines.add(new Text());
		
		for(Element element : elements)
		{
			if(!element.isVisible())
				continue;
			
			List<Text> elementTexts = element.render(this);
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
			if(text.toLegacyText().contains("\n"))
				throw new IllegalStateException("Menu contains line with newline character");
			else if(text.getWidth() > 320)
				throw new IllegalStateException("Menu contains line exceeds chat width");
			
			
			List<BaseComponent> components = text.getComponents();
			result.add(components.toArray(new BaseComponent[components.size()]));
		}
		return result;
	}

	/**
	 * @return the first element found that overlaps
	 */
	@Nullable
	public Element findOverlap()
	{
		return elements.stream().filter(Element::isVisible).filter(a -> elements.stream().filter(Element::isVisible).anyMatch(b -> a != b && a.overlaps(b))).findFirst().orElse(null);
	}
	
	/**
	 * Sets the currently opened menu of the provided player to null.
	 *
	 * @param player the player that closed the menu
	 */
	public void close(@Nonnull Player player)
	{
		if(viewers.remove(player))
		{
			ChatMenuAPI.setCurrentMenu(player, null);
			ChatMenuAPI.getChatIntercept(player).resume();
		}
		if(viewers.size() == 0 && autoUnregister)
			unregister();
	}
	
	void onClosed(@Nonnull Player player)
	{
		if(viewers.remove(player))
			ChatMenuAPI.getChatIntercept(player).resume();
	}
	
	/**
	 * @return the command used to interact with this menu
	 */
	@Nonnull
	public String getCommand()
	{
		if(!isRegistered())
			throw new IllegalStateException("Unregistered menus can't be interacted with.");
		return "/cmapi " + id + " ";
	}
	
	/**
	 * @param element the element to interact with
	 * @return the command used to interact with the provided element
	 */
	@Nonnull
	public String getCommand(@Nonnull Element element)
	{
		return getCommand() + elements.indexOf(element)+" ";
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
	@Nonnull
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
	@Nonnull
	public ChatMenu pauseChat(int x, int y, @Nonnull String text)
	{
		setPauseChat(true);
		add(ButtonElement.createCloseButton(x, y, text, this));
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
