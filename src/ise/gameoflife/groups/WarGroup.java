/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.groups;

import ise.gameoflife.inputs.LeaveNotification.Reasons;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.models.Tuple;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.tokens.AgentType;
import ise.gameoflife.tokens.InteractionResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
/**
 *
 * @author The0s
 */
public class WarGroup extends AbstractGroupAgent {
	private static final long serialVersionUID = 1L;

	@Deprecated
	public WarGroup() {
		super();
	}

	public WarGroup(GroupDataInitialiser dm) {
		super(dm);
	}

	@Override
	protected void onActivate() {
                this.setGroupStrategy(AgentType.R); //ADDED The0

	}

	@Override
	protected boolean respondToJoinRequest(String playerID) {
            return true;
	}

	private Comparator<String> c = new Comparator<String>() {
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
		ArrayList<HuntingTeam> teams = new ArrayList <HuntingTeam>();
		List<String> members = new ArrayList<String>(getDataModel().getMemberList());
                List<String> members_with_strategy = new ArrayList<String>();
                List<String> members_without_strategy = new ArrayList<String>();
                Collections.sort(members, c);

                //int agents = members.size();
                //Check if they have a strategy  ADDED THE0
//                for (String mem : members)
//                {
//                    if (getConn().getAgentById(mem) != null)
//                    {
//                        if (getConn().getAgentById(mem).getAgentType() != null)
//                        {
//                            members_with_strategy.add(mem);
//                        }
//                        else
//                        {
//                            members_without_strategy.add(mem);
//                        }
//                    }
//                }

                int agents = members.size();
//                int agents_s = members_with_strategy.size();
//                int agents_no_s = members_without_strategy.size();

		for(int i=0; i < agents; i += 2){
			int ubound = (i + 2 >= agents) ? agents : i + 2;
			teams.add(new HuntingTeam(members.subList(i, ubound)));
                }

//                for(int i=0; i < agents_no_s; i += 2){
//			int ubound = (i + 2 >= agents_no_s) ? agents_no_s : i + 2;
//			teams.add(new HuntingTeam(members_without_strategy.subList(i, ubound)));
//                }

                return teams;
	}

	@Override
	protected void onMemberLeave(String playerID, Reasons reason) {
                //update economic belief of the group when the agent leaves the group
                double size = this.getDataModel().getMemberList().size();
                double economic = 0;
                for (String members : this.getDataModel().getMemberList()){
                    economic += getConn().getAgentById(members).getEconomicBelief();
                }
                economic = economic / (size);
                this.setEconomicPosition(economic);
	}

