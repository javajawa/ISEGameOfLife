package ise.mace.models;

/**
 *
 */
public class ScaledDouble extends Number implements Comparable<Number>
{
	private static final long serialVersionUID = 1L;
	private int value;
	private int scaleFactor;
	private transient Double scaledValue = null;
	private transient ImmutableScaledDouble s = null;

	public static double scale(double old, double amount, double scale)
	{
		ScaledDouble d = new ScaledDouble(old, scale);
		d.alterValue((int)amount);
		return d.doubleValue();
	}

	public ScaledDouble()
	{
		this(10);
	}

	public ScaledDouble(int scaleFactor)
	{
		this.value = 0;
		this.scaleFactor = scaleFactor;
	}

	public ScaledDouble(double initialValue)
	{
		this(initialValue, 10);
	}

	public ScaledDouble(double initialValue, double scaleFactor)
	{
		this(initialValue, (int)(scaleFactor < 1 ? 1.0 / scaleFactor : scaleFactor));
	}

	public ScaledDouble(double initialValue, int scaleFactor)
	{
		this(scaleFactor);
		if (initialValue < 0 || initialValue > 1)
			throw new IllegalArgumentException("Value must be between 0 and 1");

		int mul = 1;

		if (initialValue > 0.5)
		{
			initialValue = 1 - initialValue;
			mul = -1;
		}
		value = mul * (int)Math.round(scaleFactor * Math.log(2 * initialValue));
	}

	private void calculate()
	{
		if (value > 0)
		{
			scaledValue = 1 - (Math.exp(-(double)value / scaleFactor) / 2);
		}
		else
		{
			scaledValue = Math.exp((double)value / scaleFactor) / 2;
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

	public ScaledDouble safeClone()
	{
		if (s == null) s = new ImmutableScaledDouble(this);
		return s;
	}
}
