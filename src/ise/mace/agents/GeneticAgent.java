package ise.mace.agents;

import java.util.List;

import ise.mace.simulations.evolution.SimulationGenome;
import ise.mace.neuralnetworks.TanhFeedForwardNetwork;
import ise.mace.neuralnetworks.NetworkGenome;
import ise.mace.participants.PublicAgentDataModel;
import ise.mace.models.Food;
import ise.mace.models.History;

public class GeneticAgent extends TestPoliticalAgent
{
	private static final long serialVersionUID = 1L;

	private TanhFeedForwardNetwork chooseFoodNet;

	public GeneticAgent(SimulationGenome genome)
	{
		super(genome.initialFood(), genome.consumption(),
				genome.type(), genome.socialBelief(), genome.economicBelief());

		NetworkGenome cfGenome = genome.chooseFoodGenome();
		chooseFoodNet = new TanhFeedForwardNetwork(cfGenome.nodeCounts());
		chooseFoodNet.setOffsets(cfGenome.offsets());
		chooseFoodNet.setWeights(cfGenome.weights());
	}

	@Override
	protected Food chooseFood()
	{
		if (null == getDataModel().getHuntingTeam())
		{
			return null;
		}

		List<String> members = this.getDataModel().getHuntingTeam().getMembers();
		int teamSize = members.size();
		PublicAgentDataModel player = this.getDataModel();
		PublicAgentDataModel opponent = null;
		Food stag = this.getFoodTypes().get(0);
		Food hare = this.getFoodTypes().get(1);

		if (teamSize > 1)
		{
			int oppIdx = (members.get(0).equals(this.getId())) ? 1 : 0;
			opponent = getConn().getAgentById(members.get(oppIdx));
		}

		boolean hasOpponent = (null != opponent);
		History<Food> opponentHuntingHistory = null;
		boolean opponentLastHuntStag = false;
		double opponentStagStrategy = 0;
		if (hasOpponent)
		{
			opponentHuntingHistory = opponent.getHuntingHistory();

			if (null != opponentHuntingHistory &&
				opponentHuntingHistory.size() > 1)
			{
				Food opponentLastHunt = opponentHuntingHistory.getValue(1);
				opponentLastHuntStag = stag.equals(opponentLastHunt);

				for (int i = 0; i < opponentHuntingHistory.size(); i++)
				{
					Food opponentFood = opponentHuntingHistory.getValue(i);
					if (stag.equals(opponentFood))
					{
						opponentStagStrategy++;
					}
				}
				opponentStagStrategy /= (double)opponentHuntingHistory.size();
			}
		}

		String groupId = player.getGroupId();
		boolean hasGroup = false;
		if (null != groupId &&
			getConn().getGroupById(groupId).getMemberList().size() > 1)
		{
			hasGroup = true;
		}
		boolean suggestedStag = false;
		if (hasGroup)
		{
			String mostTrusted = null;
			double maxTrust = 0;
			for (String member : members)
			{
				Double trust = player.getTrust(member);
				if (null == trust) continue;
				double curTrust = trust.doubleValue();
				if (curTrust < maxTrust) continue;
				maxTrust = trust;
				mostTrusted = member;
			}
			if (null != mostTrusted)
			{
				Food suggested = this.seekAvice(mostTrusted);
				suggestedStag = stag.equals(suggested);
			}
		}

		double happiness = .5;
		double loyalty = .5;
		if (null != player.getCurrentHappiness())
			happiness = player.getCurrentHappiness().doubleValue();
		if (null != player.getCurrentLoyalty())
			loyalty = player.getCurrentLoyalty().doubleValue();
		double foodVal = player.getFoodAmount();

		double inVals[] = new double[]
		{
			opponentLastHuntStag ? 1 : -1,
			2 * opponentStagStrategy - 1,
			hasGroup ? 1 : -1,
			suggestedStag ? 1 : -1,
			2 * happiness - 1,
			2 * foodVal - 1,
			2 * loyalty - 1
		};

		double outVal[] = chooseFoodNet.out(inVals);
		return (outVal[0] > outVal[1]) ? stag : hare;
	}

}
