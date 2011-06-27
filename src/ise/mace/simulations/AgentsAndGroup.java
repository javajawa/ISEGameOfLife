package ise.mace.simulations;

import ise.mace.agents.TestAgent;
import ise.mace.agents.TestGroupableAgent;
import ise.mace.groups.TestGroup;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.plugins.HuntersAlivePlugin;
import ise.mace.plugins.database.DatabasePlugin;
import ise.mace.plugins.DebugSwitchPlugin;
import ise.mace.plugins.HunterListPlugin;
import ise.mace.plugins.PoliticalCompassPlugin;


/**
 *
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
		addPlugin(new DatabasePlugin("Simulation comment",false));
		addPlugin(new HunterListPlugin());
		addPlugin(new PoliticalCompassPlugin());
	}

	@Override
	protected void events()
	{
	}

}
