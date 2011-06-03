package ise.gameoflife;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Allows classes to easily store historical values in a type safe, controlled way
 * TODO: Document
 * @param <T> The type of things being recorded historically
 * @author Benedict
 */
public class History<T> implements Serializable
{

	private static final long serialVersionUID = 1L;
	private LinkedList<T> data = new LinkedList<T>();
	private int maxSize = 10;

	public T getValue()
	{
		return data.element();
	}

	public T getValue(int turnsAgo)
	{
		return data.get(turnsAgo);
	}

	public T setValue(T value)
	{
		return data.set(0, value);
	}

	public void newEntry()
	{
		data.push(null);
		truncate();
	}

	public int getMaxSize()
	{
		return maxSize;
	}

	public void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
		truncate();
	}

	private void truncate()
	{
		while (data.size() > maxSize)
		{
			data.pollLast();
		}
	}

	public int size()
	{
		return data.size();
	}

}
