package ise.gameoflife.simulations;

import ise.gameoflife.agents.TestAgent;
import ise.gameoflife.agents.TestGroupableAgent;
import ise.gameoflife.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.gameoflife.participants.AbstractFreeAgentGroup;
import ise.gameoflife.plugins.HuntersAlivePlugin;
import ise.gameoflife.plugins.database.DatabasePlugin;
import ise.gameoflife.plugins.DebugSwitchPlugin;
import ise.gameoflife.plugins.HunterListPlugin;
import ise.gameoflife.plugins.PoliticalCompassPlugin;

/**
 *
 * @author Benedict Harcourt
 */
public class FreeAgentsTest extends GenericSimulation
{

	public FreeAgentsTest()
	{
		super("Test of Grouping of Free Agents", 250, 0, 0);
	}

	@Override
	protected void plugins()
	{
		addPlugin(new DebugSwitchPlugin());
		addPlugin(new HuntersAlivePlugin(getPath() + "/population.png", 1500, 1200));
		addPlugin(new DatabasePlugin(comment,false));
		addPlugin(new HunterListPlugin());
		addPlugin(new PoliticalCompassPlugin());
	}

	@Override
	protected void foods()
	{
		addFood("Rabbit", 1, 1);
		addFood("Stag", 5, 2);
	}

	@Override
	protected void agents()
	{
		for (int i = 0; i < 5; i++)
		{
			addAgent(new TestGroupableAgent(20, 2));
		}
		addAgent(new TestAgent(20, 2));
	}

	@Override
	protected void groups()
	{
	}

	@Override
	protected void events()
	{
	}

	@Override
	protected Class<? extends AbstractFreeAgentGroup> chooseFreeAgentHandler()
	{
		return BasicFreeAgentGroup.class;
	}

}
