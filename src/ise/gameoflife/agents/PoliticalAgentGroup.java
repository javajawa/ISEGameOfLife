/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.agents;

import ise.gameoflife.actions.Proposal.ProposalType;
import ise.gameoflife.actions.Vote.VoteType;
import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.inputs.Proposition;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.participants.AbstractAgent;
import ise.gameoflife.participants.PublicGroupDataModel;
import ise.gameoflife.models.GroupDataInitialiser;
import static ise.gameoflife.models.ScaledDouble.scale;
import ise.gameoflife.tokens.AgentType;
import ise.gameoflife.models.History;
import ise.gameoflife.models.Tuple;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ise.gameoflife.participants.AbstractGroupAgent;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import presage.Simulation;
/**
 *
 * @author The0s
 */
public class PoliticalAgentGroup extends AbstractAgent
{

	private static final long serialVersionUID = 1L;
        private static String special= null;
        private static int special_no=0;

        private String invitationToGroup = null;

        private final static TreeSet<String> invitationHolders = new TreeSet<String>();
        private final static HashMap<String, String> groupFounders = new HashMap<String, String>();
        private final static TreeSet<String> membersToKickOut = new TreeSet<String>();
        private final static TreeSet<String> freeToGroup = new TreeSet<String>();
	private History<Double> satisfaction = new History<Double>(1);

        public static  List<String> dead_agents = new LinkedList<String>();
        
    Random randomGenerator = new Random();

    @Deprecated
    public PoliticalAgentGroup()
    {
	super();
    }

    public PoliticalAgentGroup (double initialFood, double consumption, AgentType type,
                              double socialBelief, double economicBelief, String name){
        super("<hunter>", 0, initialFood, consumption, type, socialBelief, economicBelief, name);

    }

    @Override
    protected void onActivate() {
    }

