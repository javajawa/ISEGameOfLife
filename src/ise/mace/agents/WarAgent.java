/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.mace.agents;

import ise.mace.actions.Proposal.ProposalType;
import ise.mace.actions.Vote.VoteType;
import ise.mace.groups.WarGroup;
import ise.mace.inputs.Proposition;
import ise.mace.models.Food;
import ise.mace.models.HuntingTeam;
import ise.mace.participants.AbstractAgent;
import ise.mace.participants.PublicGroupDataModel;
import ise.mace.models.GroupDataInitialiser;
import static ise.mace.models.ScaledDouble.scale;
import ise.mace.tokens.AgentType;
import ise.mace.models.History;
import ise.mace.models.Tuple;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ise.mace.participants.AbstractGroupAgent;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
/**
 *
 * @author The0s
 */
public class WarAgent extends AbstractAgent
{
	private static final long serialVersionUID = 1L;

        public static String special;
        private static int special_no;

        private String invitationToGroup = null;

        private final static TreeSet<String> invitationHolders = new TreeSet<String>();
        private final static HashMap<String, String> groupFounders = new HashMap<String, String>();
        private final static TreeSet<String> membersToKickOut = new TreeSet<String>();
        private final static TreeSet<String> freeToGroup = new TreeSet<String>();
	private History<Double> satisfaction = new History<Double>(1);

//        private static  List<String> special_agents = new LinkedList<String>();

        Random randomGenerator = new Random();

	@Deprecated
	public WarAgent()
	{
		super();
        }

    public WarAgent(double initialFood, double consumption, AgentType type,
                              double socialBelief, double economicBelief){
        super("<hunter>", 0, initialFood, consumption, type, socialBelief, economicBelief);

    }

    //ADDED The0
    public WarAgent(double initialFood, double consumption, AgentType type,
                              double socialBelief, double economicBelief, String name){
        super("<hunter>", 0, initialFood, consumption, type, socialBelief, economicBelief, name);

    }

    @Override
    protected void onActivate() {
        freeToGroup.add(this.getId());
    }

    @Override
    protected void beforeNewRound() {
    }

    /**
    * This method enables agents to form groups. It uses a heuristic based on mutual trust and
    * the socio-economic beliefs. The agent can either try the heuristic with another free agent or
    * with an existing group. The priority is to find an existing group to join. If that fails then
    * we check compatibility between two free agents.
    * @param none
    * @return The group ID that this agent has chosen to join. If null no group is chosen.
     *        If leaveGroup is returned the agent requested to leave the group
    */
    @Override
    protected String chooseGroup() {
        
        if (this.getDataModel().getGroupId() != null) return null;

        String chosenGroup = "";

        Set<String> groups = getConn().availableGroups();

        if(!groups.isEmpty())
        {
            for(String groupID : groups)
            {
                if (!groupID.equals(PoliticalAgentGroup.special))
                {
                        double deltaEconomic  = getDataModel().getEconomicBelief() - getConn().getGroupById(groupID).getCurrentEconomicPoisition();
                        double deltaSocial  = getDataModel().getSocialBelief() - WarGroup.socialBeliefs.get(groupID);
                        double vectorDistance = Math.sqrt(Math.pow(deltaEconomic, 2) + Math.pow(deltaSocial, 2));
                        if (vectorDistance < 0.1)
                        {
                            chosenGroup = groupID;
                        }
                }
            }
        }

        if (chosenGroup.equals(""))
        {
            Class<? extends AbstractGroupAgent> gtype = getConn().getAllowedGroupTypes().get(0);
            String newGroupID = getConn().createGroup(gtype, new GroupDataInitialiser(this.uniformRandLong(), getDataModel().getEconomicBelief()));
            //TODO:The following line of code might solve the social position problem
            WarGroup.socialBeliefs.put(newGroupID, getDataModel().getSocialBelief());
            createGroupAgent(newGroupID); //Create the group agent
            return newGroupID;
        }

        return chosenGroup;
    }

    /**
     * Creates the agent that represents a group
     * @param chosenGroup : the name of the agent is equal to the group id representing
     */
    private void createGroupAgent(String chosenGroup){
        //****** ADDED THEO
        //GROUP INTO AGENTS
        PoliticalAgentGroup.special_no++;
        //Create special group
        if(special_no == 1){
            GroupDataInitialiser spGroup = new GroupDataInitialiser(this.uniformRandLong(),1.0);
            Class<? extends AbstractGroupAgent> gtype = getConn().getAllowedGroupTypes().get(0);
            PoliticalAgentGroup.special = getConn().createGroup(gtype, spGroup);
        }
        //Creates a political Agent-group
        getConn().createAgent(0, getConn().getGroupById(PoliticalAgentGroup.special).getCurrentEconomicPoisition(),0.5 , chosenGroup); //CREATE a new AGENT-Group
        //*********

    }


