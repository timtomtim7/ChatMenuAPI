package me.tom.sparse.spigot.chat.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class State<V>
{
	private Consumer<State<V>> changeCallback;
	
	private Function<V, V> valueFilter;

	@Nullable
	private V current;
	@Nullable
	private V previous;
	
	/**
	 * Constructs a new {@code State} with the provided value and the provided value filter.
	 * <br>
	 * The value filter will replace the value every time {@link State#setCurrent} is called.
	 *
	 * @param current     the starting value
	 * @param valueFilter the filter for every value
	 */
	public State(@Nonnull V current, Function<V, V> valueFilter)
	{
		this.valueFilter = valueFilter;
		this.current = valueFilter.apply(current);
	}
	
	/**
	 * Constructs a new {@code State} with the provided value and no input filter.
	 *
	 * @param current the starting value
	 */
	public State(@Nonnull V current)
	{
		this(current, v -> v);
	}
	
	/**
	 * Sets the current value if the provided value is not {@link Object#equals} to the old one, then calls the {@code changeCallback}.
	 *
	 * @param newValue the new value
	 */
	public void setCurrent(@Nonnull V newValue)
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
	@Nullable
	public V getCurrent()
	{
		return current;
	}
	
	/**
	 * @return the getPrevious value. Might be {@code null}.
	 */
	@Nullable
	public V getPrevious()
	{
		return previous;
	}
	
	/**
	 * Sets the change callback. Every time this {@code State} changes, the provided callback will be called.
	 * <br>
	 * Replaces any previously setCurrent change callbacks.
	 *
	 * @param changeCallback the new change callback.
	 */
	public void setOnChange(@Nonnull Consumer<State<V>> changeCallback)
	{
		this.changeCallback = changeCallback;
	}
	
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof State)) return false;
		
		State<?> state = (State<?>) o;
		
		return current != null ? current.equals(state.current) : state.current == null;
	}
	
	public int hashCode()
	{
		return current != null ? current.hashCode() : 0;
	}
	
	public String toString()
	{
		return "State{" +
				"getCurrent=" + current +
				", getPrevious=" + previous +
				'}';
	}
}
