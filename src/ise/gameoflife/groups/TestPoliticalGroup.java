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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
/**
 *
 * @author Aadil
 */
public class TestPoliticalGroup extends AbstractGroupAgent {
	private static final long serialVersionUID = 1L;

        private TreeSet<String> panel = new TreeSet<String>();

	@Deprecated
	public TestPoliticalGroup() {
		super();
	}

	public TestPoliticalGroup(GroupDataInitialiser dm) {
		super(dm);
	}

	@Override
	protected void onActivate() {
		// Do nothing.
	}

	@Override
	protected boolean respondToJoinRequest(String playerID) {    
                       
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
                double vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social, 2));

                //The longer the distance the lower the esFaction is. Therefore, agents close to group's beliefs have
                //higher probability of joining this group
                double esFaction = 1 - (vectorDistance / maxDistance);

                double heuristicValue;
                if (numKnownTrustValues != 0)
                {
                    //The actual heuristic value is calculated. The politics is more important for compatibility than
                    //trust when a group decides on the request, but trust does contribute signicantly
                    heuristicValue = 0.4*(trustSum/numKnownTrustValues) + 0.6*esFaction;
                }
                else
                {
                    heuristicValue = 0.6*esFaction;                
                }                                  

                if (heuristicValue > 0.5)
                {
                    //agent can join, so update economic beliefs
                    double size = this.getDataModel().getMemberList().size();
                    economic = 0;
                    for (String members : getDataModel().getMemberList()){
                        if (getConn().getAgentById(members) != null)   //GIVES PROBLEMS
                            economic += getConn().getAgentById(members).getEconomicBelief();
                    }
                    economic += getConn().getAgentById(playerID).getEconomicBelief();
                    economic = economic / (size+1);
                    setEconomicPosition(economic);                    
                    return true; 
                }
                else
                {   
                    return false;
                }         
            }
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
                Collections.sort(members, c);
                int agents = members.size();
                
		for(int i=0; i < agents; i += 2){
			int ubound = (i + 2 >= agents) ? agents : i + 2;
			teams.add(new HuntingTeam(members.subList(i, ubound)));
            }

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
		this.panel = updatePanel();
            }
	}
        
    /**
    * This method updates the panel for this group. The panel is the set of leaders in this group
    * The size of the panel depends on the social position of the group. If it is at the very top
    * it has a single leader (dictator). If it is at the bottom then every member belongs to the panel (anarchism).
    * @param none
    * @return The new panel members.
    */
        private TreeSet<String> updatePanel(){
            double groupSocialPosition;
            int population, panelSize;

            //STEP 1:Find the size of the panel. It is the proportion of the total population that
            // can be in the panel. It is calculated using the social position of the group.
            population = this.getDataModel().getMemberList().size();
            groupSocialPosition = this.getDataModel().getEstimatedSocialLocation();

            //Round to the closest integer
            panelSize = (int) Math.ceil(population*groupSocialPosition - 0.5);
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
            TreeSet<String> newPanel = new TreeSet<String>();
            if (!panelCandidates.isEmpty())
            {
                for (int i = 0; i < panelSize; i++)
                {
                    newPanel.add(panelCandidates.get(i).getKey());
                }
            }
            else
            {
                //return old panel. No change in leadership
                newPanel = this.panel;
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
        if (panel.isEmpty())  return null;

        Tuple<AgentType, Double> tftTypes = new Tuple<AgentType, Double>(AgentType.TFT, 0.0);
        Tuple<AgentType, Double> acTypes = new Tuple<AgentType, Double>(AgentType.AC, 0.0);
        Tuple<AgentType, Double> adTypes = new Tuple<AgentType, Double>(AgentType.AD, 0.0);
        Tuple<AgentType, Double> rTypes = new Tuple<AgentType, Double>(AgentType.R, 0.0);

        //Count followers types
        for (String followerID : getDataModel().getMemberList())
        {
            switch(getConn().getAgentById(followerID).getAgentType())
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
        
        List<Tuple<AgentType, Double> > typesCounterList = new LinkedList<Tuple<AgentType, Double> >();

        int population = getDataModel().getMemberList().size();

        //Find the average of each type
        acTypes.setValue(acTypes.getValue()/population);
        adTypes.setValue(adTypes.getValue()/population);
        tftTypes.setValue(tftTypes.getValue()/population);
        rTypes.setValue(rTypes.getValue()/population);

        //Add the ratios to the list
        typesCounterList.add(acTypes);
        typesCounterList.add(adTypes);
        typesCounterList.add(tftTypes);
        typesCounterList.add(rTypes);

        int followers = population - panel.size();
        double quotum = (followers * getDataModel().getEstimatedSocialLocation())/population;

          //FOR DEBUGGING ONLY
//        Iterator<Tuple<AgentType, Double> > j = typesCounterList.iterator();
//        System.out.println("---------------");
//        System.out.println(getDataModel().getName());
//        System.out.println("Population: "+ population);
//        System.out.println("Followers: "+ followers);
//        System.out.println("Social belief: "+ getDataModel().getEstimatedSocialLocation());
//        System.out.println("Quotum: "+quotum);
//        while(j.hasNext())
//        {
//            Tuple<AgentType, Double> counter = j.next();
//            System.out.println(counter.getKey() + ": " + counter.getValue());
//        }
//        System.out.println(getConn().availableGroups().size());
//        for (String a: getConn().availableGroups())
//        {
//            int size = getConn().getGroupById(a).getMemberList().size();
//            System.out.println(getConn().getGroupById(a).getId() + " of size: " + size );
//        }
        //FOR DEBUGGING ONLY END

        Iterator<Tuple<AgentType, Double> > i = typesCounterList.iterator();
        while(i.hasNext())
        {
            Tuple<AgentType, Double> typeRatio = i.next();
            if (typeRatio.getValue()>quotum)
            {
                return typeRatio.getKey();
            }
        }
        return null;
    }

    public TreeSet<String> getPanel(){
        return panel;
    }

}