    @Override
    protected void groupApplicationResponse(boolean accepted) {
    }

    /**
    * This method enables agents to pick their preferred choice of food
    * The choice is based on several factors. First of all if we deal with a free agent
    * its choice is based only on its type (TFT, AD, AC or R). Otherwise, the agent belongs
    * to a group it can also ask for advice. If the advice is not good enough then the agent just
    * follows its type.
    * @param none
    * @return The chosen food for this round.
    */
    @Override
    protected Food chooseFood()
    {
            if (getDataModel().getHuntingTeam() == null) return null;
            List<String> members = this.getDataModel().getHuntingTeam().getMembers();

            //We assume there will only be two food sources (stags/rabbits)
            List<Food> foodArray = new LinkedList<Food>();
            Food suggestedFood, cooperateFood, defectFood, choice;

            //Distinguish between stag (cooperate) and rabbit (defect)
            foodArray = this.getFoodTypes();
            cooperateFood = foodArray.get(0);
            defectFood = foodArray.get(1);

            //If the agent is not in a group or advisor didn't give a definitive answer then hunt
            //according to type
            switch (this.getDataModel().getAgentType())
            {
                    //The choice is always to hunt stags
                    case AC:
                            choice = cooperateFood;
                            break;

                    //The choice is always to hunt rabbits
                    case AD:
                            choice = defectFood;
                            break;

                    // Picks a random stratergy
                    case R:
                            choice = (uniformRandBoolean() ? cooperateFood : defectFood);
                            break;

                    //If first time cooperate else imitate what your partner (opponent?) choose the previous time
                    case TFT:
                            //Get last hunting choice of opponent and act accordingly
                            Food opponentPreviousChoice = cooperateFood;

                            // TFT makes no sense in a team of 1...
                            if (members.size() == 1)
                            {
                                    choice = cooperateFood;
                                    return choice;
                            }
                            //Get the previous choice of your pair. For this round imitate him.
                            //In the first round we have no hunting history therefore default choice is stag
                            if (members.get(0).equals(this.getId()))
                            {
                                    if (getConn().getAgentById(members.get(1)) != null)
                                    {
                                        if (getConn().getAgentById(members.get(1)).getHuntingHistory().size() != 1)
                                        {
                                                opponentPreviousChoice = getConn().getAgentById(members.get(1)).getHuntingHistory().getValue(1);
                                        }
                                    }
                            }
                            else
                            {
                                    if (getConn().getAgentById(members.get(0)) != null)
                                    {
                                        if (getConn().getAgentById(members.get(0)).getHuntingHistory().size() != 1)
                                        {
                                                opponentPreviousChoice = getConn().getAgentById(members.get(0)).getHuntingHistory().getValue(1);
                                        }
                                    }
                            }
                            choice = opponentPreviousChoice;
                            break;

                    default:
                            throw new IllegalStateException("Agent type was not recognised");
            }

            return choice;
    }

    /**
    * This method allows an agent to make a proposal to change the economic belief of the group.
    * Based on its own economic belief the agent decides the type of the proposal.
    * @param none
    * @return The type of the proposal. Three types {staySame, moveRight, moveLeft}
    */
    @Override
    protected ProposalType makeProposal()
    {
        //Do nothing. No proposals for this simulation
        return ProposalType.staySame;
    }

    /**
    * This method allows an agent to cast its vote about a proposal. The decision depends
    * on the economic belief of this agent.
    * @param p The proposition
    * @return The type of the vote. Two types {For, Against}
    */
    @Override
    protected VoteType castVote(Proposition p)
    {
        //Do nothing. No proposal = no vote
        return VoteType.Abstain;
    }

    /**
    * If this agent has been chosen to be an advisor then this method will return the advice
    * @param agent The asking agent
    * @param agentsTeam The hunting team the asking agent belongs to
    * @return The advice in the form of suggested food type
    */
    @Override
    protected Food giveAdvice(String agent, HuntingTeam agentsTeam)
    {
        //Do nothing
        return null;
    }

