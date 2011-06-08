package ise.gameoflife.simulatons;

import ise.gameoflife.agents.TestAgent;
import ise.gameoflife.participants.AbstractFreeAgentGroup;
import ise.gameoflife.plugins.DatabasePlugin;
import ise.gameoflife.plugins.DebugSwitchPlugin;
import ise.gameoflife.plugins.HunterListPlugin;
import ise.gameoflife.plugins.HuntersAlivePlugin;
import ise.gameoflife.plugins.PoliticalCompassPlugin;

/**
 *
 * @author Benedict
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
		addPlugin(new DatabasePlugin(1,"Simulation comment",false));
		addPlugin(new HunterListPlugin());
		addPlugin(new PoliticalCompassPlugin());
	}

	@Override
	protected void events()
	{
	}

}
