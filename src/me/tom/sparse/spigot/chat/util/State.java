package me.tom.sparse.spigot.chat.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class State<V>
{
	private Consumer<State<V>> changeCallback;
	
	private Function<V, V> valueFilter;
	
	private V current;
	private V previous;
	
	/**
	 * Constructs a new {@code State} with the provided value and the provided value filter.
	 * <br>
	 * The value filter will replace the value every time {@link State#set} is called.
	 *
	 * @param current     the starting value
	 * @param valueFilter the filter for every value
	 */
	public State(V current, Function<V, V> valueFilter)
	{
		this.valueFilter = valueFilter;
		this.current = valueFilter.apply(current);
	}
	
	/**
	 * Constructs a new {@code State} with the provided value and no input filter.
	 *
	 * @param current the starting value
	 */
	public State(V current)
	{
		this(current, v -> v);
	}
	
	/**
	 * Sets the current value if the provided value is not {@link Object#equals} to the old one, then calls the {@code changeCallback}.
	 *
	 * @param newValue the new value
	 */
	public void set(V newValue)
	{
		newValue = valueFilter.apply(newValue);
		
		if(Objects.equals(newValue, this.current))
			return;
		
		this.previous = this.current;
		this.current = newValue;
		
		if(changeCallback != null)
			changeCallback.accept(this);
	}
	
	/**
	 * @return the current value. Might be {@code null}.
	 */
	public V current()
	{
		return current;
	}
	
	/**
	 * @return the previous value. Might be {@code null}.
	 */
	public V previous()
	{
		return previous;
	}
	
	/**
	 * Sets the change callback. Every time this {@code State} changes, the provided callback will be called.
	 * <br>
	 * Replaces any previously set change callbacks.
	 *
	 * @param changeCallback the new change callback.
	 */
	public void onChange(Consumer<State<V>> changeCallback)
	{
		this.changeCallback = changeCallback;
	}
}
