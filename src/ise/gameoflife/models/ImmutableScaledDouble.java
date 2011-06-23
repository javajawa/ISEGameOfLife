package ise.gameoflife.models;

/**
 *
 * @author benedict
 */
public class ImmutableScaledDouble extends ScaledDouble
{
	private static final long serialVersionUID = 1L;
	private ScaledDouble source;

	ImmutableScaledDouble(ScaledDouble source)
	{
		this.source = source;
	}

	@Override
	public synchronized void alterValue(int amount)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void setValue(int value)
	{
		throw new UnsupportedOperationException();
	}

}
