package ise.mace.models;

import java.io.Serializable;
import java.util.Map;

/**
 * Models a ordered-2-tuple or key-value pair.
 * THis class is not for use with a backing map, and only implements the
 * entry interface for portability
 * @param <K> Class of the key
 * @param <V> Class of the Value
 */
public class Tuple<K, V> implements Map.Entry<K, V>, Serializable
{
	/**
	 * Serialisation UID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Value in 'key'
	 */
	private K key;
	/**
	 * Value in 'value'
	 */
	private V value;

	/**
	 * Creates an empty tuple
	 */
	public Tuple()
	{
		//Do nothing
	}

	/**
	 * Creates a new tuple with these values
	 * @param key Key value
	 * @param value Value value
	 */
	public Tuple(K key, V value)
	{
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey()
	{
		return this.key;
	}

	@Override
	public V getValue()
	{
		return this.value;
	}

	@Override
	public V setValue(V newValue)
	{
		V tmp = value;
		this.value = newValue;
		return tmp;
	}

	/**
	 * Changes the key of this entry
	 * @param newKey
	 */
	public void setKey(K newKey)
	{
		this.key = newKey;
	}

	/**
	 * Convenience method for setting both the key and value at once
	 * @param newKey New key value
	 * @param newValue New value value
	 * @see #setKey(java.lang.Object)
	 * @see #setValue(java.lang.Object)
	 */
	public void set(K newKey, V newValue)
	{
		this.setKey(newKey);
		this.setValue(newValue);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj)
	{
		if (obj instanceof Tuple) return equals((Tuple<K, V>)obj);
		return super.equals(obj);
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 59 * hash + (this.key != null ? this.key.hashCode() : 0);
		hash = 59 * hash + (this.value != null ? this.value.hashCode() : 0);
		return hash;
	}

	/**
	 * Compares this tuple to a key and a value
	 * @param cKey Key to check against
	 * @param cVal Value to check against
	 * @return If both KEy and Value are {@link Object#equals(java.lang.Object)
	 * equal} to the values in this tuple
	 */
	public boolean equals(K cKey, V cVal)
	{
		boolean kmatch = (key == null ? cKey == null : key.equals(cKey));
		boolean vmatch = (key == null ? cVal == null : key.equals(cVal));
		return kmatch && vmatch;
	}

	/**
	 * Compares this tuple to another with the same type erasure.
	 * @param other Other tuple
	 * @return If the key and value are equal
	 */
	public boolean equals(Tuple<? extends K, ? extends V> other)
	{
		return equals(other.getKey(), other.getValue());
	}

	/**
	 * Determines if the key of this tuple equals the supplied value
	 * @param cKey Value to check against
	 * @return
	 */
	public boolean keyequals(K cKey)
	{
		return key == null ? cKey == null : key.equals(cKey);
	}
}
