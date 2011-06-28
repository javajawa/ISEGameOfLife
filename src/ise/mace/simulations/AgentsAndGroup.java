package ise.mace.simulations;

import ise.mace.agents.TestAgent;
import ise.mace.agents.TestGroupableAgent;
import ise.mace.groups.TestGroup;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.plugins.HuntersAlivePlugin;
import ise.mace.plugins.database.DatabasePlugin;
import ise.mace.plugins.DebugSwitchPlugin;
import ise.mace.plugins.HunterListPlugin;
import ise.mace.plugins.GroupPoliticalCompassPlugin;

/**
 * <p>Politics Simulation</p>
 *
 * <ul>
 * <li>Cycles: 200</li>
 * <li>Agents: 10 {@link PoliticalAgent}s
 *   <ul>
 *     <li>Initial Food: 20</li>
 *     <li>Default Consumption: 2</li>
 *   </ul>
 * <li>Advice Consumption: 0.0</li>
 * <li>Free Group: Null</li>
 * <li>Groups:
 *   <ul>
 *     <li>{@link TestGroup}</li>
 *   </ul>
 * </li>
 * <li>Foods:
 *   <ul>
 *     <li>Rabbit: 1 from 1</li>
 *     <li>Stag: 5 from 2</li>
 *   </ul>
 * </li>
 * <li>Database: Local Databse/li>
 * <li>Default seed: 0</li>
 * </ul>
 */
public class AgentsAndGroup extends GenericSimulation
{
	/**
	 * Creates an AgentsAndGroup() simulation
	 * @see AgentsAndGroup
	 */
	public AgentsAndGroup()
	{
		super("Simple Groups Testing Simulaton", 200, 0, 0);
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
		addPlugin(new DatabasePlugin(comment, false));
		addPlugin(new HunterListPlugin());
		addPlugin(new GroupPoliticalCompassPlugin());
	}

	@Override
	protected void events()
	{
	}
}
