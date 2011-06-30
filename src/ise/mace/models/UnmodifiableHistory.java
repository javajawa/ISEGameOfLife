package ise.mace.models;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Allows classes to easily store historical values in a type safe, controlled way
 * @param <T> The type of things being recorded historically
 */
public final class UnmodifiableHistory<T extends Serializable> extends History<T>
{
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the unmodifiable history, backed on a reference to the internal
	 * linked list of a {@link History}.
	 * @param d The backing list
	 * @param size The max size, to allow the call to super.
	 */
	UnmodifiableHistory(LinkedList<T> d, int size)
	{
		super(d, size);
	}

	@Override
	public T setValue(T value)
	{
		throw new UnsupportedOperationException("The History is not modifiable");
	}

	@Override
	public void newEntry(boolean cloneOld)
	{
		throw new UnsupportedOperationException("The History is not modifiable");
	}

	@Override
	public void newEntry()
	{
		throw new UnsupportedOperationException("The History is not modifiable");
	}

	@Override
	public void newEntry(T value)
	{
		throw new UnsupportedOperationException("The History is not modifiable");
	}

	@Override
	public void setMaxSize(int maxSize)
	{
		throw new UnsupportedOperationException("The History is not modifiable");
	}

	@Override
	public UnmodifiableHistory<T> getUnmodifableHistory()
	{
		return this;
	}
}
