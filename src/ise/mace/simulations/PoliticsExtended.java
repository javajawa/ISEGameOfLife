package ise.mace.simulations;

import ise.mace.agents.PoliticalAgent;
import ise.mace.groups.PoliticalGroup;
import ise.mace.groups.SpecialGroup;
import ise.mace.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.plugins.CompassControl;
import ise.mace.plugins.HuntersAlivePlugin;
import ise.mace.plugins.DebugSwitchPlugin;
import ise.mace.plugins.GroupAgentInfo;
import ise.mace.plugins.GroupGraphs;
import ise.mace.plugins.GroupInfo;
import ise.mace.plugins.HunterInfo;
import ise.mace.plugins.HunterListPlugin;
import ise.mace.plugins.LoansInfoPolitics;
import ise.mace.plugins.PoliticalCompass2Plugin;
import ise.mace.plugins.PoliticalCompassPlugin;
import ise.mace.plugins.database.DatabasePlugin;
import ise.mace.tokens.AgentType;
import java.util.Random;

/**
 * <p>Extended Politics Simulation</p>
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
 *     <li>{@link PoliticalGroup}</li>
 *     <li>{@link SpecialGroup}</li>
 *   </ul>
 * </li>
 * <li>Foods:
 *   <ul>
 *     <li>Rabbit: 2 from 1</li>
 *     <li>Stag: 5 from 2</li>
 *   </ul>
 * </li>
 * <li>Database: Primary Remote. +Politics, +Loans</li>
 * </ul>
 */
public class PoliticsExtended extends GenericSimulation
{
	/**
	 * Creates a PoliticsExtended simulation
	 * @see PoliticsExtended
	 */
	public PoliticsExtended()
	{
		super("Politics with Loans Testing Bed", 500, 0, 0.1);
	}

	@Override
	protected void agents()
	{
		Random randomGenerator = new Random(randomSeed);

		for (int i = 0; i < 20; i++)
		{
			addAgent(new PoliticalAgent(20, 2, AgentType.AC,
							randomGenerator.nextDouble(),
							randomGenerator.nextDouble()));
			addAgent(new PoliticalAgent(20, 2, AgentType.TFT,
							randomGenerator.nextDouble(),
							randomGenerator.nextDouble()));
			addAgent(new PoliticalAgent(20, 2, AgentType.AD,
							randomGenerator.nextDouble(),
							randomGenerator.nextDouble()));
			addAgent(new PoliticalAgent(20, 2, AgentType.R,
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
		addGroup(PoliticalGroup.class);
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
		addPlugin(new DatabasePlugin(comment + ": Loans, Politics", true, false,
						true));
		addPlugin(new HunterListPlugin());
		addPlugin(new PoliticalCompassPlugin());
		addPlugin(new PoliticalCompass2Plugin());
		addPlugin(new CompassControl());
		addPlugin(new HunterInfo());
		addPlugin(new GroupAgentInfo());
		addPlugin(new GroupInfo());
		addPlugin(new GroupGraphs());
		addPlugin(new LoansInfoPolitics());
	}

	@Override
	protected void events()
	{
	}
}
