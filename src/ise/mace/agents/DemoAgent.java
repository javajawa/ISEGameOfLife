package ise.mace.agents;

import ise.mace.actions.Proposal.ProposalType;
import ise.mace.actions.Vote.VoteType;
import ise.mace.inputs.Proposition;
import ise.mace.models.Food;
import ise.mace.models.HuntingTeam;
import ise.mace.participants.AbstractAgent;
import java.util.Map;

/**
 *
 */
public class DemoAgent extends AbstractAgent
{
	private static final long serialVersionUID = 1L;

	@Deprecated
	public DemoAgent()
	{
		super();
		// This function is, surprisingly, important
	}

	public DemoAgent(double initialFood, double consumption)
	{
		// MORE VARIABLES! MORE!
		super("<hunter>", 0, initialFood, consumption);
	}

	@Override
	protected void onActivate()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected void beforeNewRound()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected String chooseGroup()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected void groupApplicationResponse(boolean accepted)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected Food chooseFood()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected ProposalType makeProposal()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected VoteType castVote(Proposition p)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected Food giveAdvice(String agent, HuntingTeam agentsTeam)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected double updateHappinessAfterHunt(double foodHunted,
					double foodReceived)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected double updateLoyaltyAfterHunt(double foodHunted, double foodReceived)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected Map<String, Double> updateTrustAfterHunt(double foodHunted,
					double foodReceived)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected double updateLoyaltyAfterVotes(Proposition proposition, int votes,
					double overallMovement)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected double updateHappinessAfterVotes(Proposition proposition, int votes,
					double overallMovement)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected Map<String, Double> updateTrustAfterVotes(Proposition proposition,
					int votes, double overallMovement)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected void onInvite(String group)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected double updateSocialBeliefAfterVotes(Proposition proposition,
					int votes, double overallMovement)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected double updateEconomicBeliefAfterVotes(Proposition proposition,
					int votes, double overallMovement)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected Map<String, Double> updateTrustAfterLeadersHunt()
	{
		return null;
		//throw new UnsupportedOperationException("Not supported yet.");
	}
}
