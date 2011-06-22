/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.simulations;

import ise.gameoflife.agents.WarAgent;
import ise.gameoflife.agents.WarAgent;
import ise.gameoflife.groups.WarGroup;
import ise.gameoflife.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.participants.AbstractFreeAgentGroup;
import ise.gameoflife.plugins.HuntersAlivePlugin;
import ise.gameoflife.plugins.DebugSwitchPlugin;
import ise.gameoflife.plugins.GroupGraphs;
import ise.gameoflife.plugins.GroupInfo;
import ise.gameoflife.plugins.HunterInfo;
import ise.gameoflife.plugins.HunterListPlugin;
import ise.gameoflife.plugins.PoliticalCompass2Plugin;
import ise.gameoflife.plugins.PoliticalCompassPlugin;
import ise.gameoflife.plugins.database.DatabasePlugin;
import ise.gameoflife.tokens.AgentType;
import java.util.Random;
/**
 *
 * @author The0s
 */
public class WarGames extends GenericSimulation
{

	public WarGames()
	{
		super("Basic Politics Testing Bed", 1000, 0, 0.1);
	}

	@Override
	protected void agents()
	{
                Random randomGenerator = new Random();

addAgent(new WarAgent(20, 2, AgentType.AC, 0.1, 0.1));
addAgent(new WarAgent(20, 2, AgentType.AC, 0.1, 0.12));
addAgent(new WarAgent(20, 2, AgentType.AC, 0.99, 0.99));
addAgent(new WarAgent(20, 2, AgentType.AC, 0.99, 0.97));
//
//		for (int i = 0; i < 5; i++)
//		{
//                        addAgent(new WarAgent(20, 2, AgentType.AC, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
//			addAgent(new WarAgent(20, 2, AgentType.TFT, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
//			addAgent(new WarAgent(20, 2, AgentType.AD, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
//			addAgent(new WarAgent(20, 2, AgentType.R, randomGenerator.nextDouble(), randomGenerator.nextDouble()));
//		}

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
		//simulation comment and whether to store to remote db
		//if having errors, delete your Simulations.db file to recreate db
		//addPlugin(new DatabasePlugin(comment,false));
		addPlugin(new HunterListPlugin());
		addPlugin(new PoliticalCompassPlugin());
                addPlugin(new PoliticalCompass2Plugin());
                addPlugin(new HunterInfo());
                addPlugin(new GroupInfo());
                addPlugin(new GroupGraphs());
	}

	@Override
	protected void events()
	{
	}

}

