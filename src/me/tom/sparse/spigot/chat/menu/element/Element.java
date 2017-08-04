package me.tom.sparse.spigot.chat.menu.element;

import me.tom.sparse.spigot.chat.menu.ChatMenu;
import me.tom.sparse.spigot.chat.util.State;
import me.tom.sparse.spigot.chat.util.Text;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Element
{
	protected int x, y;
	
	protected Sound clickSound  = Sound.UI_BUTTON_CLICK;
	protected float clickVolume = 0.5f;
	protected float clickPitch  = 1;
	
	/**
	 * Constructs an element at the given x and y coordinates.
	 *
	 * @param x the x coordinate to put this element at
	 * @param y the y coordinate to put this element at
	 */
	public Element(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return the pitch of the sound played when a player clicks this element
	 */
	public float getClickPitch()
	{
		return clickPitch;
	}
	
	/**
	 * @return the volume of the sound played when a player clicks this element
	 */
	public float getClickVolume()
	{
		return clickVolume;
	}
	
	/**
	 * @return the sound played when a player clicks this element
	 */
	public Sound getClickSound()
	{
		return clickSound;
	}
	
	/**
	 * Sets the sound and the volume and pitch of the sound played when a player clicks this element.
	 *
	 * @param clickSound the new sound
	 * @param volume     the volume of the sound
	 * @param pitch      the pitch of the sound
	 */
	public void setClickSound(Sound clickSound, float volume, float pitch)
	{
		this.clickSound = clickSound;
		this.clickVolume = volume;
		this.clickPitch = pitch;
	}
	
	/**
	 * @return the x coordinate of the left-most part of this element
	 */
	public final int getLeft()
	{
		return getX();
	}
	
	/**
	 * @return the x coordinate of the right-most part of this element
	 */
	public final int getRight()
	{
		return getX() + getWidth();
	}
	
	/**
	 * @return the y coordinate of the top-most part of this element
	 */
	public final int getTop()
	{
		return getY();
	}
	
	/**
	 * @return the y coordinate of the bottom-most part of this element
	 */
	public final int getBottom()
	{
		return getY() + getHeight();
	}
	
	/**
	 * @return the x coordinate of this element
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 * Sets the x coordinate of this element
	 *
	 * @param x the new coordinate
	 */
	public void setX(int x)
	{
		this.x = x;
	}
	
	/**
	 * @return the width of this element
	 */
	public abstract int getWidth();
	
	/**
	 * @return the y coordinate of this element
	 */
	public int getY()
	{
		return y;
	}
	
	/**
	 * Sets the y coordinate of this element
	 *
	 * @param y the new coordinate
	 */
	public void setY(int y)
	{
		this.y = y;
	}
	
	/**
	 * @return the height of this element
	 */
	public abstract int getHeight();
	
	/**
	 * Detects if the provided element overlaps this one.
	 * <br>
	 * This method will always return false if the provided element is this element.
	 *
	 * @param other the element to detect collision with
	 * @return true of the elements overlap
	 */
	public final boolean overlaps(Element other)
	{
		if(other == this)
			return false;
		
		int tw = this.getWidth();
		int th = this.getHeight();
		int rw = other.getWidth();
		int rh = other.getHeight();
		
		if(rw <= 0 || rh <= 0 || tw <= 0 || th <= 0)
		{
			return false;
		}
		int tx = this.getX();
		int ty = this.getY();
		int rx = other.getX();
		int ry = other.getY();
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		
		//      overflow || intersect
		return ((rw < rx || rw > tx) &&
				(rh < ry || rh > ty) &&
				(tw < tx || tw > rx) &&
				(th < ty || th > ry));
	}
	
	/**
	 * @param menu         the menu this is being rendered for
	 * @param elementIndex the index of this element in the menu
	 * @return the rendered text
	 */
	public abstract List<Text> render(ChatMenu menu, int elementIndex);
	
	/**
	 * Called when a player clicks this element.
	 * <br>
	 * More specifically, when a player runs the command to edit this element.
	 *
	 * @param menu   the menu this element was clicked on
	 * @param player the player that clicked this element
	 * @return true if the menu should rebuild and resend
	 */
	public boolean onClick(ChatMenu menu, Player player)
	{
		if(clickSound != null)
			player.playSound(player.getEyeLocation(), clickSound, clickVolume, clickPitch);
		return true;
	}
	
	/**
	 * Called to edit this element
	 *
	 * @param menu the menu this element is being edited on
	 * @param args the data to be parsed
	 */
	public abstract void edit(ChatMenu menu, String[] args);
	
	/**
	 * @param menu         the menu to edit
	 * @param elementIndex the index of this element in the provided menu
	 * @return the command to be run to edit this element
	 */
	public final String getCommand(ChatMenu menu, int elementIndex)
	{
		return menu.getCommand() + elementIndex + " ";
	}
	
	/**
	 * @return an unmodifiable {@link java.util.Collection} of all the states in this element.
	 */
	public Collection<State<?>> getStates()
	{
		return Collections.emptyList();
	}
}
