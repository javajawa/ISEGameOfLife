package ise.mace.simulations.validation;

import ise.mace.agents.TestPoliticalAgent;
import ise.mace.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.plugins.DebugSwitchPlugin;
import ise.mace.plugins.HunterListPlugin;
import ise.mace.plugins.HuntersAlivePlugin;
import ise.mace.plugins.GroupPoliticalCompassPlugin;
import ise.mace.simulations.GenericSimulation;
import ise.mace.tokens.AgentType;

/**
 * <p>Stupid Sharing</p>
 * <p><strong>Proposition:</strong> If one agent needs more food than the other, agents still distribute
 * the food equally. This can lead to the scenario where two agents hunt together, with one living
 * happily, but the other gradually starving. When the other dies, the remaining agent can only
 * hunt rabbits, and so it dies too.</p>
 *
 * <ul>
 * <li>Cycles: 500</li>
 * <li>Agents: 2 {@link TestPoliticalAgent}, one with default Food consumption, one with a food consumption of 4
 *   <ul>
 *     <li>Initial Food: 20</li>
 *     <li>Default Consumption: 2</li>
 *   </ul>
 * <li>Advice Consumption: 0</li>
 * <li>Free Group: None</li>
 * <li>Groups:
 *   <ul>
 *     <li>None</li>
 *   </ul>
 * </li>
 * <li>Foods:
 *   <ul>
 *     <li>Rabbit:  1 from 1</li>
 *     <li>Stag: 6 from 2</li>
 *   </ul>
 * </li>
 * <li>Database: None</li>
 * <li>Default seed: 0</li>
 * </ul>
 */

public class StupidSharing extends GenericSimulation
{
	/**
	 * Creates a {@link StupidSharing} simulation
	 * @see StupidSharing
	 */
	public StupidSharing()
	{
		super("Two agents starving to death", 500, 0, 0);
	}

	@Override
	protected void foods()
	{
		addFood("Rabbit", 1, 1);
		addFood("Stag", 6, 2);
	}

	@Override
	protected void agents()
	{
		addAgent(new TestPoliticalAgent(20, 4, AgentType.AC, 0.5, 0.5));
		addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.5, 0.5));
	}

	@Override
	protected void groups()
	{
	}

	@Override
	protected Class<? extends AbstractFreeAgentGroup> chooseFreeAgentHandler()
	{
		return BasicFreeAgentGroup.class;
	}

	@Override
	protected void plugins()
	{
		addPlugin(new DebugSwitchPlugin());
		addPlugin(new HuntersAlivePlugin(getPath() + "/population.png", 300, 200));
		//addPlugin(new DatabasePlugin("Simulation comment",false));
		addPlugin(new HunterListPlugin());
		addPlugin(new GroupPoliticalCompassPlugin());
	}

	@Override
	protected void events()
	{
	}
}
