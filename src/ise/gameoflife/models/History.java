package ise.gameoflife.models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import presage.util.ObjectCloner;

/**
 * Allows classes to easily store historical values in a type safe, controlled way
 * @param <T> The type of things being recorded historically
 * @author Benedict
 */
public class History<T extends Serializable> implements Serializable
{

	private static final long serialVersionUID = 1L;
	@ElementList(type=Object.class)
	private LinkedList<T> data;
	@Element
	private int maxSize;
	private UnmodifiableHistory<T> u;

	/**
	 * Here is where History is written
	 * @deprecated For serialisability
	 */
	@Deprecated
	public History()
	{
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
	 * Adds new entry into History, using either the current value, or null to
	 * populate it
	 * @param cloneOld Use (a deep copy of) the previous value
	 */
	@SuppressWarnings("unchecked")
	public void newEntry(boolean cloneOld)
	{
		if (cloneOld && !data.isEmpty())
		{
			T clone;
			try
			{
				clone = (T)ObjectCloner.deepCopy(data.element());
			}
			catch (Exception ex)
			{
				Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, ex);
				clone = null;
			}
			data.push(clone);
		}
		else
		{
			data.push(null);
		}
		truncate();
	}

	/**
	 * Creates a new entry, initialised as null
	 */
	public void newEntry()
	{
		newEntry(false);
	}
	
	/**
	 * Creates a new entry with a specific value
	 * @param value The value to use
	 */
	public void newEntry(T value)
	{
		data.push(value);
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
	 * 
	 * @return the size of the linked list History
	 */
	public boolean isEmpty()
	{
		return data.isEmpty();
	}

	/**
	 * Gets the class used to make the History unmodifiable History
	 * @return the unmodifiable history pointer
	 */
	public UnmodifiableHistory<T> getUnmodifableHistory()
	{
		if (u == null) u = new UnmodifiableHistory<T>(data, maxSize);
		return u;
	}
}
