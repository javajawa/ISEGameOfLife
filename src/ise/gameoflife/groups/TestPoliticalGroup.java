/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ise.gameoflife.groups;

import ise.gameoflife.inputs.LeaveNotification.Reasons;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.participants.AbstractGroupAgent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
/**
 *
 * @author Aadil
 */
public class TestPoliticalGroup extends AbstractGroupAgent {
	private static final long serialVersionUID = 1L;

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
		/**Change groups economic belief onMemberLeave HERE
                 * !!!reset individual agent loyalty from agent NOT HERE
                 * !!!change economic belief when a new member joins the group NOT HERE
                 * */
            System.out.println("Member Leave");
	}

	@Override
	protected void beforeNewRound() {
		// Do nothing
	}
	
}
