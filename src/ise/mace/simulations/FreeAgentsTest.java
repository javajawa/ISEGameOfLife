package ise.mace.simulations;

import ise.mace.agents.TestAgent;
import ise.mace.agents.TestGroupableAgent;
import ise.mace.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.plugins.HuntersAlivePlugin;
import ise.mace.plugins.database.DatabasePlugin;
import ise.mace.plugins.DebugSwitchPlugin;
import ise.mace.plugins.HunterListPlugin;
import ise.mace.plugins.GroupPoliticalCompassPlugin;

/**
 * <p>Free Agents Simulation</p>
 *
 * <ul>
 * <li>Cycles: 250</li>
 * <li>Agents: 5 {@link TestGroupableAgent}s and 1 {@link TestAgent}
 *   <ul>
 *     <li>Initial Food: 20</li>
 *     <li>Default Consumption: 2</li>
 *   </ul>
 * <li>Advice Consumption: 0.0</li>
 * <li>Free Group: {@link BasicFreeAgentGroup}</li>
 * <li>Groups:
 *   <ul>
 *			<li>None</li>
 *   </ul>
 * </li>
 * <li>Foods:
 *   <ul>
 *     <li>Rabbit: 1 from 1</li>
 *     <li>Stag: 5 from 2</li>
 *   </ul>
 * </li>
 * <li>Database: Local Database</li>
 * <li>Default seed: 0</li>
 * </ul>
 */
/**
 *
 */
public class FreeAgentsTest extends GenericSimulation
{
	/**
	 * Creates a FreeAgentsTest simulation
	 * @see FreeAgentsTest
	 */
	public FreeAgentsTest()
	{
		super("Test of Grouping of Free Agents", 250, 0, 0);
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
