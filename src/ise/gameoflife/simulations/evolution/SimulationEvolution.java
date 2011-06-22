package ise.gameoflife.simulations.evolution;

import java.util.ArrayList;

import ise.gameoflife.genetics.Evolution;
import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.simulations.GeneticAgentSimulation;

public class SimulationEvolution
	extends Evolution<SimulationGenome, GeneticAgentSimulation>
{

	private final float elitistProportion = 0.10f;
	private long randSeed = System.currentTimeMillis();

	@Override
	protected GeneticAgentSimulation newEntity(SimulationGenome genome)
	{
		return new GeneticAgentSimulation(genome);
	}

	@Override
	protected SimulationGenome newGenome()
	{
		SimulationGenome genome = new SimulationGenome();
		genome.setRandomSeed(randSeed++);
		return genome;
	}

	@SuppressWarnings("unused")
	@Override
	protected void evaluate(GeneticAgentSimulation entity)
	{
		entity.run();
		ArrayList<PublicAgentDataModel> agentDataModels = entity.agentDataModels();
		for (PublicAgentDataModel dataModel : agentDataModels)
		{
			// dataModel.getHappinessHistory();
		}
		entity.setFitness(0); // TODO proper evaluation
	}

	@Override
	protected boolean select(int rank, GeneticAgentSimulation entity)
	{
		if (rank <= 1)
		{
			System.out.println("Iteration: " + this.currentIteration() +
					",\tfitness: " + entity.fitness());
		}

		if (rank <= this.population() * elitistProportion)
		{
			return true;
		}
		return false;
	}

	public static void main(String[] args)
	{
		SimulationEvolution evolution = new SimulationEvolution();
		evolution.setIterations(1000);
		evolution.setPopulation(50);
		evolution.evolve();
	}

}
