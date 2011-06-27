package ise.gameoflife.simulations;
import ise.gameoflife.agents.LoansAgent;
import ise.gameoflife.groups.LoansGroup;
import ise.gameoflife.groups.SpecialGroup;
import ise.gameoflife.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.gameoflife.participants.AbstractFreeAgentGroup;
import ise.gameoflife.plugins.HuntersAlivePlugin;
import ise.gameoflife.plugins.DebugSwitchPlugin;
import ise.gameoflife.plugins.GroupGraphs;
import ise.gameoflife.plugins.GroupInfo;
import ise.gameoflife.plugins.HunterInfo;
import ise.gameoflife.plugins.HunterListPlugin;
import ise.gameoflife.plugins.LoansInfo;
import ise.gameoflife.plugins.PoliticalCompass2Plugin;
import ise.gameoflife.plugins.PoliticalCompassPlugin;
import ise.gameoflife.plugins.database.DatabasePlugin;
import ise.gameoflife.tokens.AgentType;
import java.util.Random;

/**
 *
 * @author george
 */
public class Loans extends GenericSimulation
{

	public Loans()
	{
		super("Basic Loans Testing Bed", 500, 0, 0.1);
	}

	@Override
	protected void agents()
	{
                Random randomGenerator = new Random();

		for (int i = 0; i < 20; i++)
		{
                        addAgent(new LoansAgent(20, 2, AgentType.AC, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
			addAgent(new LoansAgent(20, 2, AgentType.TFT, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
			addAgent(new LoansAgent(20, 2, AgentType.AD, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
			addAgent(new LoansAgent(20, 2, AgentType.R, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
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
		addPlugin(new DatabasePlugin("Loans - this is Loans WITHOUT Politcs",true,false,true));                 
                
		//addPlugin(new HunterListPlugin());
		addPlugin(new PoliticalCompassPlugin());
                addPlugin(new PoliticalCompass2Plugin());
                addPlugin(new LoansInfo());
                addPlugin(new GroupInfo());

	}

	@Override
	protected void events()
	{
	}

}
