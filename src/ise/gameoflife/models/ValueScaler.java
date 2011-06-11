package ise.gameoflife.models;

/**
 * @author Benedict
 * @deprecated Use {@link ScaledDouble} instead
 */
@Deprecated
public class ValueScaler
{
	public static double scale(double old, double amount)
	{
		return scale(old, amount, 0.1);
	}

	/**
	 * Allows the easy changing of a value between 0 and 1 by an number of jumps
	 * whilst guaranteeing that the returned value will never fall outside of the
	 * range 0 - 1. Also, The operation is not directly reversible: jumping one
	 * +1 units then -1 units will result in a smaller value than the one you
	 * started of with.
	 * 
	 * The function is implemented in a similar way to Xeno's paradox - each jump
	 * towards the end of the range is smaller than the previous space, as defined
	 * by the space parameter. This parameter must lie within the range 0-1
	 * @param old The old value that the scaled member had
	 * @param amount The number of jumps to make
	 * @param step The size of each jump
	 * @return The new value, scaled between 0 - 1
	 */
	public static double scale(double old, double amount, double step)
	{
		if (step <= 1) step = 1/step;
		if (old < 0.5)
		{
			return 1-scale(1-old, -amount, step);
		}

		// First Phase: Retrieve the equivilant amount from old with given step
		double ls = Math.log(step);
		double am = -Math.log(2-2*old)*ls;
		am += amount;

		if (am < 0)
		{
			return (Math.exp(am) / 2 / ls);
		}
		else
		{
			return 1 - (Math.exp(-am) / 2 / ls);
		}
	}

	private ValueScaler()
	{
	}
}
