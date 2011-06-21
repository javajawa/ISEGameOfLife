package ise.gameoflife.agents;

import java.util.Map;

import ise.gameoflife.actions.Proposal.ProposalType;
import ise.gameoflife.actions.Vote.VoteType;
import ise.gameoflife.inputs.Proposition;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.participants.AbstractAgent;
import ise.gameoflife.simulations.evolution.SimulationGenome;

public class GeneticAgent extends AbstractAgent
{

	private static final long serialVersionUID = 1L;

	public GeneticAgent(SimulationGenome genome)
	{
		super("Genetic Agent", genome.randomSeed(),
				genome.initialFood(), genome.consumption(),
				genome.type(), genome.socialBelief(), genome.economicBelief());
	}

	@Override
	protected void onActivate()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void beforeNewRound()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String chooseGroup()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void groupApplicationResponse(boolean accepted)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected Food chooseFood()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ProposalType makeProposal()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected VoteType castVote(Proposition p)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Food giveAdvice(String agent, HuntingTeam agentsTeam)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected double updateHappinessAfterHunt(double foodHunted,
			double foodReceived)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected double updateLoyaltyAfterHunt(double foodHunted,
			double foodReceived)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected Map<String, Double> updateTrustAfterHunt(double foodHunted,
			double foodReceived)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Double> updateTrustAfterLeadersHunt()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected double updateLoyaltyAfterVotes(Proposition proposition,
			int votes, double overallMovement)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected double updateHappinessAfterVotes(Proposition proposition,
			int votes, double overallMovement)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected double updateSocialBeliefAfterVotes(Proposition proposition,
			int votes, double overallMovement)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected double updateEconomicBeliefAfterVotes(Proposition proposition,
			int votes, double overallMovement)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected Map<String, Double> updateTrustAfterVotes(
			Proposition proposition, int votes, double overallMovement)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onInvite(String group)
	{
		// TODO Auto-generated method stub

	}

}