    /**
    * This method updates the agent's happiness after hunt.
    * @param foodHunted The amount of food the agent returned from hunting.
    * @param foodReceived The final amount of food the agent received after tax
    * @return The new happiness value
    */
    @Override
    protected double updateHappinessAfterHunt(double foodHunted,
                                    double foodReceived)
    {
        //NOTE: Free agents can update their happiness but not their loyalty (see next method)

            //'entitelment' denotes the amount of food an agent wants to get, at the least
            double entitlement = getDataModel().getEconomicBelief() * foodHunted;
            double surplus = foodReceived - entitlement;
            Double currentHappiness = getDataModel().getCurrentHappiness();

            if (currentHappiness == null)
                //By default we are all satisfied with the economic position
                //we start off in, unless you are always happy or just hate life
                currentHappiness = 0.5 * getDataModel().getEconomicBelief();

            //If surplus is >0 you're overjoyed and increase happiness
            //If surplus is <0 you are dissapointed and decrease your happiness
            //If surplus is zero nothing really changed
            currentHappiness = scale(currentHappiness, surplus, 0.1);

            return currentHappiness;
    }

    /**
    * This method updates the agent's loyalty after hunt. Note that loyalty is related to happiness
    * @param foodHunted The amount of food the agent returned from hunting.
    * @param foodReceived The final amount of food the agent received after tax
    * @return The new loyalty value
    */
    @Override
    protected double updateLoyaltyAfterHunt(double foodHunted, double foodReceived)
    {
        //TODO: Reuse most of the code from TestPoliticalAgent
        return 0;
    }

    /**
    * This method updates the agent's trust value for its current opponent after hunt.
    * @param foodHunted The amount of food the agent returned from hunting.
    * @param foodReceived The final amount of food the agent received after tax
    * @return A map entry containing the opponent's ID and the new trust value
    */
    @Override
    protected Map<String, Double> updateTrustAfterHunt(double foodHunted,
                                    double foodReceived)
    {
            String opponentID;
            Map<String, Double> newTrustValue = new HashMap<String, Double>();
            double trust;

            //get what this agent has chosen to hunt in this round
            Food lastHunted = this.getDataModel().getLastHunted();

            //Get the members of the hunting team
            if (this.getDataModel().getHuntingTeam() == null) return null;
            List<String> members = this.getDataModel().getHuntingTeam().getMembers();

            //If agent didn't go hunting or has no team pair then do nothing
            if ((lastHunted == null)||(members.size() <2)) return null;

            //Find out agent's opponent ID
            if (members.get(0).equals(this.getId()))
            {
                opponentID = members.get(1);

            }
            else
            {
                opponentID = members.get(0);

            }

            //Get agent's trust value for this particular opponent
            //If there is no entry initialise it
            if (this.getDataModel().getTrust(opponentID) != null)
            {
                trust = this.getDataModel().getTrust(opponentID);
            }
            else
            {
                trust = 0.1;
            }

            //If agent hunted stag then check what the opponent did. If betrayed decrease trust
            // otherwise increase it. If the agent hunted rabbit no change in trust
            if (lastHunted.getName().equals("Stag"))
            {
                    if (foodHunted == 0) //Agent has been betrayed
                    {
                            trust = scale(trust, -1, 0.3);
                    }
                    else //Opponent cooperated
                    {
                        trust = scale(trust, 1, 0.3);
                    }
            }
            else    //Agent hunted rabbit so no trust issues
            {
                trust = scale(trust, 0, 0.3);
            }

            newTrustValue.put(opponentID, trust);
            return  newTrustValue;
    }

    /**
    * This method updates the agent's loyalty after the voting results are published.
    * @param proposition The proposition
    * @param votes How many votes this proposition got. If votes > 0 proposition passed otherwise has not.
    * @param overallMovement The overall change in group's position after voting
    * @return The new loyalty value
    */
    @Override
    protected double updateLoyaltyAfterVotes(Proposition proposition, int votes,
                                    double overallMovement)
    {
        //Do nothing!
        return this.getDataModel().getCurrentLoyalty();
    }

    /**
    * This method updates the agent's happiness after the voting results are published.
    * @param proposition The proposition
    * @param votes How many votes this proposition got. If votes > 0 proposition passed otherwise has not.
    * @param overallMovement The overall change in group's position after voting
    * @return The new happiness value
    */
    @Override
    protected double updateHappinessAfterVotes(Proposition proposition, int votes,
                                    double overallMovement)
    {
        //Do nothing!
        return this.getDataModel().getCurrentHappiness();
    }

    /**
    * This method updates the agent's trust for the proposer after the voting results are published.
    * @param proposition The proposition
    * @param votes How many votes this proposition got. If votes > 0 proposition passed otherwise has not.
    * @param overallMovement The overall change in group's position after voting
    * @return The new loyalty value
    */
    @Override
    protected Map<String, Double> updateTrustAfterVotes(Proposition proposition,
                                    int votes, double overallMovement)
    {
        //Do nothing!
        return null;
        }

