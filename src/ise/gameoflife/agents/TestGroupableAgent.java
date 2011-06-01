package ise.gameoflife.agents;

import ise.gameoflife.participants.AbstractAgent;
import ise.gameoflife.models.Food;
import ise.gameoflife.participants.AbstractGroupAgent;
import java.util.Set;

/**
 * Test agent of joy!
 * @author Benedict
 */
public class TestGroupableAgent extends AbstractAgent
{
	private static final long serialVersionUID = 1L;

	@Deprecated
	public TestGroupableAgent()
	{
		super();
	}

	/**
	 * Creates a new agent with the two primary properties
	 * @param initialFood The initial amount of food
	 * @param consumption The amount consumed per turn
	 */
	public TestGroupableAgent(double initialFood, double consumption)
	{
		super("<hunter>", 0, initialFood, consumption);
	}

	/**
	 * Uses the user's magically specified heuristics to determine which food type
	 * the player wishes to hunt for. 
	 * @return 
	 */
	@Override
	protected Food chooseFood()
	{
		if (this.getDataModel().getOrder() != null) return this.getDataModel().getOrder();

		Food bestSoFar = null;

		for (Food noms : conn.availableFoods())
		{
			if (noms.getHuntersRequired() <= 1)
			{
				if (bestSoFar == null)
				{
					bestSoFar = noms;
				}
			}
				if (noms.getNutrition() > bestSoFar.getNutrition())
				{
					bestSoFar = noms;
				}
		}
		return bestSoFar;
	}

	@Override
	protected void onInit()
	{
		// Nothing to see here. Move along, citizen!
	}

	@Override
	protected void onActivate()
	{
		// Nothing to see here. Move along, citizen!
	}


	@Override
	protected void beforeNewRound()
	{
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected String chooseGroup()
	{
		Set<String> groups = conn.availableGroups();

		if (groups.isEmpty())
		{
			Class<? extends AbstractGroupAgent> gtype = conn.getAllowedGroupTypes().get(0);
			return conn.createGroup(gtype);
		}

		return groups.iterator().next();
	}

	@Override
	protected void groupApplicationResponse(boolean accepted)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
