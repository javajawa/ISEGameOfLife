package ise.gameoflife.simulations;

import ise.gameoflife.agents.TestPoliticalAgent;
import ise.gameoflife.groups.TestPoliticalGroup;
import ise.gameoflife.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.gameoflife.participants.AbstractFreeAgentGroup;
import ise.gameoflife.plugins.DebugSwitchPlugin;
import ise.gameoflife.plugins.database.DatabasePlugin;
import ise.gameoflife.tokens.AgentType;
import java.util.Random;

/**
 *
 * @author george
 */
public class CLIPolitics extends GenericSimulation
{
        
	public CLIPolitics()
	{
		super("Basic Politics Testing Bed", 500, 0, 0.1);
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
		addPlugin(new DebugSwitchPlugin());
		//addPlugin(new HuntersAlivePlugin(getPath() + "/population.png", 1500, 1200));
		//simulation comment and whether to store to remote db
		//if having errors, delete your Simulations.db file to recreate db
		addPlugin(new DatabasePlugin(comment,true));
		//addPlugin(new HunterListPlugin());
		//addPlugin(new PoliticalCompassPlugin());
    //            addPlugin(new PoliticalCompass2Plugin());
    //            addPlugin(new HunterInfo());
    //            addPlugin(new GroupInfo());
		addPlugin(new DatabasePlugin(comment, Boolean.FALSE));
	}

	@Override
	protected void events()
	{
	}

}
