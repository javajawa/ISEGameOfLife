/**
 *
 */
package ise.gameoflife.simulations.evolution;

import ise.gameoflife.genetics.Genome;

/**
 * @author admko
 *
 */
public class SimulationGenome extends Genome<SimulationGenome>
{

	private static final long serialVersionUID = 1L;

	private final String comment = "Learning Agent";
	private final int iterations = 200;
	private final double foodConsumedPerAdvice = 0.1;
	private long randomSeed;

	/* (non-Javadoc)
	 * @see ise.gameoflife.genetics.Genome#randomize()
	 */
	@Override
	public void randomize()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ise.gameoflife.genetics.Genome#mutate()
	 */
	@Override
	public SimulationGenome mutate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ise.gameoflife.genetics.Genome#crossOver(ise.gameoflife.genetics.Genome)
	 */
	@Override
	public SimulationGenome crossOver(SimulationGenome genome)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String comment()
	{
		return comment;
	}

	public int iterations()
	{
		return iterations;
	}

	public double foodConsumedPerAdvice()
	{
		return foodConsumedPerAdvice;
	}

	public void setRandomSeed(long seed)
	{
		this.randomSeed = seed;
	}

	public long randomSeed()
	{
		return randomSeed;
	}

}
