package ise.gameoflife.simulations.evolution;

import ise.gameoflife.genetics.Evolution;
import ise.gameoflife.simulations.LearningAgentSimulation;

public class AgentSimulationEvolution
	extends Evolution<SimulationGenome, LearningAgentSimulation>
{

	private final float elitistProportion = 0.10f;

	@Override
	protected LearningAgentSimulation newEntity(SimulationGenome genome)
	{
		return new LearningAgentSimulation(genome);
	}

	@Override
	protected SimulationGenome newGenome()
	{
		return new SimulationGenome();
	}

	@Override
	protected void evaluate(LearningAgentSimulation entity)
	{
		// TODO Evaluation of entity
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

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		AgentSimulationEvolution evolution = new AgentSimulationEvolution();
		evolution.setIterations(1000);
		evolution.setPopulation(50);
		evolution.evolve();
	}

}