    /**
    * This method updates the agent's social belief after the voting results are published.
    * @param proposition The proposition
    * @param votes How many votes this proposition got. If votes > 0 proposition passed otherwise has not.
    * @param overallMovement The overall change in group's position after voting
    * @return The new social belief
    */
        @Override
	protected double updateSocialBeliefAfterVotes(Proposition proposition, int votes, double overallMovement)
        {
            //Do nothing!
            return this.getDataModel().getSocialBelief();
        }

    /**
    * This method updates the agent's economic belief after the voting results are published.
    * @param proposition The proposition
    * @param votes How many votes this proposition got. If votes > 0 proposition passed otherwise has not.
    * @param overallMovement The overall change in group's position after voting
    * @return The new social belief
    */
        @Override
        protected double updateEconomicBeliefAfterVotes(Proposition proposition, int votes, double overallMovement)
        {
            //Do nothing!
            return this.getDataModel().getEconomicBelief();
        }

    /**
    * An agent which has been invited to a group must be tagged in order to process the invitation later
    * @param group The group this agent has been invited to
    * @return none
    */
    @Override
    protected void onInvite(String group)
    {

    }


    /**
    * This is a helper method and distinguishes what is the food type for cooperation and defection
    * @param none
    * @return A list containing the food for cooperation and defection
    */
        private List<Food> getFoodTypes(){
            List<Food> foodArray = new LinkedList<Food>();
            List<Food> foodList = new LinkedList<Food>();
            Food cooperateFood, defectFood;

            //Stores the two sources in an array
            for (Food noms : getConn().availableFoods())
            {
                    foodArray.add(noms);
            }

            //Hunting a stag is equivalent to cooperation. Hunting rabbit is equivalent to defection
            if (foodArray.get(0).getNutrition() > foodArray.get(1).getNutrition())
            {
                    cooperateFood = foodArray.get(0);
                    defectFood = foodArray.get(1);
            }
            else
            {
                    cooperateFood = foodArray.get(1);
                    defectFood = foodArray.get(0);
            }

            foodList.add(cooperateFood);
            foodList.add(defectFood);
            return foodList;
       }

        private Comparator< Tuple<String, Double> > c = new Comparator< Tuple<String, Double> >() {
            @Override
            public int compare(Tuple<String, Double> o1, Tuple<String, Double> o2)
            {
                Double v1 = o1.getValue();
                Double v2 = o2.getValue();
            	return (v1>v2 ? -1 : 1);
            }
	};

    /**
    * This method allows followers to rate their leaders' decisions. The rating is based only on the external
    * strategy followed by the group.
    * @param none
    * @return A map structure containing new trust values for every member of the current panel of the group that this agent belongs to
    */
    @Override
    protected Map<String, Double> updateTrustAfterLeadersHunt() {

        String groupID = getDataModel().getGroupId();
        //If this is a free agent then no leader to rate
        if (groupID == null) return null;

        //Get the current panel of te group that this agent belongs to
        List<String> currentPanel = getConn().getGroupById(groupID).getPanel();

        //If there is nobobdy to rate or this agent is member of the current panel do nothing
        if (currentPanel.isEmpty()||(currentPanel.contains(getDataModel().getId()))) return null;

        //Get the preferred strategy for an agent i.e. its type and the strategy adopted by the panel
        //Positive or negative rating depends on the similarity of these two strategies
        AgentType groupStrategy = getConn().getGroupById(groupID).getGroupStrategy();
        AgentType followerStrategy = getDataModel().getAgentType();

        //The rating weighting is a simple function of the group's population
        int population = getConn().getGroupById(groupID).getMemberList().size();
        double rating = 1/population;

        Map<String, Double> newTrustValues = new HashMap<String, Double>();

        //If the agent supports the group's strategy it will give a positive rating to every member of the panel
        //The reward to a panel member is to increase its current trust value from this agent. Accordingly, the
        //punishment for a bad decision is to decrease the trust value! Note that there is a threshold associated with a leader (panel member)
        for (String panelMember: currentPanel)
        {
            if(getDataModel().getTrust(panelMember) != null)
            {
                if (followerStrategy == groupStrategy)
                {
                     double currentTrustForPanelMember = getDataModel().getTrust(panelMember);
                     currentTrustForPanelMember = scale(currentTrustForPanelMember, 1, rating);
                     newTrustValues.put(panelMember, currentTrustForPanelMember);
                }
                else
                {
                     double currentTrustForPanelMember = getDataModel().getTrust(panelMember);
                     currentTrustForPanelMember = scale(currentTrustForPanelMember, -1, rating);
                     newTrustValues.put(panelMember, currentTrustForPanelMember);
                }
            }
        }
    return newTrustValues;
    }
}

