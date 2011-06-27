package ise.gameoflife.simulations.validation;

import ise.gameoflife.agents.TestPoliticalAgent;
import ise.gameoflife.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.gameoflife.participants.AbstractFreeAgentGroup;
import ise.gameoflife.plugins.DebugSwitchPlugin;
import ise.gameoflife.plugins.HunterListPlugin;
import ise.gameoflife.plugins.HuntersAlivePlugin;
import ise.gameoflife.plugins.PoliticalCompassPlugin;
import ise.gameoflife.simulations.GenericSimulation;
import ise.gameoflife.tokens.AgentType;

/**
 *
 * @author Benedict
 */
public class BigRabbitDoubleAC extends GenericSimulation
{

	public BigRabbitDoubleAC()
	{
		super("Single agent starving to death", 500, 0, 0);
	}

	@Override
	protected void foods()
	{
		addFood("Rabbit", 5, 1);
		addFood("Stag", 2, 2);
	}

	@Override
	protected void agents()
	{
		 addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.5, 0.5));
                 addAgent(new TestPoliticalAgent(20, 2, AgentType.AC, 0.5, 0.5));
	}

	@Override
	protected void groups()
	{
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
		addPlugin(new HuntersAlivePlugin(getPath() + "/population.png", 300, 200));
		//addPlugin(new DatabasePlugin("Simulation comment",false));
		addPlugin(new HunterListPlugin());
		addPlugin(new PoliticalCompassPlugin());
	}

	@Override
	protected void events()
	{
	}

}
