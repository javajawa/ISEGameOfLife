/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.mace.groups;

import ise.mace.agents.PoliticalAgentGroup;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 *
 * @author The0s
 */
public class SpecialGroup extends AbstractGroupAgent {
	private static final long serialVersionUID = 1L;

        public static Map<String, Double> socialBeliefs = new HashMap<String, Double>();

	@Deprecated
	public SpecialGroup() {
		super();
	}

	public SpecialGroup(GroupDataInitialiser dm) {
		super(dm);

	}

	@Override
	protected void onActivate() {
                this.setGroupStrategy(AgentType.R); //ADDED The0

	}

	@Override
	protected boolean respondToJoinRequest(String playerID) {

            if (getConn().getAgentById(playerID) == null) //ADDED THE0
            {
                //agent does not exist so invitation is denied
                return false;
            }
            else if(this.getId().equals(PoliticalAgentGroup.special))
            { //exception for the Special group
                return true;
            }
            else
            {
                    //agent can join, so update economic beliefs
                    double size = this.getDataModel().getMemberList().size();
                    double economic = 0;
                    for (String members : getDataModel().getMemberList()){
                        if (getConn().getAgentById(members) != null)   //GIVES PROBLEMS
                            economic += getConn().getAgentById(members).getEconomicBelief();
                    }
                    economic += getConn().getAgentById(playerID).getEconomicBelief();
                    economic = economic / (size+1);
                    setEconomicPosition(economic);
                    return true;
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
                if(this.getId().equals(PoliticalAgentGroup.special)){
                    //do nothing
                }
                else{
                //update economic belief of the group when the agent leaves the group
                double size = this.getDataModel().getMemberList().size();
                double economic = 0;
                for (String members : this.getDataModel().getMemberList()){
                    economic += getConn().getAgentById(members).getEconomicBelief();
                }
                economic = economic / (size);
                this.setEconomicPosition(economic);
                System.out.println("CANNOT HAPPEN: Agent: " + getConn().getAgentById(playerID).getName() + " left its group (can only die)");
                }
        }

	@Override
	protected void beforeNewRound() {
            if (getDataModel().getMemberList().size() != 1)
            {
                List<String> newPanel = updatePanel();
                this.setPanel(newPanel);
            }
	}

    /**
    * This method updates the panel for this group. The panel is the set of leaders in this group
    * The size of the panel depends on the social position of the group. If it is at the very top
    * it has a single leader (dictator). If it is at the bottom then every member belongs to the panel (anarchism).
    * @param none
    * @return The new panel members.
    */
        private List<String> updatePanel(){
            List<String> panel = new LinkedList<String>();
            return panel;
        }

    @Override
    protected AgentType decideGroupStrategy() {
        return null;
    }


    @Override
    protected Tuple<AgentType, Double> makePayments()
    {
        return new Tuple<AgentType, Double>(this.getDataModel().getGroupStrategy(), this.getDataModel().getCurrentReservedFood());
    }

    @Override
    protected Tuple<InteractionResult, Double> interactWithOtherGroups() {
        Tuple<InteractionResult, Double> interactionResult = new Tuple<InteractionResult, Double>();
        interactionResult.add(InteractionResult.NothingHappened, 0.0);
        return interactionResult;
    }

    @Override
    protected Tuple<Double, Double> updateTaxedPool(double sharedFood) {
        Tuple<Double, Double> newSharedAndReserve = new Tuple<Double,Double>();
        newSharedAndReserve.add(sharedFood, 0.0);
        return newSharedAndReserve;
    }
}
