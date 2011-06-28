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
 * <p>The Hungry Cooperating Double</p>
 * <p><strong>Proposition:</strong> Two co-operating agents will die in a finite amount
 *	of time if the stag’s value is < twice their daily food requirements.</p>
 *
 * <ul>
 * <li>Cycles: 500</li>
 * <li>Agents: 2 {@link TestAgent}
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
 *     <li>Stag: 3 from 2</li>
 *   </ul>
 * </li>
 * <li>Database: None</li>
 * <li>Default seed: 0</li>
 * </ul>
 */
public class HungryDoubleAC extends GenericSimulation
{
	/**
	 * Creates a new {@link HungryDoubleAC simulation}
	 * @see HungryDoubleAC
	 */
	public HungryDoubleAC()
	{
		super("Two agents starving to death", 500, 0, 0);
	}

	@Override
	protected void foods()
	{
		addFood("Rabbit", 1, 1);
		addFood("Stag", 3, 2);
	}

	@Override
	protected void agents()
	{
		addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.5, 0.5));
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