	@Override
	protected void beforeNewRound() {
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
        private List<String> updatePanel(){

            double groupSocialPosition;
            int population, panelSize;

            //STEP 1:Find the size of the panel. It is the proportion of the total population that
            // can be in the panel. It is calculated using the social position of the group.
            population = getDataModel().getMemberList().size();
            groupSocialPosition = getDataModel().getEstimatedSocialLocation();

            //Round to the closest integer
            panelSize = (int) Math.round(population * groupSocialPosition);
            if (panelSize == 0) //The group is on the very top of the axis. Dictatorship
            {
                //Force panelSize to be at least one (dictator)
                panelSize = 1;
            }
            //STEP 1 END

            //STEP 2: Get the average trust of each agent in the group
            List< Tuple<String, Double> > panelCandidates = new LinkedList< Tuple<String, Double> >();

            List<String> groupMembers = getDataModel().getMemberList();

            for (String candidate: groupMembers )
            {
                double sum = 0;
                int numKnownTrustValues = 0;
                for (String member: groupMembers )
                {
                    if ((getConn().getAgentById(member).getTrust(candidate) != null)&&(!member.equals(candidate)))
                    {
                        sum += getConn().getAgentById(member).getTrust(candidate);
                        numKnownTrustValues++;
                    }
                }

                Tuple<String, Double> tuple;
                if (numKnownTrustValues != 0)
                {
                    tuple = new Tuple<String, Double>(candidate, sum/numKnownTrustValues);
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
            if (!panelCandidates.isEmpty()&&(panelCandidates.size() >= panelSize))//Panel is not empty and we have enough candidates to select leaders
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

        private Comparator< Tuple<String, Double> > d = new Comparator< Tuple<String, Double> >() {
            @Override
            public int compare(Tuple<String, Double> o1, Tuple<String, Double> o2)
            {
                Double v1 = o1.getValue();
                Double v2 = o2.getValue();
            	return (v1>v2 ? -1 : 1);
            }
	};

    @Override
    protected AgentType decideGroupStrategy() {
        //Check if this group has leader/leaders. If leaders have not emerge yet then no decision at all
        List<String> currentPanel = getDataModel().getPanel();
        int population = getDataModel().getMemberList().size();

        if (currentPanel.isEmpty()||(population == 1))  return null;

        List<Tuple<AgentType, Double> > followersTypeCounterList = new LinkedList<Tuple<AgentType, Double> >();
        List<Tuple<AgentType, Double> > panelTypeCounterList = new LinkedList<Tuple<AgentType, Double> >();

        //We get lists containing panel's and followers' preferences in strategies in descending order
        followersTypeCounterList = getStrategyPreferences(getDataModel().getMemberList());
        panelTypeCounterList = getStrategyPreferences(currentPanel);

        //Calculate the quotum. It is the number of supporters needed to pass a proposal. In this case proposal
        //is the strategy of the group. The quotum is a function of the social belief of the group
        double quotum = (population * getDataModel().getEstimatedSocialLocation())/population;

        //Start with the most prefereed strategy of the panel (the strategy that the leader/leaders wish to follow
        //If this strategy is supported by a high enough number of followers (quotum) then we pick this strategy
        //Otherwise try the next best strategy. The lower the quotum the less easy is to get your proposal accepted
        //This is the case of dictatorship.
        Iterator<Tuple<AgentType, Double> > i = panelTypeCounterList.iterator();
        while(i.hasNext())
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

    private List<Tuple<AgentType, Double>> getStrategyPreferences(List<String> agents) {

        int population = agents.size();

        Tuple<AgentType, Double> tftTypes = new Tuple<AgentType, Double>(AgentType.TFT, 0.0);
        Tuple<AgentType, Double> acTypes = new Tuple<AgentType, Double>(AgentType.AC, 0.0);
        Tuple<AgentType, Double> adTypes = new Tuple<AgentType, Double>(AgentType.AD, 0.0);
        Tuple<AgentType, Double> rTypes = new Tuple<AgentType, Double>(AgentType.R, 0.0);

        //Count types in agents list
        for (String agentID : agents)
        {
            switch(getConn().getAgentById(agentID).getAgentType())
            {
                case AC:
                    double oldCountAC = acTypes.getValue();
                    acTypes.setValue(oldCountAC+1);
                    break;
                case AD:
                    double oldCountAD = adTypes.getValue();
                    adTypes.setValue(oldCountAD+1);
                    break;
                case TFT:
                    double oldCountTFT = tftTypes.getValue();
                    tftTypes.setValue(oldCountTFT+1);
                    break;
                case R:
                    double oldCountR = rTypes.getValue();
                    rTypes.setValue(oldCountR+1);
                    break;
            }
        }

        //Find the average of each type
        acTypes.setValue(acTypes.getValue()/population);
        adTypes.setValue(adTypes.getValue()/population);
        tftTypes.setValue(tftTypes.getValue()/population);
        rTypes.setValue(rTypes.getValue()/population);

        List< Tuple<AgentType, Double> > preferencesRatioList = new LinkedList<Tuple<AgentType, Double> >();

        //Add the ratios to the list
        preferencesRatioList.add(acTypes);
        preferencesRatioList.add(adTypes);
        preferencesRatioList.add(tftTypes);
        preferencesRatioList.add(rTypes);

        //Sort the preferred startegies in descending order
        Collections.sort(preferencesRatioList, preferencesComparator);

        return preferencesRatioList;
    }

    private Comparator< Tuple<AgentType, Double> > preferencesComparator = new Comparator< Tuple<AgentType, Double> >() {
        @Override
        public int compare(Tuple<AgentType, Double> o1, Tuple<AgentType, Double> o2)
        {
            Double v1 = o1.getValue();
            Double v2 = o2.getValue();
            return (v1>v2 ? -1 : 1);
        }
    };

    @Override
    protected double decideTaxForReservePool() {
        return 0;
    }

    @Override
    protected Tuple<AgentType, Double> makePayments()
    {
        return new Tuple<AgentType, Double>(this.getDataModel().getGroupStrategy(), this.getDataModel().getCurrentReservedFood());
    }

    @Override
    protected Tuple<InteractionResult, Double> interactWithOtherGroups() {
        //throw new UnsupportedOperationException("Not supported yet.");
        Tuple<InteractionResult, Double> interactionResult = new Tuple<InteractionResult, Double>();
        interactionResult.add(InteractionResult.NothingHappened, 0.0);
        return interactionResult;
    }
}

