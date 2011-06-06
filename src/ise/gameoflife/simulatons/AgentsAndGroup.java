package ise.gameoflife.simulatons;

import ise.gameoflife.agents.TestAgent;
import ise.gameoflife.agents.TestGroupableAgent;
import ise.gameoflife.groups.TestGroup;
import ise.gameoflife.participants.AbstractFreeAgentGroup;
import ise.gameoflife.plugins.ErrorLog;
import ise.gameoflife.plugins.HuntersAlivePlugin;
import ise.gameoflife.plugins.DatabasePlugin;
import ise.gameoflife.plugins.DebugSwitchPlugin;
import ise.gameoflife.plugins.HunterListPlugin;
import ise.gameoflife.plugins.PoliticalCompassPlugin;


/**
 *
 * @author Benedict Harcourt
 */
public class AgentsAndGroup extends GenericSimulation
{

	public AgentsAndGroup()
	{
		super("Simle Groups Testing Simulaton", 200, 0, 0);
	}

	@Override
	protected void agents()
	{
		for (int i = 0; i < 10; i++)
		{
			addAgent(new TestAgent(20, 2));
			addAgent(new TestGroupableAgent(20, 2));
		}
	}

	@Override
	protected void foods()
	{
		addFood("Rabbit", 1, 1);
		addFood("Stag", 5, 2);
	}

	@Override
	protected void groups()
	{
		addGroup(TestGroup.class);
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
		addPlugin(new ErrorLog());
		addPlugin(new DatabasePlugin(1,"Simulation comment",false));
		addPlugin(new HunterListPlugin());
		addPlugin(new PoliticalCompassPlugin());
	}

	@Override
	protected void events()
	{
	}

}
