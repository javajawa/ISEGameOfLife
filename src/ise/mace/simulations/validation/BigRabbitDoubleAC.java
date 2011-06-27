package ise.mace.simulations.validation;

import ise.mace.agents.TestPoliticalAgent;
import ise.mace.groups.freeagentgroups.BasicFreeAgentGroup;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.plugins.DebugSwitchPlugin;
import ise.mace.plugins.HunterListPlugin;
import ise.mace.plugins.HuntersAlivePlugin;
import ise.mace.plugins.PoliticalCompassPlugin;
import ise.mace.simulations.GenericSimulation;
import ise.mace.tokens.AgentType;

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