    @Override
    protected void beforeNewRound() {
        if (getConn().getGroupById(this.getDataModel().getName()) == null){
            //DEACTIVATE list
            dead_agents.add(this.getId());
            System.out.println("Agent-Group must be killed : "+ this.getId() + "Group: " + this.getDataModel().getName());
        }
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

//        System.out.println("-------------START-FREE-TO-GROUP-WITH--------------------");
//        for (String agent : freeToGroup.descendingSet())
//        {
//            System.out.println(getConn().getAgentById(agent).getName());
//        }
//        System.out.println(freeToGroup.size());
//        System.out.println("-------------END-FREE-TO-GROUP-WITH--------------------");
//        System.out.println();
//        System.out.println();
//
//
//        System.out.println("-------------START-GROUP---------------------------");
//        for (String groupID : getConn().availableGroups())
//        {
//            int size = getConn().getGroupById(groupID).getMemberList().size();
//            System.out.println(getConn().getGroupById(groupID).getName() +" with size: " +size );
//            for (String a: getConn().getGroupById(groupID).getMemberList())
//            {
//                System.out.println("    "+getConn().getAgentById(a).getName());
//            }
//        }
//        System.out.println("--------------END-GROUP---------------------------");
//        System.out.println();
//        System.out.println();


        if(getConn().getAgentById(this.getId()).getGroupId() == null)
        {
            System.out.println("Special agent needs special treetment: "+TestPoliticalAgent.special );
            return TestPoliticalAgent.special;
        }
        else return null;
        //return can either be null or a String (which is the group)
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

            String groupID = this.getDataModel().getGroupId();
            //If the agent belongs to a group then it can ask for advice
            if (groupID != null && getConn().getGroupById(groupID).getMemberList().size() > 1)
            {
                suggestedFood = this.askAdvice(members);
                if (suggestedFood != null)
                {
                    return suggestedFood;
                }
            }
            //If the agent is not in a group or advisor didn't give a definitive answer then hunt
            //according to type

            AgentType group_type = AgentType.R;
            if (getConn().getGroupById(this.getDataModel().getName()) == null){
                System.out.println("Type for Agent-Group: " + this.getId() + " [" + this.getDataModel().getName()+ "] Not ");
            }
            else
            {
                group_type = this.getConn().getGroupById(this.getDataModel().getName()).getGroupStrategy();
                this.getDataModel().setAgentType(group_type);
                System.out.println("Type for Agent-Group [" + this.getDataModel().getName()+ "] : "+ group_type);

                if (group_type == null)
                {
                    return null;
                }
            }
            switch (group_type) /////THE0 ADDED
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
            double MaxThreshold = 0.9;
            double MinThreshold = 0.1;
            String opponentID = null;

            //find opponent
            if (agentsTeam.getMembers().get(0).equals(agent))
            {
                    if (getConn().getAgentById(agentsTeam.getMembers().get(1)) != null)
                    {
                        opponentID = agentsTeam.getMembers().get(1);
                    }
            }
            else
            {
                    if (getConn().getAgentById(agentsTeam.getMembers().get(0)) != null)
                    {
                        opponentID = agentsTeam.getMembers().get(0);
                    }
            }

            //get opponent's trust value from "this" agent
            double opponentTrust;
            if ((opponentID != null)&& (getDataModel().getTrust(opponentID)!= null))
            {
                opponentTrust = getDataModel().getTrust(opponentID);
            }
            else
            {
                return null;
            }

            //We assume there will only be two food sources (stags/rabbits)
            List<Food> foodArray = new LinkedList<Food>();
            Food cooperateFood, defectFood, choice;

            //Distinguish between stag (cooperate) and rabbit (defect)
            foodArray = this.getFoodTypes();
            cooperateFood = foodArray.get(0);
            defectFood = foodArray.get(1);

            //Check for threshold values. If this agent has high trust value for the opponent
            // the advice is to cooperate. Otherwise the advice is to defect.
            if(opponentTrust >= MaxThreshold)
            {
                    choice = cooperateFood;
            }
            else if(opponentTrust <= MinThreshold)
            {
                    choice = defectFood;
            }
            else //This agent cannot say for sure if the opponent will cooperate or not
            {
                choice = null;
            }
            return choice;
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
//        //NOTE: Free agents can update their happiness but not their loyalty (see next method)
//
//            //'entitelment' denotes the amount of food an agent wants to get, at the least
//            double entitlement = getDataModel().getEconomicBelief() * foodHunted;
//            double surplus = foodReceived - entitlement;
//            Double currentHappiness = getDataModel().getCurrentHappiness();
//
//            if (currentHappiness == null)
//                //By default we are all satisfied with the economic position
//                //we start off in, unless you are always happy or just hate life
//                currentHappiness = 0.5 * getDataModel().getEconomicBelief();
//
//            //If surplus is >0 you're overjoyed and increase happiness
//            //If surplus is <0 you are dissapointed and decrease your happiness
//            //If surplus is zero nothing really changed
//            currentHappiness = scale(currentHappiness, surplus, 0.1);
//
//            return currentHappiness;
        return 0;
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
//            //Loyalty after hunting refines from how much more happy you are after the hunt
//            //and from comparing your economic (sharing of food) belief with the group's belief.
//            String groupId = getDataModel().getGroupId();
//            if (groupId != null  && getConn().getGroupById(groupId).getMemberList().size() > 1)
//            {
//                //get change in economic beliefs
//                double myEconomic = getDataModel().getEconomicBelief();
//                double myGroupEconomic = getConn().getGroupById(getDataModel().getGroupId()).getCurrentEconomicPoisition();
//
//                //how close are you to the group's belief
//                double deltaEconomic = Math.abs(myGroupEconomic - myEconomic);
//
//                //get change in happiness
//
//                Double oneTurnAgoHappiness = this.getDataModel().getHappinessHistory().getValue(1);
//
//                //if there is no entry for happiness initialise it
//                if (oneTurnAgoHappiness == null)
//                {
//                    oneTurnAgoHappiness = 0.5 * myEconomic;
//                }
//
//                //Calculate difference in happiness between the current and the previous round
//                Double currentHappiness = getDataModel().getCurrentHappiness();
//                double deltaHappiness =  currentHappiness - oneTurnAgoHappiness ;//how much or less happy did you get
//
//                //get new loyalty
//                Double currentLoyalty = getDataModel().getCurrentLoyalty();
//
//                //As this if statement implies either entry to your first group or
//                //entry to a new (but not necessarily your first) group then you're
//                //loyal to the average sense (not too much and no too little)
//                if (currentLoyalty == null || currentLoyalty == 0)
//                {
//                    currentLoyalty = 0.5 * (oneTurnAgoHappiness + deltaEconomic);
//                }
//
//                //If deltaHappiness is < 0 you lose loyalty to the group. Otherwise if deltaHappiness is >0
//                //you gain loyalty. If deltaHappiness is zero you don't change your loyalty
//                currentLoyalty = scale(currentLoyalty, deltaHappiness, deltaEconomic);
//
//                return currentLoyalty;
//            }
//            else //agent doesnt belong to a group and so is not loyal to anyone
//            {
//                return 0;
//            }
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
//            //Loyalty after voting refines from how much more happy you are after the vote
//            //and from comparing your economic (decision to deviate from your belief) belief
//            //with the group's belief.
//            String groupId = getDataModel().getGroupId();
//            if (groupId != null  && getConn().getGroupById(groupId).getMemberList().size() > 1)
//            {
//                //get change in economic beliefs
//                double myEconomic = getDataModel().getEconomicBelief();
//                double myGroupEconomic = getConn().getGroupById(getDataModel().getGroupId()).getCurrentEconomicPoisition();
//                double deltaEconomic = Math.abs(myGroupEconomic - myEconomic);//how close are you to the group's belief
//
//                //get change in happiness
//                Double oneTurnAgoHappiness = getDataModel().getHappinessHistory().getValue(1);
//                if (oneTurnAgoHappiness == null)
//                {
//                    oneTurnAgoHappiness = 0.5 * myEconomic;
//                }
//                double currentHappiness = getDataModel().getCurrentHappiness();
//                double deltaHappiness = currentHappiness - oneTurnAgoHappiness;//how much or less happy did you get
//
//                //get new loyalty
//                Double currentLoyalty = getDataModel().getCurrentLoyalty();
//                if (currentLoyalty == null || currentLoyalty == 0)
//                    //As this if statement implies either entry to your first group or
//                    //entry to a new (but not necessarily your first) group then you're
//                    //loyal to the average sense (not too much and no too little)
//                    currentLoyalty = 0.5 * (oneTurnAgoHappiness + deltaEconomic);
//
//               //If this concerns you...
//                if (this.getDataModel().getGroupId().equals(proposition.getOwnerGroup()))
//                {
//                        //If deltaHappiness <0 you lose loyalty to your group
//                        //If deltaHappiness >0 you increase your loyalty to your group
//                        //If deltaHappiness = 0 you don't change at all
//                        currentLoyalty = scale(currentLoyalty, deltaHappiness, deltaEconomic);
//                }
//                return currentLoyalty;
//            }
//            else
                return 0;//agent doesnt belong to a group and so is not loyal to anyone
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
            Double currentHappiness = getDataModel().getCurrentHappiness();

            if (getDataModel().getGroupId() == null) return currentHappiness;

            if (currentHappiness == null)
            {
                //By default we are all satisfied with the economic position
                //we start off in, unless you are always happy or just hate life
                currentHappiness = 0.5 * getDataModel().getEconomicBelief();
            }

            //If this concerns you...
            if (getDataModel().getGroupId().equals(proposition.getOwnerGroup()))
            {
                    //If votes > 0 you are happy your proposition was passed
                    //If votes < 0 you are dissapointed your proposition was not passed
                    currentHappiness = scale(currentHappiness, votes, Math.abs(overallMovement));
             }

            return currentHappiness;
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
            Map<String, Double> newTrustValue = new HashMap<String, Double>();
            String proposer = proposition.getProposer();
            double proposerTrust;

            //Check if proposer is not this agent. There is no point in increasing (or decreasing)
            //the trust to yourself
            if (!this.getDataModel().getId().equals(proposer))
            {
                if (this.getDataModel().getTrust(proposer) != null)
                {
                       proposerTrust = this.getDataModel().getTrust(proposer); //get current trust of proposer
                }
                else
                {
                       proposerTrust = 0.1;
                }

                //if votes > 0 we increase the trust for proposer
                //if votes < 0 we decrease the trust for proposer
                proposerTrust = scale(proposerTrust, votes, Math.abs(overallMovement));
                newTrustValue.put(proposer, proposerTrust);
             }
             else
             {
                    newTrustValue = null;
             }
            return newTrustValue;
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
            if (getConn().getGroupById(this.getDataModel().getName()) == null)
            {
                //this.beforeNewRound();
                return 0;
            }
            else
            {
                return getConn().getGroupById(this.getDataModel().getName()).getEstimatedSocialLocation();
            }
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
            if (getConn().getGroupById(this.getDataModel().getName()) == null)
            {
                //this.beforeNewRound();
                return 0;
            }
            else
            {
                return getConn().getGroupById(this.getDataModel().getName()).getCurrentEconomicPoisition();
            }
        }

