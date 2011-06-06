/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ise.gameoflife.groups;

import ise.gameoflife.inputs.LeaveNotification.Reasons;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.participants.AbstractGroupAgent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
//            double heuristic;
//
//            //used for the socio-economic faction of heuristic
//            double vectorDistance;
//            double maxDistance = Math.sqrt(2);
//            double economic, social, esFaction;
//
//            //used for the trust faction of heuristic
//            double trustFaction, trustSum = 0;
//            int numKnownTrustValues = 0;
//
//            //Obtain how much trust there is between the memberss of the group and the agent
//            for (String trustor : this.getDataModel().getMemberList()) {
//                    Double trustValue = this.getConn().getAgentById(trustor).getTrust(playerID);
//                    if (trustValue != null) {
//                            trustSum += trustValue;
//                            numKnownTrustValues++;
//                    }
//            }
//            if (numKnownTrustValues == 0) {
//                return true;//this agent, playerID, wants to make a group of only himself
//            }
//            trustFaction = trustSum / numKnownTrustValues;
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
	/**
	 * Determines the optimum hunting choice in terms of the food gained/hunters needed ratio
	 * divides the group up into teams of this size, then passes them the order to hunt that food.
	 * If the group number doesn't divide easily, the excess will be put into a smaller team.
	 * This team will still be given the same order.
	 * @return the assignment of teams and which food they are to hunt.
	 */
	@Override
	public List<HuntingTeam> selectTeams() {
		ArrayList<HuntingTeam> teams = new ArrayList <HuntingTeam>();
		List<String> members = getDataModel().getMemberList();
		int agents = members.size();

		for(int i=0; i < agents; i += 2){
			int ubound = (i + 2 >= agents) ? agents : i + 2;
			teams.add(new HuntingTeam(members.subList(i, ubound)));
		}

		return teams;
	}

	@Override
	protected void onMemberLeave(String playerID, Reasons reason) {
		// Do nothing
	}

	@Override
	protected void beforeNewRound() {
		// Do nothing
	}
	
}
