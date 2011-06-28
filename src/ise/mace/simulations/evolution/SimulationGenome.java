/**
 *
 */
package ise.mace.simulations.evolution;

import ise.mace.genetics.Genome;
import ise.mace.tokens.AgentType;

/**
 *
 */
public class SimulationGenome extends Genome<SimulationGenome>
{
	private static final long serialVersionUID = 1L;
	private static final double initialFood = 0;
	private static final double consumption = 0;
	private static final AgentType type = null;
	private static final String comment = "Learning Agent";
	private static final int iterations = 200;
	private static final double foodConsumedPerAdvice = 0.1;
	private long randomSeed;

	@Override
	public void randomize()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public SimulationGenome mutate()
	{
		// TODO Auto-generated method stub
		return null;
	}

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

	public double initialFood()
	{
		return initialFood;
	}

	public double consumption()
	{
		return consumption;
	}

	public AgentType type()
	{
		return type;
	}

	public double socialBelief()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public double economicBelief()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
