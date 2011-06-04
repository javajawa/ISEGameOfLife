package ise.gameoflife.agents;

import ise.gameoflife.actions.Proposal.ProposalType;
import ise.gameoflife.actions.Vote.VoteType;
import ise.gameoflife.inputs.Proposition;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.participants.AbstractAgent;
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

	/**
	 * Creates a new agent with the two primary properties
	 * @param initialFood The initial amount of food
	 * @param consumption The amount consumed per turn
	 */
	public TestAgent(double initialFood, double consumption)
	{
		super("<hunter>", 0, initialFood, consumption);
	}

	/**
	 * Uses the user's magically specified heuristics to determine which food type
	 * the player wishes to hunt for. 
	 * @return The food they choose to hunt
	 */
	@Override
	protected Food chooseFood()
	{
		Food bestSoFar = null;

		for (Food noms : getConn().availableFoods())
		{
			if (noms.getHuntersRequired() <= 1)
			{
				if (bestSoFar == null)
				{
					bestSoFar = noms;
				}
				else if (noms.getNutrition() > bestSoFar.getNutrition())
				{
					bestSoFar = noms;
				}
			}
		}
		return bestSoFar;
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
		return null;
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected void groupApplicationResponse(boolean accepted)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected ProposalType makeProposal()
	{
		// TODO: Implement
		return ProposalType.staySame;
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected VoteType castVote(Proposition p)
	{
		// TODO: Implement
		return VoteType.For;
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected Food giveAdvice(String agent, HuntingTeam agentsTeam)
	{
		return null;
	}

}
