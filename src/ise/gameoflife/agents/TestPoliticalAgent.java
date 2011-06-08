/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ise.gameoflife.agents;

import ise.gameoflife.actions.Proposal.ProposalType;
import ise.gameoflife.actions.Vote.VoteType;
import ise.gameoflife.inputs.Proposition;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.participants.AbstractAgent;
import ise.gameoflife.participants.PublicGroupDataModel;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.models.ValueScaler;
import ise.gameoflife.tokens.AgentType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ise.gameoflife.participants.AbstractGroupAgent;
import java.util.TreeSet;
import presage.Participant;

/**
 *
 * @author george
 */

public class TestPoliticalAgent extends AbstractAgent
{

	private static final long serialVersionUID = 1L;

        private String invitationToGroup = null;

        private final static TreeSet<String> invitationHolders = new TreeSet<String>();
        private final static TreeSet<String> groupFounders = new TreeSet<String>();

	@Deprecated
	public TestPoliticalAgent()
	{
		super();
        }

    public TestPoliticalAgent(double initialFood, double consumption, AgentType type,
                              double socialBelief, double economicBelief){
        super("<hunter>", 0, initialFood, consumption, type, socialBelief, economicBelief);

    }

    @Override
    protected void onActivate() {
        //Do nothing
    }

    @Override
    protected void beforeNewRound() {
         //Do nothing
    }

    @Override
    protected String chooseGroup() {
         System.out.println("----------------------------------------------------");

        //ONLY FOR DEBUGGING
        if (this.getDataModel().getGroupId() == null)
            System.out.println("I, agent "+ this.getDataModel().getName() + " am a free agent!");
        else
            System.out.println("I, agent " + this.getDataModel().getName() + " with E belief: "+ this.getDataModel().getEconomicBelief() + " and I belong to group" + getConn().getGroupById(this.getDataModel().getGroupId()).getName());
        System.out.println("No of groups so far: " + getConn().availableGroups().size());
        //ONLY FOR DEBUGGING END

        String chosenGroup = "";
        
        //If agent is already member of a group do nothing
        if (this.getDataModel().getGroupId() != null) {
            //if (groupFounders.contains(this.getId()))
                    //groupFounders.remove(this.getId());
            //if (invitationHolders.contains(this.getId()))
                    //invitationHolders.remove(this.getId());

            return null;
        }
        else if(this.invitationToGroup != null) //If this agent has a pending invitation to a group, return the invitation
        {
            System.out.println("I was invited in a group so I will join it");
            return this.invitationToGroup;
        }
        else //If none of the above worked out then first try to find an optimal group to join with
        {
            chosenGroup = agentGroupGrouping();
            //ONLY FOR DEBUGGING
            if (chosenGroup.equals(""))
                System.out.println("I, agent "+this.getConn().getAgentById(this.getId()).getName() + " tried groups with no success" );
            else
                System.out.println("I, agent "+this.getConn().getAgentById(this.getId()).getName() + " tried groups and joined one" );
            //ONLY FOR DEBUGGING END
        }

        //And if the above line didn't work then try to group with other free agents
        if (chosenGroup.equals(""))
        {
           chosenGroup = freeAgentsGrouping();
           //ONLY FOR DEBUGGING
            if ((chosenGroup == null))
                System.out.println("I, agent "+this.getConn().getAgentById(this.getId()).getName() + " tried agents with no success" );
            else
                System.out.println("I, agent "+this.getConn().getAgentById(this.getId()).getName() + " tried agents and joined one" );
           //ONLY FOR DEBUGGING END
        }
        return chosenGroup;
    }

    private String agentGroupGrouping() {
        String chosenGroup = "";
        double currentHeuristic = 0, previousHeuristic = 0;
        //used for the socio-economic faction of heuristic
        double vectorDistance; 
        double maxDistance = Math.sqrt(2);
        double economic, social, esFaction=0;
        //used for the trust faction of heuristic
        double trustFaction=0, trustSum;
        int numKnownTrustValues;
        
        PublicGroupDataModel aGroup;

        //Assess each group in turn
        for (String groupID : getConn().availableGroups()) {
            aGroup = getConn().getGroupById(groupID);

            //Obtain how much trust there is between this agent and the members of the group
            numKnownTrustValues = 0;
            trustSum = 0;
            for (String trustee : aGroup.getMemberList()) {
                    Double trustValue = this.getDataModel().getTrust(trustee);

                    if (trustValue != null) {
                            trustSum += trustValue;
                            numKnownTrustValues++;
                    }
            }
            if(numKnownTrustValues != 0) {
                trustFaction = trustSum / numKnownTrustValues;
            }
            else {
                trustFaction = 0;
            }

            economic = aGroup.getCurrentEconomicPoisition() - this.getDataModel().getEconomicBelief();//change in X
            social = aGroup.getEstimatedSocialLocation() - getDataModel().getSocialBelief();//change in Y
            vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social, 2));
            esFaction = 1 - (vectorDistance / maxDistance);

