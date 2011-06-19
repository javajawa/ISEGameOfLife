/**
 *
 */
package ise.gameoflife.simulations;

import ise.gameoflife.genetics.Evolvable;
import ise.gameoflife.participants.AbstractFreeAgentGroup;
import ise.gameoflife.simulations.evolution.SimulationGenome;

/**
 * @author admko
 *
 */
public class LearningAgentSimulation
	extends GenericSimulation implements Evolvable<SimulationGenome>
{

	/**
	 *
	 */
	public LearningAgentSimulation()
	{
		super("Basic Politics Testing Bed", 400, 0, 0.1);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see ise.gameoflife.simulations.GenericSimulation#plugins()
	 */
	@Override
	protected void plugins()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ise.gameoflife.simulations.GenericSimulation#foods()
	 */
	@Override
	protected void foods()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ise.gameoflife.simulations.GenericSimulation#agents()
	 */
	@Override
	protected void agents()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ise.gameoflife.simulations.GenericSimulation#groups()
	 */
	@Override
	protected void groups()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ise.gameoflife.simulations.GenericSimulation#events()
	 */
	@Override
	protected void events()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ise.gameoflife.simulations.GenericSimulation#chooseFreeAgentHandler()
	 */
	@Override
	protected Class<? extends AbstractFreeAgentGroup> chooseFreeAgentHandler()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SimulationGenome genome()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGenome(SimulationGenome genome)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public double fitness()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFitness(double aFitness)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
