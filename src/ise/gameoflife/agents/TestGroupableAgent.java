package ise.gameoflife.agents;

import ise.gameoflife.actions.Proposal.ProposalType;
import ise.gameoflife.actions.Vote.VoteType;
import ise.gameoflife.inputs.Proposition;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.participants.AbstractAgent;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.participants.AbstractGroupAgent;
import java.util.Map;
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

	@Override
	protected Food chooseFood()
	{
		// TODO: Update this code to work with team
		HuntingTeam team = this.getDataModel().getHuntingTeam();
		int teamSize = (team != null ? team.getMembers().size() : 1);
		Food bestSoFar = null;

		for (Food noms : getConn().availableFoods())
		{
			if (noms.getHuntersRequired() <= teamSize)
			{
				if (bestSoFar == null)
				{
					bestSoFar = noms;
				}
				else
				{
					if (noms.getNutrition() > bestSoFar.getNutrition())
					{
						bestSoFar = noms;
					}
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
		// Nothing to see here. Move along, citizen!
	}

	@Override
	protected String chooseGroup()
	{
		if (this.getDataModel().getGroupId() != null) return null;

		Set<String> groups = getConn().availableGroups();

		if (groups.isEmpty())
		{
			if (getConn().getAllowedGroupTypes().isEmpty()) return null;
			Class<? extends AbstractGroupAgent> gtype = getConn().getAllowedGroupTypes().get(0);
			return getConn().createGroup(gtype, new GroupDataInitialiser(this.uniformRandLong(), getDataModel().getEconomicBelief()));
		}

		return groups.iterator().next();
	}

	@Override
	protected void groupApplicationResponse(boolean accepted)
	{
		// TODO: Do something here?
		// throw new UnsupportedOperationException("Not supported yet.");
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
		// TODO Implement
		return null;
	}

	@Override
	protected double updateHappinessAfterHunt(double foodHunted,
					double foodReceived)
	{
		return 0; //throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected double updateLoyaltyAfterHunt(double foodHunted, double foodReceived)
	{
		return 0; //throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected Map<String, Double> updateTrustAfterHunt(double foodHunted,
					double foodReceived)
	{
		return null; //throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected double updateLoyaltyAfterVotes(Proposition proposition, int votes,
					double overallMovement)
	{
		return 0;
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected double updateHappinessAfterVotes(Proposition proposition, int votes,
					double overallMovement)
	{
		return 0;
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected Map<String, Double> updateTrustAfterVotes(Proposition proposition,
					int votes, double overallMovement)
	{
		return null;
		//throw new UnsupportedOperationException("Not supported yet.");
	}

}
