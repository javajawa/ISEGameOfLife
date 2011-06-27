/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.mace.agents;

import static ise.mace.models.ScaledDouble.scale;
import ise.mace.actions.Proposal.ProposalType;
import ise.mace.actions.Vote.VoteType;
import ise.mace.inputs.Proposition;
import ise.mace.models.Food;
import ise.mace.models.GroupDataInitialiser;
import ise.mace.models.HuntingTeam;
import ise.mace.models.Tuple;
import ise.mace.participants.AbstractAgent;
import ise.mace.participants.AbstractGroupAgent;
import ise.mace.tokens.AgentType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 */
public class LoansAgent extends AbstractAgent{

    private static final long serialVersionUID = 1L;

    @Deprecated
    public LoansAgent()
    {
    	super();
    }

    public LoansAgent(double initialFood, double consumption, AgentType type,
                              double socialBelief, double economicBelief){
        super("<hunter>", 0, initialFood, consumption, type, socialBelief, economicBelief);

    }

    @Override
    protected void onActivate() {
    }

    @Override
    protected void beforeNewRound() {
    }

    @Override
    protected String chooseGroup() {
        Random randomGenerator = new Random();
        
        if (this.getDataModel().getGroupId() != null) return null;

        if (randomGenerator.nextDouble() > 0.9)
        {
                Class<? extends AbstractGroupAgent> gtype = getConn().getAllowedGroupTypes().get(0);
                String chosenGroup = getConn().createGroup(gtype, new GroupDataInitialiser(this.uniformRandLong(), getDataModel().getEconomicBelief()));
                createGroupAgent(chosenGroup); //Create the group agent
                return chosenGroup;
        }
        else
        {
            //TODO: When there is no ungrouped agent left check for one membered groups and release their members
            Set<String> groups = getConn().availableGroups();
            int randomIndex = (int)Math.round(randomGenerator.nextDouble()*groups.size());
            Iterator<String> i = groups.iterator();
            String groupID = null;
            for(int j = 0; j<randomIndex; j++)
            {
                groupID = i.next();
            }
            return groupID;
        }
    }

     /**
     * Creates the agent that represents a group
     * @param chosenGroup : the name of the agent is equal to the group id representing
     */
    private void createGroupAgent(String chosenGroup){
        //GROUP INTO AGENTS
        PoliticalAgentGroup.special_no++;
        //Create special group
        if(PoliticalAgentGroup.special_no == 1){
            GroupDataInitialiser spGroup = new GroupDataInitialiser(this.uniformRandLong(),1.0);
            Class<? extends AbstractGroupAgent> gtype = getConn().getAllowedGroupTypes().get(1);
            PoliticalAgentGroup.special = getConn().createGroup(gtype, spGroup);
        }
        //Creates a political Agent-group
        getConn().createAgent(0, getConn().getGroupById(PoliticalAgentGroup.special).getCurrentEconomicPoisition(),0.5 , chosenGroup); //CREATE a new AGENT-Group

    }
    
    @Override
    protected void groupApplicationResponse(boolean accepted) {
        //TODO: Reuse the code of TestPoliticalAgent. No change here
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
            Food cooperateFood, defectFood, choice;

            //Distinguish between stag (cooperate) and rabbit (defect)
            foodArray = this.getFoodTypes();
            cooperateFood = foodArray.get(0);
            defectFood = foodArray.get(1);

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

    @Override
    protected ProposalType makeProposal() {
        //Do nothing. No proposals for this simulation
        return ProposalType.staySame;
    }

    @Override
    protected VoteType castVote(Proposition p) {
        //Do nothing. No proposal = no vote
        return VoteType.Abstain;
    }

    @Override
    protected Food giveAdvice(String agent, HuntingTeam agentsTeam) {
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

    @Override
    protected double updateLoyaltyAfterHunt(double foodHunted, double foodReceived) {
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

    @Override
    protected Map<String, Double> updateTrustAfterLeadersHunt() {
        //TODO: It depends on what we will implement in decideGroupStrategy().
        return null;
    }

    @Override
    protected double updateLoyaltyAfterVotes(Proposition proposition, int votes, double overallMovement) {
        //Do nothing!
        return this.getDataModel().getCurrentLoyalty();
    }

    @Override
    protected double updateHappinessAfterVotes(Proposition proposition, int votes, double overallMovement) {
        //Do nothing!
        return this.getDataModel().getCurrentHappiness();
    }

    @Override
    protected double updateSocialBeliefAfterVotes(Proposition proposition, int votes, double overallMovement) {
        //Do nothing!
        return this.getDataModel().getSocialBelief();
    }

    @Override
    protected double updateEconomicBeliefAfterVotes(Proposition proposition, int votes, double overallMovement) {
        //Do nothing!
        return this.getDataModel().getEconomicBelief();
    }

    @Override
    protected Map<String, Double> updateTrustAfterVotes(Proposition proposition, int votes, double overallMovement) {
        //Do nothing!
        return null;
    }

    @Override
    protected void onInvite(String group) {
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
}