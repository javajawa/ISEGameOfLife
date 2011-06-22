package ise.gameoflife.agents;

import ise.gameoflife.simulations.evolution.SimulationGenome;

public class GeneticAgent extends TestPoliticalAgent
{

	private static final long serialVersionUID = 1L;

	public GeneticAgent(SimulationGenome genome)
	{
		super(genome.initialFood(), genome.consumption(),
				genome.type(), genome.socialBelief(), genome.economicBelief());
	}

}
