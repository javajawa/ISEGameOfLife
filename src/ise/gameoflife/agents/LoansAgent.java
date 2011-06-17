/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.agents;

import ise.gameoflife.actions.Proposal.ProposalType;
import ise.gameoflife.actions.Vote.VoteType;
import ise.gameoflife.inputs.Proposition;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.models.Tuple;
import ise.gameoflife.participants.AbstractAgent;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.tokens.AgentType;
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
 * @author george
 */
public class LoansAgent extends AbstractAgent{

    private static final long serialVersionUID = 1L;

    private static int groupCounter = 0;

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
                return getConn().createGroup(gtype, new GroupDataInitialiser(this.uniformRandLong(), getDataModel().getEconomicBelief()));
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

            String groupID = this.getDataModel().getGroupId();

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
        //TODO: If we get rid of advice in chooseFoo() then do nothing here
        return null;
    }

    @Override
    protected double updateHappinessAfterHunt(double foodHunted, double foodReceived) {
        //TODO: Reuse most of the code from TestPoliticalAgent
        return 0;
    }

    @Override
    protected double updateLoyaltyAfterHunt(double foodHunted, double foodReceived) {
        //TODO: Reuse most of the code from TestPoliticalAgent
        return 0;
    }

    @Override
    protected Map<String, Double> updateTrustAfterHunt(double foodHunted, double foodReceived) {
        //TODO: Reuse most of the code from TestPoliticalAgent
        return null;
    }

    @Override
    protected Map<String, Double> updateTrustAfterLeadersHunt() {
        //TODO: It depends on what we will implement in decideGroupStrategy().
        return null;
    }

    @Override
    protected double updateLoyaltyAfterVotes(Proposition proposition, int votes, double overallMovement) {
        //Do nothing!
        return 0;
    }

    @Override
    protected double updateHappinessAfterVotes(Proposition proposition, int votes, double overallMovement) {
        //Do nothing!
        return 0;
    }

    @Override
    protected double updateSocialBeliefAfterVotes(Proposition proposition, int votes, double overallMovement) {
        //Do nothing!
        return 0;
    }

    @Override
    protected double updateEconomicBeliefAfterVotes(Proposition proposition, int votes, double overallMovement) {
        //Do nothing!
        return 0;
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