package ise.gameoflife.agents;

import java.util.List;

import ise.gameoflife.simulations.evolution.SimulationGenome;
import ise.gameoflife.neuralnetworks.TanhFeedForwardNetwork;
import ise.gameoflife.neuralnetworks.NetworkGenome;
import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.History;

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

			if (null != opponentHuntingHistory)
			{
				Food opponentLastHunt = opponentHuntingHistory.getValue(1);
				opponentLastHuntStag = stag.equals(opponentLastHunt);

				for (int i = 0; i < opponentHuntingHistory.size(); i++)
				{
					Food opponentFood = opponentHuntingHistory.getValue(i);
					if (opponentFood.equals(stag))
					{
						opponentStagStrategy++;
					}
				}
				opponentStagStrategy /= opponentHuntingHistory.size();
			}
		}

		String groupId = player.getGroupId();
		boolean hasGroup = (null != groupId);
		hasGroup &= getConn().getGroupById(groupId).getMemberList().size() > 1;
		boolean suggestedStag = false;
		if (hasGroup)
		{
			String mostTrusted = null;
			double maxTrust = 0;
			for (String member : members)
			{
				double trust = player.getTrust(member);
				if (trust < maxTrust) continue;
				maxTrust = trust;
				mostTrusted = member;
			}
			Food suggested = this.seekAvice(mostTrusted);
			suggestedStag = stag.equals(suggested);
		}

		double happiness = player.getCurrentHappiness();
		double foodVal = player.getFoodAmount();
		double loyalty = player.getCurrentLoyalty();

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
