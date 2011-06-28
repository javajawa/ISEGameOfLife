package ise.mace.models;

import java.util.Map;

/**
 *
 * @param <K>
 * @param <V>
 */
public class Tuple<K, V> implements Map.Entry<K, V>
{
	private K key;
	private V value;

	public Tuple()
	{
		//Do nothing
	}

	public Tuple(K key, V value)
	{
		this.key = key;
		this.value = value;
	}

	public K getKey()
	{
		return this.key;
	}

	public V getValue()
	{
		return this.value;
	}

	public V setValue(V newValue)
	{
		V tmp = value;
		this.value = newValue;
		return tmp;
	}

	public void setKey(K newKey)
	{
		this.key = newKey;
	}

	public void add(K newKey, V newValue)
	{
		this.key = newKey;
		this.value = newValue;
	}

	public boolean contains(K cKey, V cValue)
	{
		if ((this.key == cKey) && (this.value == cValue))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean equalsKey(K cKey)
	{
		if (this.key == cKey)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
