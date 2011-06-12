package ise.gameoflife.agents;

import ise.gameoflife.actions.Proposal.ProposalType;
import ise.gameoflife.actions.Vote.VoteType;
import ise.gameoflife.inputs.Proposition;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.participants.AbstractAgent;
import ise.gameoflife.models.Food;
import java.util.Map;

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
		return ProposalType.staySame;
	}

	@Override
	protected VoteType castVote(Proposition p)
	{
		return VoteType.Abstain;
	}

	@Override
	protected Food giveAdvice(String agent, HuntingTeam agentsTeam)
	{
		return null;
	}

	@Override
	protected double updateHappinessAfterHunt(double foodHunted,
					double foodReceived)
	{
		return 0;
	}

	@Override
	protected double updateLoyaltyAfterHunt(double foodHunted, double foodReceived)
	{
		return 0;
	}

	@Override
	protected Map<String, Double> updateTrustAfterHunt(double foodHunted,
					double foodReceived)
	{
		return null;
	}

	@Override
	protected double updateLoyaltyAfterVotes(Proposition proposition, int votes,
					double overallMovement)
	{
		return 0;
	}

	@Override
	protected double updateHappinessAfterVotes(Proposition proposition, int votes,
					double overallMovement)
	{
		return 0;
	}

	@Override
	protected Map<String, Double> updateTrustAfterVotes(Proposition proposition,
					int votes, double overallMovement)
	{
		return null;
	}

	@Override
	protected void onInvite(String group)
	{
		// Nothing to see here. Move along, citizen!
	}

	@Override
	protected double updateSocialBeliefAfterVotes(Proposition proposition,
					int votes, double overallMovement)
	{
		return 0.5;
	}

	@Override
	protected double updateEconomicBeliefAfterVotes(Proposition proposition,
					int votes, double overallMovement)
	{
		return 0.5;
	}
        
}
