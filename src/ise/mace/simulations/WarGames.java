/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.mace.simulations;

import ise.mace.agents.WarAgent;
import ise.mace.agents.WarAgent;
import ise.mace.groups.WarGroup;
import ise.mace.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.mace.models.GroupDataInitialiser;
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
import ise.mace.plugins.database.DatabasePlugin;
import ise.mace.tokens.AgentType;
import java.util.Random;
/**
 *
 */
public class WarGames extends GenericSimulation
{

	public WarGames()
	{
		super("Basic Politics Testing Bed", 10000, 0, 0.1);
	}

	@Override
	protected void agents()
	{
                Random rand = new Random();

                //Communist group
		for (int i = 0; i < 10; i++)
		{
                        addAgent(new WarAgent(20, 2, AgentType.AC, 0.1+rand.nextDouble()/10, 0.1+rand.nextDouble()/10));
                        //addAgent(new WarAgent(20, 2, AgentType.AD, 0.1+rand.nextDouble()/10, 0.1+rand.nextDouble()/10));
                        //addAgent(new WarAgent(20, 2, AgentType.R, 0.1+rand.nextDouble()/10, 0.1+rand.nextDouble()/10));
                        //addAgent(new WarAgent(20, 2, AgentType.TFT, 0.1+rand.nextDouble()/10, 0.1+rand.nextDouble()/10));
		}

                //Fascist group
                for (int i = 0; i < 10; i++)
		{
                        addAgent(new WarAgent(20, 2, AgentType.AC, 0.1+rand.nextDouble()/10, 0.9+rand.nextDouble()/10));
                        //addAgent(new WarAgent(20, 2, AgentType.AD, 0.1+rand.nextDouble()/10, 0.9+rand.nextDouble()/10));
                        //addAgent(new WarAgent(20, 2, AgentType.R, 0.1+rand.nextDouble()/10, 0.9+rand.nextDouble()/10));
                        //addAgent(new WarAgent(20, 2, AgentType.TFT, 0.1+rand.nextDouble()/10, 0.9+rand.nextDouble()/10));
		}
                //Democrats
                                for (int i = 0; i < 10; i++)
		{
                        addAgent(new WarAgent(20, 2, AgentType.AC, 0.5+rand.nextDouble()/10, 0.5+rand.nextDouble()/10));
                        //addAgent(new WarAgent(20, 2, AgentType.AD, 0.5+rand.nextDouble()/10, 0.5+rand.nextDouble()/10));
                        //addAgent(new WarAgent(20, 2, AgentType.R, 0.5+rand.nextDouble()/10, 0.5+rand.nextDouble()/10));
                        //addAgent(new WarAgent(20, 2, AgentType.TFT, 0.5+rand.nextDouble()/10, 0.5+rand.nextDouble()/10));
		}
                //Anarchists
                for (int i = 0; i < 10; i++)
		{
                        addAgent(new WarAgent(20, 2, AgentType.AC, 0.9+rand.nextDouble()/10, 0.5    +rand.nextDouble()/10));
                        //addAgent(new WarAgent(20, 2, AgentType.AD, 0.9+rand.nextDouble()/10, 0.5+rand.nextDouble()/10));
                        //addAgent(new WarAgent(20, 2, AgentType.R, 0.9+rand.nextDouble()/10, 0.5+rand.nextDouble()/10));
                        //addAgent(new WarAgent(20, 2, AgentType.TFT, 0.9+rand.nextDouble()/10, 0.5+rand.nextDouble()/10));
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
		//simulation comment and whether to store to remote db
		//if having errors, delete your Simulations.db file to recreate db
		//addPlugin(new DatabasePlugin(comment,false));
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

