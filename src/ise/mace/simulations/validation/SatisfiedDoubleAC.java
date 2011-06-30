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
 * <p>The Satisfied Cooperating Double</p>
 * <p><strong>Proposition:</strong> Two always co-operating agents will not die if the
 * value of a stag is ≥ the twice their daily food requirements.</p>
 *
 * <ul>
 * <li>Cycles: 500</li>
 * <li>Agents: 2 {@link TestPoliticalAgent}
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
 *     <li>Stag: 5 from 2</li>
 *   </ul>
 * </li>
 * <li>Database: None</li>
 * <li>Default seed: 0</li>
 * </ul>
 */
public class SatisfiedDoubleAC extends GenericSimulation
{
	/**
	 * Creates a new {@link SatisfiedDoubleAC simulation}
	 * @see SatisfiedDoubleAC
	 */
	public SatisfiedDoubleAC()
	{
		super("Two agents surviving forever", 500, 0, 0);
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
