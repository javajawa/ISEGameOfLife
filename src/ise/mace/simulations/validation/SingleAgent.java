package ise.mace.simulations.validation;

import ise.mace.agents.TestAgent;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.plugins.database.DatabasePlugin;
import ise.mace.plugins.DebugSwitchPlugin;
import ise.mace.plugins.HunterListPlugin;
import ise.mace.plugins.HuntersAlivePlugin;
import ise.mace.plugins.PoliticalCompassPlugin;
import ise.mace.simulations.GenericSimulation;

/**
 *
 */
public class SingleAgent extends GenericSimulation
{

	public SingleAgent()
	{
		super("Single agent starving to death", 100, 0, 0);
	}

	@Override
	protected void foods()
	{
		addFood("Rabbit", 1, 1);
		addFood("Chicken", 2, 1);
	}

	@Override
	protected void agents()
	{
		addAgent(new TestAgent(20, 5));
	}

	@Override
	protected void groups()
	{
	}

	@Override
	protected Class<? extends AbstractFreeAgentGroup> chooseFreeAgentHandler()
	{
		return null;
	}

	@Override
	protected void plugins()
	{
		addPlugin(new DebugSwitchPlugin());
		addPlugin(new HuntersAlivePlugin(getPath() + "/population.png", 1500, 1200));
		addPlugin(new DatabasePlugin("Simulation comment",false));
		addPlugin(new HunterListPlugin());
		addPlugin(new PoliticalCompassPlugin());
	}

	@Override
	protected void events()
	{
	}

}
