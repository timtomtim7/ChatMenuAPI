package me.tom.sparse.spigot.chat.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class State<V>
{
	private Consumer<State<V>> changeCallback;
	
	@NotNull
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
	public State(@Nullable V current, @Nullable Function<V, V> valueFilter)
	{
		this.valueFilter = valueFilter == null ? v -> v : valueFilter;
		this.current = this.valueFilter.apply(current);
	}
	
	/**
	 * Constructs a new {@code State} with the provided value and no input filter.
	 *
	 * @param current the starting value
	 */
	public State(@Nullable V current)
	{
		this(current, v -> v);
	}
	
	/**
	 * Sets the current value if the provided value is not {@link Object#equals} to the old one, then calls the {@code changeCallback}.
	 *
	 * @param newValue the new value
	 */
	public void setCurrent(@Nullable V newValue)
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
	 * @return the current value as an {@link java.util.Optional}
	 */
	public Optional<V> getOptionalCurrent()
	{
		return Optional.ofNullable(current);
	}
	
	/**
	 * @return the previous value as an {@link java.util.Optional}
	 */
	public Optional<V> getOptionalPrevious()
	{
		return Optional.ofNullable(previous);
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
	public void setChangeCallback(@NotNull Consumer<State<V>> changeCallback)
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
				"current=" + current +
				", previous=" + previous +
				'}';
	}
}
