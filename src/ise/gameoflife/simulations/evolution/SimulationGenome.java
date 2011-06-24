/**
 *
 */
package ise.gameoflife.simulations.evolution;

import ise.gameoflife.genetics.Genome;
import ise.gameoflife.neuralnetworks.NetworkGenome;
import ise.gameoflife.tokens.AgentType;

/**
 * @author admko
 *
 */
public class SimulationGenome extends Genome<SimulationGenome>
{

	private static final long serialVersionUID = 1L;

	private static final double initialFood = 20;
	private static final double consumption = 3.2;
	private static final AgentType type = null;
	private static final String comment = "Genetic Agent";
	private static final int iterations = 100;
	private static final int population = 40;
	private static final double foodConsumedPerAdvice = 0.1;

	private NetworkGenome chooseFoodGenome = new NetworkGenome(7,8,2);

	private long randomSeed;

	@Override
	public void randomize()
	{
		chooseFoodGenome.randomize();
	}

	@Override
	public SimulationGenome mutate()
	{
		SimulationGenome genome = new SimulationGenome();
		genome.setChooseFoodGenome(chooseFoodGenome.mutate());
		return genome;
	}

	@Override
	public SimulationGenome crossOver(SimulationGenome genome)
	{
		SimulationGenome newGenome = new SimulationGenome();
		NetworkGenome newCFGenome = genome.chooseFoodGenome();
		newCFGenome = chooseFoodGenome.crossOver(newCFGenome);
		newGenome.setChooseFoodGenome(newCFGenome);
		return newGenome;
	}

	public String comment()
	{
		return comment;
	}

	public int iterations()
	{
		return iterations;
	}

	public int population()
	{
		return population;
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

	public void setChooseFoodGenome(NetworkGenome genome)
	{
		this.chooseFoodGenome = genome;
	}

	public NetworkGenome chooseFoodGenome()
	{
		return chooseFoodGenome;
	}

}
