package ise.mace.simulations;

import ise.mace.agents.TestPoliticalAgent;
import ise.mace.groups.SpecialGroup;
import ise.mace.groups.TestPoliticalGroup;
import ise.mace.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.mace.models.GroupDataInitialiser;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.plugins.CompassControl;
import ise.mace.plugins.HuntersAlivePlugin;
import ise.mace.plugins.DebugSwitchPlugin;
import ise.mace.plugins.GroupAgentInfo;
import ise.mace.plugins.GroupGraphs;
import ise.mace.plugins.GroupInfo;
import ise.mace.plugins.HunterInfo;
import ise.mace.plugins.HunterListPlugin;
import ise.mace.plugins.AgentPoliticalCompassPlugin;
import ise.mace.plugins.GroupPoliticalCompassPlugin;
import ise.mace.plugins.database.DatabasePlugin;
import ise.mace.tokens.AgentType;
import java.util.Random;

/**
 * <p>Basic Politics Simulation</p>
 *
 * <ul>
 * <li>Cycles: 500</li>
 * <li>Agents: 80 {@link PoliticalAgent}s
 *   <ul>
 *     <li>20 of each Strategy in {@link AgentType}</li>
 *     <li>Initial Food: 20</li>
 *     <li>Default Consumption: 2</li>
 *     <li>Beliefs: Randomised</li>
 *   </ul>
 * <li>Advice Consumption: 0.1</li>
 * <li>Free Group: {@link BasicFreeAgentGroup}</li>
 * <li>Groups:
 *   <ul>
 *     <li>{@link TestPoliticalGroup}</li>
 *     <li>{@link SpecialGroup}</li>
 *   </ul>
 * </li>
 * <li>Foods:
 *   <ul>
 *     <li>Rabbit: 2 from 1</li>
 *     <li>Stag: 5 from 2</li>
 *   </ul>
 * </li>
 * <li>Database: Primary Remote. +Politics</li>
 * <li>Default seed: 0</li>
 * </ul>
 */
/**
 *
 */
public class Politics extends GenericSimulation
{
	/**
	 * Creates a Politics simulation
	 * @see Politics
	 */
	public Politics()
	{
		super("Basic Politics Testing Bed", 500, 0, 0.1);
	}

	@Override
	protected void agents()
	{
		Random randomGenerator = new Random(randomSeed);


		for (int i = 0; i < 20; i++)
		{
			addAgent(new TestPoliticalAgent(20, 2, AgentType.AC,
							randomGenerator.nextDouble(),
							randomGenerator.nextDouble()));
			addAgent(new TestPoliticalAgent(20, 2, AgentType.TFT,
							randomGenerator.nextDouble(),
							randomGenerator.nextDouble()));
			addAgent(new TestPoliticalAgent(20, 2, AgentType.AD,
							randomGenerator.nextDouble(),
							randomGenerator.nextDouble()));
			addAgent(new TestPoliticalAgent(20, 2, AgentType.R,
							randomGenerator.nextDouble(),
							randomGenerator.nextDouble()));
		}

	}

	@Override
	protected void foods()
	{
		addFood("Rabbit", 2, 1);
		addFood("Stag", 5, 2);
	}

	@Override
	protected void groups()
	{
		addGroup(TestPoliticalGroup.class);
		addGroup(SpecialGroup.class);
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
		addPlugin(new HuntersAlivePlugin(getPath() + "/population.png", 1500, 1200));
		addPlugin(
						new DatabasePlugin(comment + ": Politics ONLY", true, false, false));
		addPlugin(new HunterListPlugin());
		addPlugin(new GroupPoliticalCompassPlugin());
		addPlugin(new AgentPoliticalCompassPlugin());
		addPlugin(new CompassControl());
		addPlugin(new HunterInfo());
		addPlugin(new GroupAgentInfo());
		addPlugin(new GroupInfo());
		addPlugin(new GroupGraphs());
	}

	@Override
	protected void events()
	{
	}
}
