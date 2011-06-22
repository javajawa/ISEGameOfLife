package ise.gameoflife.simulations.evolution;

import java.util.ArrayList;

import ise.gameoflife.genetics.Evolution;
import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.simulations.LearningAgentSimulation;

public class AgentSimulationEvolution
	extends Evolution<SimulationGenome, LearningAgentSimulation>
{

	private final float elitistProportion = 0.10f;
	private long randSeed = System.currentTimeMillis();

	@Override
	protected LearningAgentSimulation newEntity(SimulationGenome genome)
	{
		return new LearningAgentSimulation(genome);
	}

	@Override
	protected SimulationGenome newGenome()
	{
		SimulationGenome genome = new SimulationGenome();
		genome.setRandomSeed(randSeed++);
		return genome;
	}

	@Override
	protected void evaluate(LearningAgentSimulation entity)
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
	protected boolean select(int rank, LearningAgentSimulation entity)
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
		AgentSimulationEvolution evolution = new AgentSimulationEvolution();
		evolution.setIterations(1000);
		evolution.setPopulation(50);
		evolution.evolve();
	}

}
