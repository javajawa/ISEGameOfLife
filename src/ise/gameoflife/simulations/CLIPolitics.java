package ise.gameoflife.simulations;

import ise.gameoflife.agents.TestPoliticalAgent;
import ise.gameoflife.groups.TestPoliticalGroup;
import ise.gameoflife.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.gameoflife.participants.AbstractFreeAgentGroup;
import ise.gameoflife.plugins.HuntersAlivePlugin;
import ise.gameoflife.plugins.database.DatabasePlugin;
import ise.gameoflife.tokens.AgentType;
import java.util.Random;

/**
 *
 * @author george
 */
public class CLIPolitics extends GenericSimulation
{
        
	public CLIPolitics(long rand)
	{
		super("Basic Politics Testing Bed", 500, rand, 0.1, Long.toHexString(rand));
	}

	@Override
	protected void agents()
	{
		Random randomGenerator = new Random(this.randomSeed);
		for (int i = 0; i < 10; i++)
		{
			addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
			addAgent(new TestPoliticalAgent(20, 2, AgentType.TFT, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
			addAgent(new TestPoliticalAgent(20, 2, AgentType.AD, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
			addAgent(new TestPoliticalAgent(20, 2, AgentType.R, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
		}
	}

	@Override
	protected void foods()
	{
		addFood("Rabbit", 5, 1);
		addFood("Stag", 20, 2);
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
		addPlugin(new HuntersAlivePlugin(getPath() + "/population.png", 1500, 1200));
		addPlugin(new DatabasePlugin(comment,false));
	}

	@Override
	protected void events()
	{
	}

}
