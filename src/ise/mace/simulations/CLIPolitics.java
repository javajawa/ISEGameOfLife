package ise.mace.simulations;

import ise.mace.agents.TestPoliticalAgent;
import ise.mace.groups.TestPoliticalGroup;
import ise.mace.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.plugins.HuntersAlivePlugin;
import ise.mace.plugins.database.DatabasePlugin;
import ise.mace.tokens.AgentType;
import java.util.Random;

/**
 * <p>CLIPolitics Simulation</p>
 *
 * <ul>
 * <li>Cycles: 5000</li>
 * <li>Agents: 200 {@link TestPoliticalAgent}s
 *   <ul>
 *     <li>50 of each Strategy in {@link AgentType}</li>
 *     <li>Initial Food: 20</li>
 *     <li>Default Consumption: 2</li>
 *     <li>Beliefs: Randomised</li>
 *   </ul>
 * <li>Advice Consumption: 0.1</li>
 * <li>Free Group: {@link BasicFreeAgentGroup}</li>
 * <li>Groups:
 *   <ul>
 *     <li>{@link TestPoliticalGroup}</li>
 *   </ul>
 * </li>
 * <li>Foods:
 *   <ul>
 *     <li>Rabbit: 2 from 1</li>
 *     <li>Stag: 10 from 2</li>
 *   </ul>
 * </li>
 * <li>Database: Primary Remote, Corvette</li>
 * <li>Default seed: random (passed as parameter)</li>
 * </ul>
 */

/**
 *
 */
public class CLIPolitics extends GenericSimulation
{
	/**
	 * Creates a CLIPolitics simulation
	 * @see CLIPolitics
	 * @param rand Random Seed
	 */
	public CLIPolitics(long rand)
	{
		super("Command Line Basic Politics Testing Bed " + rand, 5000, rand, 0.1,
						Long.toHexString(rand));
	}

	@Override
	protected void agents()
	{
		Random randomGenerator = new Random(this.randomSeed);
		for (int i = 0; i < 50; i++)
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
		addFood("Stag", 10, 2);
	}

	@Override
	protected void groups()
	{
		addGroup(TestPoliticalGroup.class);
	}

	@Override
	protected Class<? extends AbstractFreeAgentGroup> chooseFreeAgentHandler()
	{
		return BasicFreeAgentGroup.class;
	}

	@Override
	protected void plugins()
	{
		addPlugin(new HuntersAlivePlugin(getPath() + "/population" + Long.toHexString(
						randomSeed) + ".png", 1500, 1200));
		addPlugin(new DatabasePlugin(comment, true, true));
	}

	@Override
	protected void events()
	{
	}
}
