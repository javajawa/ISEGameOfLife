package ise.gameoflife.simulations.evolution;

import ise.gameoflife.genetics.Evolution;
import ise.gameoflife.simulations.LearningAgentSimulation;

public class AgentSimulationEvolution
	extends Evolution<SimulationGenome, LearningAgentSimulation>
{

	@Override
	protected LearningAgentSimulation newEntity(SimulationGenome genome)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SimulationGenome newGenome()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void evaluate(LearningAgentSimulation entity)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean select(int rank, LearningAgentSimulation entity)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
