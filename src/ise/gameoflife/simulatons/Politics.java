package ise.gameoflife.simulatons;

import ise.gameoflife.agents.TestPoliticalAgent;
import ise.gameoflife.groups.TestPoliticalGroup;
import ise.gameoflife.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.gameoflife.participants.AbstractFreeAgentGroup;
import ise.gameoflife.plugins.HuntersAlivePlugin;
import ise.gameoflife.plugins.database.DatabasePlugin;
import ise.gameoflife.plugins.DebugSwitchPlugin;
import ise.gameoflife.plugins.HunterListPlugin;
import ise.gameoflife.plugins.PoliticalCompass2Plugin;
import ise.gameoflife.plugins.PoliticalCompassPlugin;
import ise.gameoflife.tokens.AgentType;
import java.util.Random;

/**
 *
 * @author george
 */
public class Politics extends GenericSimulation
{

	public Politics()
	{

		super("Basic Politics Testing Bed", 3000, 0, 0.1);



	}

	@Override
	protected void agents()
	{


            addAgent(new TestPoliticalAgent(20, 2, AgentType.R, 0.5, 0.1));
            addAgent(new TestPoliticalAgent(20, 2, AgentType.R, 0.5, 0.2));
           // addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.4, 0.3));
            addAgent(new TestPoliticalAgent(20, 2, AgentType.R, 0.4, 0.4));

            addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.5, 0.5));
            addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.55, 0.45));
            addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.45, 0.55));
            addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.55, 0.55));
            addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.35, 0.55));
            addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.56, 0.55));
            addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.55, 0.58));
            addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.55, 0.7));
            addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.65, 0.58));
            addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.55, 0.42));

//                Random randomGenerator = new Random();
//		for (int i = 0; i < 10; i++)
//		{
//                        addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
//			addAgent(new TestPoliticalAgent(20, 2, AgentType.TFT, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
//			addAgent(new TestPoliticalAgent(20, 2, AgentType.AD, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
//			addAgent(new TestPoliticalAgent(20, 2, AgentType.R, randomGenerator.nextDouble(), randomGenerator.nextDouble()));

                Random randomGenerator = new Random();
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
		addFood("Rabbit", 2, 1);
		addFood("Stag", 5, 2);
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
		addPlugin(new HuntersAlivePlugin(getPath() + "/population.png", 1500, 1200));
		//simulation comment and whether to store to remote db
		//if having errors, delete your Simulations.db file to recreate db
		//addPlugin(new DatabasePlugin(comment,false));
		addPlugin(new HunterListPlugin());
		addPlugin(new PoliticalCompassPlugin());
                addPlugin(new PoliticalCompass2Plugin());

	}

	@Override
	protected void events()
	{
	}

}
