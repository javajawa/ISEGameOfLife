package ise.mace.groups;

import ise.mace.agents.PoliticalAgentGroup;
import ise.mace.environment.PublicEnvironmentConnection;
import ise.mace.inputs.LeaveNotification.Reasons;
import ise.mace.models.GroupDataInitialiser;
import ise.mace.models.HuntingTeam;
import ise.mace.models.Tuple;
import ise.mace.participants.AbstractGroupAgent;
import ise.mace.tokens.AgentType;
import ise.mace.tokens.InteractionResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class TestPoliticalGroup extends AbstractGroupAgent
{
	private static final long serialVersionUID = 1L;

	@Deprecated
	public TestPoliticalGroup()
	{
		super();
	}

	public TestPoliticalGroup(GroupDataInitialiser dm)
	{
		super(dm);
	}

	@Override
	protected void onActivate()
	{
		this.setGroupStrategy(AgentType.R); //ADDED The0

	}

	@Override
	protected boolean respondToJoinRequest(String playerID)
	{


		if (getDataModel().getMemberList().isEmpty())
		{
			//if empty then 'playerID' created the group so there is no need to compute a heuristic
			return true;
		}
		else if (getDataModel().getMemberList().size() == 1)
		{
			//if there is only one member then 'playerID' was invited and wants to accept the invitation
			//so there is no need to compute a heuristic
			return true;
		}
		else if (getConn().getAgentById(playerID) == null) //ADDED THE0
		{
			//agent does not exist so invitation is denied
			return false;
		}
		else if (this.getId().equals(PoliticalAgentGroup.special))
		{ //exception for the Special group
			return true;
		}
		else
		{
			double maxDistance = Math.sqrt(2);
			int numKnownTrustValues = 0;
			double trustSum = 0;

			//Retieve the trust value between requester and the group
			for (String trustor : this.getDataModel().getMemberList())
			{
				if (getConn().getAgentById(trustor).getTrust(playerID) != null)
				{
					trustSum += getConn().getAgentById(trustor).getTrust(playerID);
					numKnownTrustValues++;
				}
			}

			//Calculate the vector distance between these two agents socio-economic beliefs
			double economic = getConn().getAgentById(playerID).getEconomicBelief() - getDataModel().getCurrentEconomicPoisition();//change in X
			double social = getConn().getAgentById(playerID).getSocialBelief() - getDataModel().getEstimatedSocialLocation();//change in Y
			double vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social,
							2));

			//The longer the distance the lower the esFaction is. Therefore, agents close to group's beliefs have
			//higher probability of joining this group
			double esFaction = 1 - (vectorDistance / maxDistance);

			double heuristicValue;
			if (numKnownTrustValues != 0)
			{
				//The actual heuristic value is calculated. The politics is more important for compatibility than
				//trust when a group decides on the request, but trust does contribute signicantly
				heuristicValue = 0.4 * (trustSum / numKnownTrustValues) + 0.6 * esFaction;
			}
			else
			{
				heuristicValue = 0.6 * esFaction;
			}

			if (heuristicValue > 0.5)
			{
				//agent can join, so update economic beliefs
				double size = this.getDataModel().getMemberList().size();
				economic = 0;
				for (String members : getDataModel().getMemberList())
				{
					if (getConn().getAgentById(members) != null) //GIVES PROBLEMS
						economic += getConn().getAgentById(members).getEconomicBelief();
				}
				economic += getConn().getAgentById(playerID).getEconomicBelief();
				economic = economic / (size + 1);
				setEconomicPosition(economic);
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	private Comparator<String> c = new Comparator<String>()
	{
		private Random r = new Random(0);

		@Override
		public int compare(String o1, String o2)
		{
			return (r.nextBoolean() ? -1 : 1);
		}
	};

	@Override
	public List<HuntingTeam> selectTeams()
	{
		ArrayList<HuntingTeam> teams = new ArrayList<HuntingTeam>();
		List<String> members = new ArrayList<String>(getDataModel().getMemberList());
		Collections.sort(members, c);


		int agents = members.size();
		for (int i = 0; i < agents; i += 2)
		{
			int ubound = (i + 2 >= agents) ? agents : i + 2;
			teams.add(new HuntingTeam(members.subList(i, ubound)));
		}

		return teams;
	}

	@Override
	protected void onMemberLeave(String playerID, Reasons reason)
	{
		//update economic belief of the group when the agent leaves the group
		double size = this.getDataModel().getMemberList().size();
		double economic = 0;
		for (String members : this.getDataModel().getMemberList())
		{
			economic += getConn().getAgentById(members).getEconomicBelief();
		}
		economic = economic / (size);
		this.setEconomicPosition(economic);
	}

	@Override
	protected void beforeNewRound()
	{
		if (getDataModel().getMemberList().size() != 1)
		{
			List<String> newPanel = updatePanel();
			this.setPanel(newPanel);
		}
		this.setGroupStrategy(decideGroupStrategy());
	}

	/**
	 * This method updates the panel for this group. The panel is the set of leaders in this group
	 * The size of the panel depends on the social position of the group. If it is at the very top
	 * it has a single leader (dictator). If it is at the bottom then every member belongs to the panel (anarchism).
	 * @param none
	 * @return The new panel members.
	 */
	private List<String> updatePanel()
	{

		double groupSocialPosition;
		int population, panelSize;

		//STEP 1:Find the size of the panel. It is the proportion of the total population that
		// can be in the panel. It is calculated using the social position of the group.
		population = getDataModel().getMemberList().size();
		groupSocialPosition = getDataModel().getEstimatedSocialLocation();

		//Round to the closest integer
		panelSize = (int)Math.round(population * groupSocialPosition);
		if (panelSize == 0) //The group is on the very top of the axis. Dictatorship
		{
			//Force panelSize to be at least one (dictator)
			panelSize = 1;
		}
		//STEP 1 END

		//STEP 2: Get the average trust of each agent in the group
		List< Tuple<String, Double>> panelCandidates = new LinkedList< Tuple<String, Double>>();

		List<String> groupMembers = getDataModel().getMemberList();

		for (String candidate : groupMembers)
		{
			double sum = 0;
			int numKnownTrustValues = 0;
			for (String member : groupMembers)
			{
				if ((getConn().getAgentById(member).getTrust(candidate) != null) && (!member.equals(
								candidate)))
				{
					sum += getConn().getAgentById(member).getTrust(candidate);
					numKnownTrustValues++;
				}
			}

			Tuple<String, Double> tuple;
			if (numKnownTrustValues != 0)
			{
				tuple = new Tuple<String, Double>(candidate, sum / numKnownTrustValues);
				panelCandidates.add(tuple);
			}
		}
		//STEP 2 END

		//STEP 3: Sort the agents in descending order of trust values
		Collections.sort(panelCandidates, d);
		//STEP 3 END

		//STEP 4: Populate the panel list with the most trusted agents in the group (i.e. the leaders)
		//Note that eventhough an agent is a candidate its trust must be above a threshold to become member of the panel.
		//The threshold is the social position. If the group is highly authoritarian then anyone with a trust value
		//above zero can become a leader. In libertarian groups panel formations are rare since a relatively high trust value
		//must be achieved! Also the threshold acts as a warning for current panel members. If their trust falls
		//below this threshold due to bad decisions they will be ousted in the next round.
		List<String> newPanel = new LinkedList<String>();
		if (!panelCandidates.isEmpty() && (panelCandidates.size() >= panelSize))//Panel is not empty and we have enough candidates to select leaders
		{
			for (int i = 0; i < panelSize; i++)
			{
				if (panelCandidates.get(i).getValue() >= groupSocialPosition)
				{
					newPanel.add(panelCandidates.get(i).getKey());
				}
			}
		}
		//STEP 4 END

		return newPanel;
	}
	private Comparator< Tuple<String, Double>> d = new Comparator< Tuple<String, Double>>()
	{
		@Override
		public int compare(Tuple<String, Double> o1, Tuple<String, Double> o2)
		{
			Double v1 = o1.getValue();
			Double v2 = o2.getValue();
			return (v1 > v2 ? -1 : 1);
		}
	};

	/**
	 * A political group can play the stag hunt game with another group. The panel must make a decision regarding
	 * their strategy. Important considerations for the decision is the social belief and the preferred strategy of
	 * the group and the panel.
	 * @param none
	 * @return The group's chosen strategy
	 */
	@Override
	protected AgentType decideGroupStrategy()
	{
		//Check if this group has leader/leaders. If leaders have not emerge yet then no decision at all
		List<String> currentPanel = getDataModel().getPanel();
		int population = getDataModel().getMemberList().size();

		if (currentPanel.isEmpty() || (population == 1)) return null;

		List<Tuple<AgentType, Double>> followersTypeCounterList = new LinkedList<Tuple<AgentType, Double>>();
		List<Tuple<AgentType, Double>> panelTypeCounterList = new LinkedList<Tuple<AgentType, Double>>();

		//We get lists containing panel's and followers' preferences in strategies in descending order
		followersTypeCounterList = getStrategyPreferences(
						getDataModel().getMemberList());
		panelTypeCounterList = getStrategyPreferences(currentPanel);

		//Calculate the quotum. It is the number of supporters needed to pass a proposal. In this case proposal
		//is the strategy of the group. The quotum is a function of the social belief of the group
		double quotum = (population * getDataModel().getEstimatedSocialLocation()) / population;

		//Start with the most prefereed strategy of the panel (the strategy that the leader/leaders wish to follow
		//If this strategy is supported by a high enough number of followers (quotum) then we pick this strategy
		//Otherwise try the next best strategy. The lower the quotum the less easy is to get your proposal accepted
		//This is the case of dictatorship.
		Iterator<Tuple<AgentType, Double>> i = panelTypeCounterList.iterator();
		while (i.hasNext())
		{
			int n = 0;
			Tuple<AgentType, Double> panelPreference = i.next();
			while (panelPreference.getKey() != followersTypeCounterList.get(n).getKey())
			{
				n++;
			}
			double followerSupport = followersTypeCounterList.get(n).getValue();
			if (followerSupport >= quotum)
			{
				return panelPreference.getKey();
			}
		}
		//If we have reached this statement then we have not found a well suported strategy probably because the
		//quotum is very high (bottom of y axis - anarchism)
		return null;
	}

	/**
	 * This is a helper method which returns preferred startegies of a set of agents in descending order
	 * @param The set of agents
	 * @return A list of preferred startegies in descending order
	 */
	private List<Tuple<AgentType, Double>> getStrategyPreferences(
					List<String> agents)
	{

		int population = agents.size();

		Tuple<AgentType, Double> tftTypes = new Tuple<AgentType, Double>(
						AgentType.TFT, 0.0);
		Tuple<AgentType, Double> acTypes = new Tuple<AgentType, Double>(AgentType.AC,
						0.0);
		Tuple<AgentType, Double> adTypes = new Tuple<AgentType, Double>(AgentType.AD,
						0.0);
		Tuple<AgentType, Double> rTypes = new Tuple<AgentType, Double>(AgentType.R,
						0.0);

		//Count types in agents list
		for (String agentID : agents)
		{
			switch (getConn().getAgentById(agentID).getAgentType())
			{
				case AC:
					double oldCountAC = acTypes.getValue();
					acTypes.setValue(oldCountAC + 1);
					break;
				case AD:
					double oldCountAD = adTypes.getValue();
					adTypes.setValue(oldCountAD + 1);
					break;
				case TFT:
					double oldCountTFT = tftTypes.getValue();
					tftTypes.setValue(oldCountTFT + 1);
					break;
				case R:
					double oldCountR = rTypes.getValue();
					rTypes.setValue(oldCountR + 1);
					break;
			}
		}

		//Find the average of each type
		acTypes.setValue(acTypes.getValue() / population);
		adTypes.setValue(adTypes.getValue() / population);
		tftTypes.setValue(tftTypes.getValue() / population);
		rTypes.setValue(rTypes.getValue() / population);

		List< Tuple<AgentType, Double>> preferencesRatioList = new LinkedList<Tuple<AgentType, Double>>();

		//Add the ratios to the list
		preferencesRatioList.add(acTypes);
		preferencesRatioList.add(adTypes);
		preferencesRatioList.add(tftTypes);
		preferencesRatioList.add(rTypes);

		//Sort the preferred startegies in descending order
		Collections.sort(preferencesRatioList, preferencesComparator);

		return preferencesRatioList;
	}
	private Comparator< Tuple<AgentType, Double>> preferencesComparator = new Comparator< Tuple<AgentType, Double>>()
	{
		@Override
		public int compare(Tuple<AgentType, Double> o1, Tuple<AgentType, Double> o2)
		{
			Double v1 = o1.getValue();
			Double v2 = o2.getValue();
			return (v1 > v2 ? -1 : 1);
		}
	};

	@Override
	protected Tuple<Double, Double> updateTaxedPool(double sharedFood)
	{
		Tuple<Double, Double> newSharedAndReserve = new Tuple<Double, Double>();
		newSharedAndReserve.set(sharedFood, 0.0);
		return newSharedAndReserve;
	}

	@Override
	protected Tuple<AgentType, Double> makePayments()
	{
		return new Tuple<AgentType, Double>(this.getDataModel().getGroupStrategy(),
						this.getDataModel().getCurrentReservedFood());
	}

	@Override
	protected Tuple<InteractionResult, Double> interactWithOtherGroups()
	{
		Tuple<InteractionResult, Double> interactionResult = new Tuple<InteractionResult, Double>();
		interactionResult.set(InteractionResult.NothingHappened, 0.0);
		return interactionResult;
	}
}
