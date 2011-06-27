package ise.mace.simulations.validation;

import ise.mace.agents.TestAgent;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.plugins.DebugSwitchPlugin;
import ise.mace.plugins.HuntersAlivePlugin;
import ise.mace.simulations.GenericSimulation;

/**
 * <p>Single Agent Simulation</p>
 *
 * <ul>
 * <li>Cycles: 500</li>
 * <li>Agents: 1 {@link TestAgent}
 *   <ul>
 *     <li>Initial Food: 20</li>
 *     <li>Default Consumption: 5</li>
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
 *     <li>Chicken: 2 from 1</li>
 *   </ul>
 * </li>
 * <li>Database: None</li>
 * </ul>
 */
public class SingleAgent extends GenericSimulation
{

	/**
	 * Creates a new {@link SingleAgent} simulation
	 * @see SingleAgent
	 */
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
	}

	@Override
	protected void events()
	{
	}

}