            currentHeuristic = 0.3*trustFaction + 0.7*esFaction;

            if ((currentHeuristic > 0.5) && (previousHeuristic < currentHeuristic)) {
                chosenGroup = aGroup.getId();
                previousHeuristic = currentHeuristic;
            }
        }
        return chosenGroup;
    }    
    
    private String freeAgentsGrouping() {
        String chosenGroup = "";
        double currentHeuristic = 0, previousHeuristic = 0;
        //used for the socio-economic faction of heuristic
        double vectorDistance; 
        double maxDistance = Math.sqrt(2);
        double economic, social, esFaction=0;
        //used for the trust faction of heuristic
        double trustFaction=0;
        
        String bestPartner = "";
        int i =0;
        for (String trustee : getConn().getUngroupedAgents())
        {
            //if an agent is not comparing with itself and has not been invited
            if ((!this.getId().equals(trustee))&&(!invitationHolders.contains(trustee))&&(!groupFounders.contains(trustee)))
            {   i++;         System.out.println("No of ungrouped agents: " +i);
                Double trustValue = this.getDataModel().getTrust(trustee);
                if (trustValue != null) trustFaction = trustValue;

                economic = getConn().getAgentById(trustee).getEconomicBelief() - getDataModel().getEconomicBelief();//change in X
                social = getConn().getAgentById(trustee).getSocialBelief() - getDataModel().getSocialBelief();//change in Y
                vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social, 2));
                esFaction = 1 - (vectorDistance / maxDistance);

                currentHeuristic = 0.5*trustFaction + 0.5*esFaction;
                if ((currentHeuristic > 0.5) && (previousHeuristic < currentHeuristic))
                {
                    bestPartner = trustee;
                    previousHeuristic = currentHeuristic;
                }
            }
        }
        
        if (bestPartner.equals(""))
        {
            return null;
        }
        else
        {
            GroupDataInitialiser myGroup = new GroupDataInitialiser(this.uniformRandLong(), (this.getDataModel().getEconomicBelief() + getConn().getAgentById(bestPartner).getEconomicBelief())/2);
            Class<? extends AbstractGroupAgent> gtype = getConn().getAllowedGroupTypes().get(0);
            chosenGroup = getConn().createGroup(gtype, myGroup, bestPartner);
            groupFounders.add(this.getId());
            //ONLY FOR DEBUGGING
            System.out.println("I have tried the heuristic with "+this.getConn().getAgentById(bestPartner).getName());
            System.out.println("HEURISTIC = " + previousHeuristic);
            System.out.println("Therefore I can form a group with "+ this.getConn().getAgentById(bestPartner).getName() );
            //ONLY FOR DEBUGGING END
            return chosenGroup;
        }        
    }
       
    @Override
    protected void groupApplicationResponse(boolean accepted) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Food chooseFood()
    {
            //We assume there will only be two food sources (stags/rabbits)
            List<Food> foodArray = new LinkedList<Food>();
            Food cooperateFood, defectFood, choice;

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
                            List<String> members = this.getDataModel().getHuntingTeam().getMembers();
                            Food opponentPreviousChoice = cooperateFood;

                            // TFT makes no sense in a team of 1...
                            if (members.size() == 1)
                            {
                                    choice = defectFood;
                                    break;
                            }
                            //Get the previous choice of your pair. For this round imitate him.
                            //In the first round we have no hunting history therefore default choice is stag
                            if (members.get(0).equals(this.getId()))
                            {
                                    if (getConn().getAgentById(members.get(1)).getHuntingHistory().size() != 1)
                                    {
                                            opponentPreviousChoice = getConn().getAgentById(members.get(1)).getHuntingHistory().getValue(1);
                                    }
                            }
                            else
                            {
                                    if (getConn().getAgentById(members.get(0)).getHuntingHistory().size() != 1)
                                    {
                                            opponentPreviousChoice = getConn().getAgentById(members.get(0)).getHuntingHistory().getValue(1);
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
    protected ProposalType makeProposal()
    {
            String groupId = this.getDataModel().getGroupId();
            if (groupId != null)
            {   //If this agent is member of a group
                    double groupEconomicPosition = this.getConn().getGroupById(groupId).getCurrentEconomicPoisition();
                    double agentEconomicBelief = this.getDataModel().getEconomicBelief();
                    if (agentEconomicBelief > groupEconomicPosition)
                    {
                            return ProposalType.moveRight;
                    }
                    else if (agentEconomicBelief < groupEconomicPosition)
                    {
                            return ProposalType.moveLeft;
                    }
                    else
                    {
                            return ProposalType.staySame;
                    }
            }
            else
            {   //Proposal makes no sense for a free agent
                    return ProposalType.staySame;
            }
    }

    @Override
    protected VoteType castVote(Proposition p)
    {
//            String groupId = this.getDataModel().getGroupId();
//            String proposerGroup = p.getOwnerGroup();
//            ProposalType agentProposal;
//            if (groupId != null){ //check if is in a group
//                    if (groupId.equals(proposerGroup)){ //check if agent is in the same group as the proposal
//                            double groupEconomicPosition = this.getConn().getGroupById(groupId).getCurrentEconomicPoisition();
//                            double agentEconomicBelief = this.getDataModel().getEconomicBelief();
//                            if (agentEconomicBelief > groupEconomicPosition)
//                            {
//                                 agentProposal = ProposalType.moveRight;
//                            }
//                            else if (agentEconomicBelief < groupEconomicPosition)
//                            {
//                                agentProposal = ProposalType.moveLeft;
//                            }
//                            else
//                            {
//                                agentProposal = ProposalType.staySame;
//                            }
//                            //Compare proposals
//                            if (p.getType().equals(agentProposal))
//                            {
//                                return VoteType.For;
//                            }
//                            else
//                            {
//                                return VoteType.Against;
//                            }
//                    }
//                    else{ //must never happen!!
//                        throw new UnsupportedOperationException("Agent cannot vote for other Groups ");
//                    }
//            }
//            else //must never happen!!
//            {
//                return VoteType.Abstain;
//            }
            //throw new UnsupportedOperationException("Not supported yet.");
return VoteType.For;
             
    }

    @Override
    protected Food giveAdvice(String agent, HuntingTeam agentsTeam)
    {
            double MaxThreshold = 0.8;
            double MinThreshold = 0.2;
            String opponent;

            //find opponent
            if (agentsTeam.getMembers().get(0).equals(agent))
                opponent = agentsTeam.getMembers().get(1);
            else
                opponent = agentsTeam.getMembers().get(0);

            //get opponent trust value from "this" agent
            double opponentTrust = this.getDataModel().getTrust(opponent);

            //We assume there will only be two food sources (stags/rabbits)
            List<Food> foodArray = new LinkedList<Food>();
            Food cooperateFood, defectFood, choice;

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

            //Check for threshold values
            if(opponentTrust >= MaxThreshold)
            {
                    choice = cooperateFood;
            }
            else if(opponentTrust <= MinThreshold)
            {
                    choice = defectFood;
            }
            else
            {
                choice = null;  //!!!!!!! can be modified to use distribution for inbetween values
            }

            return choice;
    }

    @Override
    protected double updateHappinessAfterHunt(double foodHunted,
                                    double foodReceived)
    {
//            double myEconomic = getDataModel().getEconomicBelief();
//            double entitlement = myEconomic * foodHunted;
//            double difference, ratio, happiness;
//            
//            if ((Double)getDataModel().getCurrentHappiness() == null)
//                happiness = myEconomic;
//            else
//                happiness = getDataModel().getCurrentHappiness();
//
//            if (foodReceived == entitlement)
//            {
//                //you're satisifed, but happy
//                happiness += 0.01;//a measure of your satisfaction
//
//                if ((getDataModel().getCurrentHappiness() == 1) || (happiness >= 1)) 
//                    return 1;
//                else                  
//                    return happiness;             
//            }
//            
//            if (foodReceived > entitlement)
//            {
//                //you're overjoyed
//                difference = foodReceived - entitlement;
//                if (entitlement > difference)
//                    ratio = difference / entitlement;
//                else
//                    ratio = entitlement / difference;
//                happiness += ratio;//a measure of your happiness
//
//                if ((getDataModel().getCurrentHappiness() == 1) || (happiness >= 1)) 
//                    return 1;
//                else                  
//                    return happiness;                
//            }
//            
//            if (foodReceived < entitlement)
//            {
//                //you're dissapointed
//                difference = entitlement - foodReceived;
//                if (entitlement > difference)
//                    ratio = difference / entitlement;
//                else
//                    ratio = entitlement / difference;
//                happiness -= ratio;//a measure of your dissapointment
//
//                if ((getDataModel().getCurrentHappiness() == 0) || (happiness <= 0)) 
//                    return 0;
//                else                  
//                    return happiness;                                 
//            }
//            
//            return happiness;//if we got to this update here without hunting first then don't change anything
            return 0; //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double updateLoyaltyAfterHunt(double foodHunted, double foodReceived)
    {
            //loyalty after hunting refines from how happy you are after the hunt?        
//            if (this.getDataModel().getGroupId() != null)
//                return updateHappinessAfterHunt(foodHunted, foodReceived);
//            else
                return 0;//agent doesnt belong to a group and so is not loyal to anyone
            //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Map<String, Double> updateTrustAfterHunt(double foodHunted,
                                    double foodReceived)
    {
            Food lastHunted = this.getDataModel().getLastHunted();
            List<String> members = this.getDataModel().getHuntingTeam().getMembers();
            if ((lastHunted == null)||(members.size() <2)) return null;

            String opponentID;

            Map<String, Double> newTrustValue = new HashMap<String, Double>();
            double trust;

            if (members.get(0).equals(this.getId()))
            {
                opponentID = members.get(1);

            }
            else
            {
                opponentID = members.get(0);
    
            }

            if (this.getDataModel().getTrust(opponentID) != null)
                trust = this.getDataModel().getTrust(opponentID);
            else
                trust = 0;
            
            if (lastHunted.getName().equals("Stag"))
            {
                    if (foodHunted == 0) //Agent has been betrayed
                    {
                        trust = ValueScaler.scale(trust, -1, 0.1);
                    }
                    else
                    {
                        trust = ValueScaler.scale(trust, 1, 0.1);
                    }
            }
            else    //Agent hunted rabbit so no trust issues
            {
                trust = ValueScaler.scale(trust, 0, 0.1);
            }
           
            newTrustValue.put(opponentID, trust);
            return  newTrustValue;

    }

    @Override
    protected double updateLoyaltyAfterVotes(Proposition proposition, int votes,
                                    double overallMovement)
    {
            //loyalty after a vote refines from how happy you are after the vote?        
//            if (this.getDataModel().getGroupId() != null)
//                return updateHappinessAfterHunt(foodHunted, foodReceived);
//            else
                return 0;//agent doesnt belong to a group and so is not loyal to anyone
            //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double updateHappinessAfterVotes(Proposition proposition, int votes,
                                    double overallMovement)
    {
        
            return 0;
            //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Map<String, Double> updateTrustAfterVotes(Proposition proposition,
                                    int votes, double overallMovement)
    {
            Map<String, Double> newTrustValue = new HashMap<String, Double>();
            String proposer = proposition.getProposer();
            double proposerTrust;
            if (!this.getDataModel().getId().equals(proposer)){
                //check for previous value
                if (this.getDataModel().getTrust(proposer) != null)
                {
                       proposerTrust = this.getDataModel().getTrust(proposer); //get current trust of proposer
                 }
                else
                 {
                       proposerTrust = 0;
                }
                //discuss... //increase or decrease trust according to what agent vote and what was the proposal
                /*
                //update the value
                if (this.castVote(proposition).equals(VoteType.For))
                {
                       proposerTrust = ValueScaler.scale(proposerTrust, votes, 0.1);
                }
                else if(this.castVote(proposition).equals(VoteType.Against))
                {
                       proposerTrust = ValueScaler.scale(proposerTrust, -1, 0.1);
                }
                else
                {
                   //do nothing
                }
                */
                    //increase the trust for proposer by number of votes
                    proposerTrust = ValueScaler.scale(proposerTrust, votes, 0.1);
            }
            else
            {
                    proposerTrust = 1; //trust for himself
            }

            newTrustValue.put(proposer, proposerTrust);

            return newTrustValue;
            //throw new UnsupportedOperationException("Not supported yet.");
        }

	@Override
	protected void onInvite(String group)
	{
		invitationHolders.add(this.getId());
                this.invitationToGroup = group;

	}

}