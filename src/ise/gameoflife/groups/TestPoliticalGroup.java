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
import ise.gameoflife.participants.PublicAgentDataModel;
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
//            //find out if the agent itself has just created this group
//            //if so then there is no need to compute a heuristic
//            if (getDataModel().getMemberList().isEmpty())
//                return true;
//
//            //find out if the agent was invited into a newly created group
//            //if so then there is no need to to compute a heuristic
//            if (getDataModel().getMemberList().size() == 1)
//                return true;
//
//            //otherwise we have the usual agent requesting entry to this group
//            double heuristic = 0;
//
//            //used for the socio-economic faction of heuristic
//            double vectorDistance;
//            double maxDistance = Math.sqrt(2);
//            double economic, social, esFaction = 0;
//
//            //used for the trust faction of heuristic
//            double trustFaction = 0, trustSum = 0;
//            int numKnownTrustValues = 0;
//
//            //Obtain how much trust there is between the members of the group and the agent
//            for (String trustor : this.getDataModel().getMemberList()) {
//                    Double trustValue = this.getConn().getAgentById(trustor).getTrust(playerID);
//                    if (trustValue != null) {
//                            trustSum += trustValue;
//                            numKnownTrustValues++;
//                    }
//            }
//            if (numKnownTrustValues != 0) {
//                trustFaction = trustSum / numKnownTrustValues;
//            }
//
//            economic = this.getConn().getAgentById(playerID).getEconomicBelief() - this.getDataModel().getCurrentEconomicPoisition();//change in X
//            social = this.getConn().getAgentById(playerID).getSocialBelief() - this.getDataModel().getEstimatedSocialLocation();//change in Y
//            vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social, 2));
//            esFaction = 1 - (vectorDistance / maxDistance);
//
//            heuristic = 0.5*trustFaction + 0.5*esFaction;
//
//            if (heuristic > 0.5) {
//                return true;
//            }
//            else {
//                return false;
//            }

            //update economic belief of the group when agent joins (TRUE)
            double size = this.getDataModel().getMemberList().size();
            double economic = 0;
            for (String members : this.getDataModel().getMemberList()){
                if (getConn().getAgentById(members) != null)   //GIVES PROBLEMS
                    economic += getConn().getAgentById(members).getEconomicBelief();
            }
            economic += getConn().getAgentById(playerID).getEconomicBelief();
            economic = economic / (size+1);
            this.setEconomicPosition(economic);
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

            //TODO: a)When panelSize = 0 (top of y-axis) force it to become 1 (dictatorship)
            //      b)Have a threshold for trust values. If below that an agent cannot become leader ->emerging leadership
            
            //STEP 1:
            //Find the size of the panel. It is the proportion of the total population that
            // can be in the panel. It is calculated using the social position of the group.
            population = this.getDataModel().getMemberList().size();
            groupSocialPosition = this.getDataModel().getEstimatedSocialLocation();

            //Round to the closest integer
            panelSize = (int) Math.ceil(population*groupSocialPosition - 0.5);
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
                newPanel = null;
            }
            //STEP 4 END

            //FOR DEBUGGING ONLY
            Iterator< Tuple<String, Double> > i = panelCandidates.iterator();
            System.out.println("-------------------------------");
            System.out.println(getDataModel().getName());
            System.out.println(panelSize);
            while (i.hasNext()){
                Tuple<String, Double> agent = i.next();
                System.out.println("Agent "+ getConn().getAgentById(agent.getKey()).getName() + " has overall trust " + agent.getValue());
            }
            System.out.println("Therefore panel consists of: ");
            if (newPanel != null)
            {
                Iterator< String > j = newPanel.iterator();
                while (j.hasNext()){
                    String agentID = j.next();
                    System.out.println(getConn().getAgentById(agentID).getName());
                }
            }
             else
            {
                System.out.println("No leader/leaders yet");
            }
            //FOR DEBUGGING ONLY END

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
}
