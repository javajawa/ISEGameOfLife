package ise.gameoflife.models;

/**
 * TODO: Add license documentation
 * @author Benedict
 */
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
		if (step <= 0 || step >= 1) throw new IllegalArgumentException("Step must fall within the range 0 to 1, exlcusive");
		if (amount < 0)
		{
			return 1-scale(1-old, -amount, step);
		}
		double space = 1-old;
		space *= Math.pow(1-step, amount);
		return 1-space;
	}

	private ValueScaler()
	{
	}
}
