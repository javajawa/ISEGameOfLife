package ise.gameoflife.agents;

import ise.gameoflife.AbstractAgent;
import ise.gameoflife.models.Food;

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

	@Override
	protected void init()
	{
		// Nothing to see here
	}

	@Override
	protected Food chooseFood()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
