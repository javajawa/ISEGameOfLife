package ise.gameoflife.simulations.evolution;

import java.util.Random;

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

	private static final double mutateRate = 0.05f;
	private static final double crossOverRate = 0.30f;

	private static final double initialFood = 20;
	private static final double consumption = 2;
	private static final String comment = "Genetic Agent";
	private static final int iterations = 400;
	private static final int population = 8;
	private static final double foodConsumedPerAdvice = 0.1;

	private double socialBelief = 0;
	private double economicBelief = 0;
	private AgentType type = null;
	private NetworkGenome chooseFoodGenome = new NetworkGenome(7,7,2);

	private long randomSeed;
	private Random rand = null;

	public SimulationGenome(long seed)
	{
		this.setRandomSeed(seed);
		rand = new Random(seed);
	}

	@Override
	public void randomize()
	{
		chooseFoodGenome.randomize();

		this.setSocialBelief(rand.nextDouble());
		this.setEconomicBelief(rand.nextDouble());

		this.setType(AgentType.values()[rand.nextInt(AgentType.values().length)]);
	}

	@Override
	public SimulationGenome mutate()
	{
		SimulationGenome genome = new SimulationGenome(randomSeed++);

		genome.setChooseFoodGenome(chooseFoodGenome.mutate());
		genome.setSocialBelief(socialBelief);
		genome.setEconomicBelief(economicBelief);
		genome.setType(type);

		if (rand.nextDouble() < mutateRate)
			genome.setSocialBelief(rand.nextDouble());
		if (rand.nextDouble() < mutateRate)
			genome.setEconomicBelief(rand.nextDouble());
		if (rand.nextDouble() < mutateRate)
			genome.setType(AgentType.values()[rand.nextInt(AgentType.values().length)]);

		return genome;
	}

	@Override
	public SimulationGenome crossOver(SimulationGenome genome)
	{
		SimulationGenome newGenome = new SimulationGenome(randomSeed++);

		NetworkGenome newCFGenome = genome.chooseFoodGenome();
		newCFGenome = this.chooseFoodGenome().crossOver(newCFGenome);
		newGenome.setChooseFoodGenome(newCFGenome);

		newGenome.setSocialBelief(socialBelief);
		newGenome.setEconomicBelief(economicBelief);
		newGenome.setType(type);

		if (rand.nextDouble() < crossOverRate)
			newGenome.setEconomicBelief(genome.economicBelief());
		if (rand.nextDouble() < crossOverRate)
			newGenome.setSocialBelief(genome.socialBelief());
		if (rand.nextDouble() < crossOverRate)
			newGenome.setType(genome.type());

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

	public void setType(AgentType type)
	{
		this.type = type;
	}

	public AgentType type()
	{
		return type;
	}

	public void setSocialBelief(double socialBelief)
	{
		this.socialBelief = socialBelief;
	}

	public double socialBelief()
	{
		return socialBelief;
	}

	public void setEconomicBelief(double economicBelief)
	{
		this.economicBelief = economicBelief;
	}

	public double economicBelief()
	{
		return economicBelief;
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
