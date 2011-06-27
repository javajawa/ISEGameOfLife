package ise.mace.simulations;

import ise.mace.agents.WarAgent;
import ise.mace.groups.WarGroup;
import ise.mace.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.plugins.HuntersAlivePlugin;
import ise.mace.plugins.DebugSwitchPlugin;
import ise.mace.plugins.GroupAgentInfo;
import ise.mace.plugins.GroupGraphs;
import ise.mace.plugins.GroupInfo;
import ise.mace.plugins.HunterInfo;
import ise.mace.plugins.HunterListPlugin;
import ise.mace.plugins.PoliticalCompass2Plugin;
import ise.mace.plugins.PoliticalCompassPlugin;
import ise.mace.tokens.AgentType;
import java.util.Random;

/**
 * <p>War Games Simulation</p>
 *
 * <ul>
 * <li>Cycles: 500</li>
 * <li>Agents: 80 {@link WarAgent}s
 *   <ul>
 *     <li>Strategy: {@link AgentType#AC Always Co-operate}</li>
 *     <li>Initial Food: 20</li>
 *     <li>Default Consumption: 2</li>
 *     <li>Beliefs: 20 * 4 Types
 *       <ul>
 *         <li>Communist:  (0.1,0.1) to (0.2, 0.2)</li>
 *         <li>Fascist:    (0.9,0.1) to (1.0, 0.2)</li>
 *         <li>Democrats:  (0.5,0.5) to (0.6, 0.6)</li>
 *         <li>Anarchists: (0.5,0.9) to (0.6, 1.0)</li>
 *       </ul>
 *   </ul>
 * <li>Advice Consumption: 0.1</li>
 * <li>Free Group: {@link BasicFreeAgentGroup}</li>
 * <li>Groups:
 *   <ul>
 *     <li>{@link WarGroup}</li>
 *   </ul>
 * </li>
 * <li>Foods:
 *   <ul>
 *     <li>Rabbit: 2 from 1</li>
 *     <li>Stag: 5 from 2</li>
 *   </ul>
 * </li>
 * <li>Database: None</li>
 * </ul>
 */
public class WarGames extends GenericSimulation
{
	/**
	 * Creates a new War Games Simulation
	 * @see WarGames
	 */
	public WarGames()
	{
		super("War Games Simulation", 10000, 0, 0.1);
	}

	@Override
	protected void agents()
	{
		Random rand = new Random(randomSeed);


		for (int i = 0; i < 10; i++)
		{
			//Communist group
			addAgent(new WarAgent(20, 2, AgentType.AC,
							0.1 + rand.nextDouble() / 10,
							0.1 + rand.nextDouble() / 10)
							);
			//Fascist group
			addAgent(new WarAgent(20, 2, AgentType.AC,
							0.1 + rand.nextDouble() / 10,
							0.9 + rand.nextDouble() / 10)
							);
			//Democrats
			addAgent(new WarAgent(20, 2, AgentType.AC,
							0.5 + rand.nextDouble() / 10,
							0.5 + rand.nextDouble() / 10)
							);
			//Anarchists
			addAgent(new WarAgent(20, 2, AgentType.AC,
							0.9 + rand.nextDouble() / 10,
							0.5 + rand.nextDouble() / 10)
							);
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
		addGroup(WarGroup.class);
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
		addPlugin(new HunterListPlugin());
		addPlugin(new PoliticalCompassPlugin());
		addPlugin(new PoliticalCompass2Plugin());
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
