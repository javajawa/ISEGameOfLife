package ise.gameoflife.simulations.evolution;

import java.util.ArrayList;
import java.util.Random;

import ise.gameoflife.genetics.Evolution;
import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.simulations.GeneticAgentSimulation;

public class SimulationEvolution
	extends Evolution<SimulationGenome, GeneticAgentSimulation>
{

	private final float elitistProportion = 0.50f;
	private long randSeed = System.currentTimeMillis();
	private Random rand = new Random(randSeed);

	@Override
	protected GeneticAgentSimulation newEntity(SimulationGenome genome)
	{
		return new GeneticAgentSimulation(genome);
	}

	@Override
	protected SimulationGenome newGenome()
	{
		// not getting entropy but guarantees unbiased simulations
		SimulationGenome genome = new SimulationGenome(rand.nextLong());
		return genome;
	}

	@Override
	protected void evaluate(GeneticAgentSimulation entity)
	{
		double fitness = 0;
		for (int i = 0; i < 5; i++)
		{
			// not getting entropy but guarantees unbiased simulations
			entity.setRandomSeed(rand.nextLong());
			entity.run();
			for (PublicAgentDataModel dataModel : entity.agentDataModels())
			{
				if (null == dataModel)
				{
					continue;
				}
				fitness += dataModel.getCurrentHappiness();
			}
		}
		entity.setFitness(fitness);
		System.out.print(".");
		// System.out.println("Iteration: " + this.currentIteration() +
		//		",\tentity fitness: " + fitness);
	}

	@Override
	protected boolean select(int rank, GeneticAgentSimulation entity)
	{
		if (rank <= 1)
		{
			System.out.print("\tIteration: " + this.currentIteration() +
					",\tfitness: " + entity.fitness());
		}

		if (rank <= this.population() * elitistProportion)
		{
			return true;
		}
		return false;
	}

	@Override
	protected void willBeginNextIteration()
	{
		System.out.print(",\tavg fit: " + this.avgFitness() + "\n");
	}

	public static void main(String[] args)
	{
		SimulationEvolution evolution = new SimulationEvolution();
		evolution.setIterations(1000);
		evolution.setPopulation(50);
		evolution.evolve();
	}

}
