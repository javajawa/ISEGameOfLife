package ise.mace.simulations.validation;

import ise.mace.agents.TestPoliticalAgent;
import ise.mace.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.plugins.DebugSwitchPlugin;
import ise.mace.plugins.HunterListPlugin;
import ise.mace.plugins.HuntersAlivePlugin;
import ise.mace.plugins.PoliticalCompassPlugin;
import ise.mace.simulations.GenericSimulation;
import ise.mace.tokens.AgentType;

/**
 * <p>The Big Rabbit Simulation</p>
 *
 * <ul>
 * <li>Cycles: 500</li>
 * <li>Agents: 1 {@link TestPoliticalAgent}
 *   <ul>
 *     <li>Initial Food: 20</li>
 *     <li>Default Consumption: 2</li>
 *     <li>Beliefs: (0.5,0.5)</li>
 *   </ul>
 * <li>Advice Consumption: 0</li>
 * <li>Free Group: {@link BasicFreeAgentGroup}</li>
 * <li>Groups:
 *   <ul>
 *     <li>None</li>
 *   </ul>
 * </li>
 * <li>Foods:
 *   <ul>
 *     <li>Rabbit: 5 from 1</li>
 *     <li>Stag:   2 from 2</li>
 *   </ul>
 * </li>
 * <li>Database: None</li>
 * <li>Default seed: 0</li>
 * </ul>
 */
public class BigRabbitDoubleAC extends GenericSimulation
{

	/**
	 * Crates the Big Rabbit Validation Simulation
	 * @see BigRabbitDoubleAC Big Rabbit Simulation
	 */
	public BigRabbitDoubleAC()
	{
		super("Single agent starving to death", 500, 0, 0);
	}

	@Override
	protected void foods()
	{
		addFood("Rabbit", 5, 1);
		addFood("Stag", 2, 2);
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
		addPlugin(new PoliticalCompassPlugin());
	}

	@Override
	protected void events()
	{
	}

}
