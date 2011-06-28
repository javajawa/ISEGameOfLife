package ise.mace.simulations;

import ise.mace.agents.LoansAgent;
import ise.mace.groups.LoansGroup;
import ise.mace.groups.SpecialGroup;
import ise.mace.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.plugins.DebugSwitchPlugin;
import ise.mace.plugins.GroupInfo;
import ise.mace.plugins.LoansInfo;
import ise.mace.plugins.AgentPoliticalCompassPlugin;
import ise.mace.plugins.GroupPoliticalCompassPlugin;
import ise.mace.plugins.database.DatabasePlugin;
import ise.mace.tokens.AgentType;
import java.util.Random;

/**
 * <p>Loans Simulation</p>
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
 *     <li>{@link LoansGroup}</li>
 *     <li>{@link SpecialGroup}</li>
 *   </ul>
 * </li>
 * <li>Foods:
 *   <ul>
 *     <li>Rabbit: 2 from 1</li>
 *     <li>Stag: 5 from 2</li>
 *   </ul>
 * </li>
 * <li>Database: Primary Remote. +Loans</li>
 * <li>Default seed: 0</li>
 * </ul>
 */
/**
 *
 */
public class Loans extends GenericSimulation
{
	/**
	 * Creates a Loans simulation
	 * @see Loans
	 */
	public Loans()
	{
		super("Basic Loans Testing Bed", 500, 0, 0.1);
	}

	@Override
	protected void agents()
	{
		Random randomGenerator = new Random(randomSeed);

		for (int i = 0; i < 20; i++)
		{
			addAgent(new LoansAgent(20, 2, AgentType.AC,
							randomGenerator.nextDouble(),
							randomGenerator.nextDouble()));
			addAgent(new LoansAgent(20, 2, AgentType.TFT,
							randomGenerator.nextDouble(),
							randomGenerator.nextDouble()));
			addAgent(new LoansAgent(20, 2, AgentType.AD,
							randomGenerator.nextDouble(),
							randomGenerator.nextDouble()));
			addAgent(new LoansAgent(20, 2, AgentType.R,
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
		addGroup(LoansGroup.class);
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
		//addPlugin(new HuntersAlivePlugin(getPath() + "/population.png", 1500, 1200));
		//local db with loans:
		//addPlugin(new DatabasePlugin(comment,false,false,true));
		//doc db with loans:
		//addPlugin(new DatabasePlugin(comment,true,true,true));
		//robs remote db with loans
		//addPlugin(new DatabasePlugin(comment,true,false,true));

		//Upload Loans WITHOUT Politics
		addPlugin(new DatabasePlugin("Loans - this is Loans WITHOUT Politcs", true,
						false, true));

		//addPlugin(new HunterListPlugin());
		addPlugin(new GroupPoliticalCompassPlugin());
		addPlugin(new AgentPoliticalCompassPlugin());
		addPlugin(new LoansInfo());
		addPlugin(new GroupInfo());

	}

	@Override
	protected void events()
	{
	}
}
