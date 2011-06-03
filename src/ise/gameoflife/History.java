package ise.gameoflife;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Allows classes to easily store historical values in a type safe, controlled way
 * @param <T> The type of things being recorded historically
 * @author Benedict
 */
public class History<T> implements Serializable
{

	private static final long serialVersionUID = 1L;
	private LinkedList<T> data;
	private int maxSize;
	private UnmodifableHistory<T> u;

	/**
	 * Here is where History is written
	 */
	public History()
	{
		this(new LinkedList<T>(), 10);
	}

	/**
	 * Instantiates the History with a constrained size
	 * @param maxsize 
	 */
	public History(int maxsize)
	{
		this(new LinkedList<T>(), maxsize);
	}

	/**
	 * Instantiates the History with a fixed size and a certain number of
	 * entires
	 * @param data
	 * @param maxsize 
	 */
	History(LinkedList<T> data, int maxsize)
	{
		this.data = data;
		this.maxSize = maxsize;
	}

	/**
	 * If possible, clones the History
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	/**
	 * Gets the most recent element in our linked list history
	 * @return the present history
	 */
	public T getValue()
	{
		return data.element();
	}

	/**
	 * Gets the history from a specified number of turns ago
	 * @param turnsAgo
	 * @return the history a set number of turns ago
	 */
	public T getValue(int turnsAgo)
	{
		return data.get(turnsAgo);
	}

	/**
	 * Sets the current history value
	 * @param value
	 * @return the old history value
	 */
	public T setValue(T value)
	{
		return data.set(0, value);
	}

	/**
	 * Adds new entry into History
	 */
	public void newEntry()
	{
		data.push(null);
		truncate();
	}

	/**
	 * How big is the History?
	 * @return the max size of the history
	 */
	public int getMaxSize()
	{
		return maxSize;
	}

	/**
	 * Sets the maximum size of this History
	 * @param maxSize 
	 */
	public void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
		truncate();
	}

	/**
	 * Cut down the History to match the stated max size
	 */
	private void truncate()
	{
		while (data.size() > maxSize)
		{
			data.pollLast();
		}
	}

	/**
	 * 
	 * @return the size of the linked list History
	 */
	public int size()
	{
		return data.size();
	}
	
	
	/**
	 * Gets the class used to make the History unmodifiable History
	 * @return the unmodifiable history pointer
	 */
	public UnmodifableHistory<T> getUnmodifableHistory()
	{
		if (u == null) u = new UnmodifableHistory<T>(data, maxSize);
		return u;
	}
}
