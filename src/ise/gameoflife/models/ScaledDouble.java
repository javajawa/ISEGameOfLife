package ise.gameoflife.models;

/**
 *
 * @author Benedict
 */
public class ScaledDouble extends Number implements Comparable<Number>
{
	private static final long serialVersionUID = 1L;
	private int value;
	private int scaleFactor;
	private transient Double scaledValue = null;

	public ScaledDouble()
	{
		this(10);
	}

	public ScaledDouble(int scaleFactor)
	{
		this.value = 0;
		this.scaleFactor = scaleFactor;
	}

	private void calculate()
	{
		if (value > 0)
		{
			scaledValue = 1 - (Math.exp(-(double)value/scaleFactor) / 2);
		}
		else
		{
			scaledValue = Math.exp((double)value/scaleFactor) / 2;
		}
	}

	@Override
	public synchronized int intValue()
	{
		return value;
	}

	@Override
	public synchronized long longValue()
	{
		return (long)value;
	}

	@Override
	public synchronized float floatValue()
	{
		if (scaledValue == null) calculate();
		return scaledValue.floatValue();
	}

	@Override
	public synchronized double doubleValue()
	{
		if (scaledValue == null) calculate();
		return scaledValue;
	}

	@Override
	@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
	public synchronized int compareTo(Number o)
	{
		if (scaledValue == -1) calculate();
		return scaledValue.compareTo(o.doubleValue());
	}

	public synchronized void alterValue(int amount)
	{
		value += amount;
		this.scaledValue = null;
	}

	public synchronized void setValue(int value)
	{
		this.value = value;
		this.scaledValue = null;
	}

}