    /**
    * An agent which has been invited to a group must be tagged in order to process the invitation later
    * @param group The group this agent has been invited to
    * @return none
    */
    @Override
    protected void onInvite(String group)
    {
    	invitationHolders.add(this.getId());
        this.invitationToGroup = group;
    }

    /**
    * An agent which belongs to a group can consult another agent to choose what type of food to hunt
    * given its current opponent
    * @param none
    * @return The suggested food type
    */
    private Food askAdvice(List<String> members) {
//        Food suggestedFood = null;
//        String opponentID = null;
//
//        //If the agent has no pair then no advice
//        if (members.size() == 1) return null;
//
//        //Find opponent's ID
//        if (members.get(0).equals(this.getId()))
//        {
//                if (getConn().getAgentById(members.get(1)) != null)
//                {
//                    opponentID = members.get(1);
//                }
//        }
//        else
//        {
//                if (getConn().getAgentById(members.get(0)) != null)
//                {
//                    opponentID = members.get(0);
//                }
//        }
//
//
//        //Get the hunting teams history of the opponent. Get the last hunting team of the opponent
//        //and find out which agent was its opponent at that time. This agent has the latest information
//        //about our opponent. Therefore this agent is the advisor.
//        if (opponentID != null)
//        {
//            HuntingTeam opponentPreviousTeam = null;
//            if (getConn().getRoundsPassed() > 26)
//                opponentPreviousTeam = getConn().getAgentById(opponentID).getTeamHistory().getValue(1);
//            if (opponentPreviousTeam != null)
//            {
//                for (String agent: opponentPreviousTeam.getMembers())
//                {
//                    if (!agent.equals(opponentID)&&!agent.equals(this.getId()))
//                    {
//                        return suggestedFood = seekAvice(agent);
//                    }
//                }
//            }
//        }
//        return suggestedFood;
    return null;
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
