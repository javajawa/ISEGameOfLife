package ise.mace.models;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Allows classes to easily store historical values in a type safe, controlled way
 * @param <T> The type of things being recorded historically
 */
public class UnmodifiableHistory<T extends Serializable> extends History<T>
{
	private static final long serialVersionUID = 1L;

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
