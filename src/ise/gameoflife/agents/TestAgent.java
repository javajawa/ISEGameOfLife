package ise.gameoflife.agents;

import ise.gameoflife.AbstractAgent;
import ise.gameoflife.models.Food;
import presage.EnvironmentConnector;

/**
 * Test agent of joy!
 * @author Benedict
 */
public class TestAgent extends AbstractAgent
{
	private static final long serialVersionUID = 1L;

	@Deprecated
	public TestAgent()
	{
		super();
	}

	/**
	 * Creates a new agent with the two primary properties
	 * @param initialFood The initial amount of food
	 * @param consumption The amount consumed per turn
	 */
	public TestAgent(double initialFood, double consumption)
	{
		super("<hunter>", 0, initialFood, consumption);
	}

	@Override
	protected Food chooseFood()
	{
		System.out.println("I, " + this.getId() + ", was asked to choose food");
		return null;
	}

	@Override
	protected void onInit(EnvironmentConnector ec)
	{
		// Nothing to see here. Move along, citizen!
	}

	@Override
	protected void onActivate()
	{
		// Nothing to see here. Move along, citizen!
	}
	
}
