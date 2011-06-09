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
import java.util.logging.Level;
import java.util.logging.Logger;

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

				private final static Logger logger = Logger.getLogger("gameoflife.PoliticalAgent");
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

    protected boolean SatisfiedInGroup() {
        double loyalty, trust, socioEconomic, satisfaction = 0;
        
        //how loyal you are to the group (effectively, are you happy in the group)
        if (getDataModel().getCurrentLoyalty() == null)
            loyalty = 0;
        else
            loyalty = getDataModel().getCurrentLoyalty();
        
        
        //how much trust is there between you and the rest of the group
        PublicGroupDataModel myGroup = getConn().getGroupById(getDataModel().getGroupId());
        double trustSum = 0;
        Double trustValue;
        int numKnownTrustValues = 0;
        
        for (String trustee : myGroup.getMemberList()) {
                trustValue = getDataModel().getTrust(trustee);
                if (trustValue != null) {
                        trustSum += trustValue;
                        numKnownTrustValues++;
                }
        }
        if (numKnownTrustValues != 0)
            trust = trustSum / numKnownTrustValues;
        else
            trust = 0;
        
        
        //how much more or less does my group believe in the same beliefs as mine
        double economic, social, vectorDistance, maxDistance = Math.sqrt(2);
        
        economic = myGroup.getCurrentEconomicPoisition() - this.getDataModel().getEconomicBelief();//change in X
        social = myGroup.getEstimatedSocialLocation() - getDataModel().getSocialBelief();//change in Y
        vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social, 2));
        socioEconomic = 1 - (vectorDistance / maxDistance);        
        
        
        //how much are you satisifed with this group
        satisfaction = 0.33*loyalty + 0.33*trust + 0.34*socioEconomic;
        if (satisfaction > 0.66)
            return true;
        else
            return false;
    }    
    
    @Override
    protected String chooseGroup() {
         logger.log(Level.INFO, "----------------------------------------------------");

        //ONLY FOR DEBUGGING
        if (this.getDataModel().getGroupId() == null)
            logger.log(Level.INFO, "I, agent {0} am a free agent!", this.getDataModel().getName());
        else
            logger.log(Level.INFO, "I, agent {0} with E belief: {1} and I belong to {2}", new Object[]{this.getDataModel().getName(), this.getDataModel().getEconomicBelief(), getConn().getGroupById(this.getDataModel().getGroupId()).getName()});
        logger.log(Level.INFO, "No of groups so far: {0}", getConn().availableGroups().size());
        //ONLY FOR DEBUGGING END

        String chosenGroup = "";
        
        //If agent is already member of a group do nothing
        if (this.getDataModel().getGroupId() != null) {
            if (groupFounders.contains(this.getId()))
                    groupFounders.remove(this.getId());
            if (invitationHolders.contains(this.getId()))
                    invitationHolders.remove(this.getId());
            //if (SatisfiedInGroup())
                //return null;
            //else
                //return leaveGroup;
            
            //This return statement must NEVER be removed!!!It can be the source of every possible bug!:P
            return null;
        }
        else if(this.invitationToGroup != null) //If this agent has a pending invitation to a group, return the invitation
        {
            logger.log(Level.INFO, "I was invited in a group so I will join it");
            return this.invitationToGroup;
        }
        else //If none of the above worked out then first try to find an optimal group to join with
        {
            chosenGroup = agentGroupGrouping();
            //ONLY FOR DEBUGGING
            if (chosenGroup.equals(""))
                logger.log(Level.INFO, "I, agent {0} tried groups with no success", this.getConn().getAgentById(this.getId()).getName());
            else
                logger.log(Level.INFO, "I, agent {0} tried groups and joined one", this.getConn().getAgentById(this.getId()).getName());
            //ONLY FOR DEBUGGING END
        }

        //And if the above line didn't work then try to group with other free agents
        if (chosenGroup.equals(""))
        {
           chosenGroup = freeAgentsGrouping();
           //ONLY FOR DEBUGGING
            if ((chosenGroup == null))
                logger.log(Level.INFO, "I, agent {0} tried agents with no success", this.getConn().getAgentById(this.getId()).getName());
            else
                logger.log(Level.INFO, "I, agent {0} tried agents and joined one", this.getConn().getAgentById(this.getId()).getName());
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
            if(numKnownTrustValues != 0)
                trustFaction = trustSum / numKnownTrustValues;
            else
                trustFaction = 0;

            economic = aGroup.getCurrentEconomicPoisition() - this.getDataModel().getEconomicBelief();//change in X
            social = aGroup.getEstimatedSocialLocation() - getDataModel().getSocialBelief();//change in Y
            vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social, 2));
            esFaction = 1 - (vectorDistance / maxDistance);

            currentHeuristic = 0.5*trustFaction + 0.5*esFaction;

            if ((currentHeuristic > 0.4) && (previousHeuristic < currentHeuristic)) {
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

        for (String trustee : getConn().getUngroupedAgents())
        {
            //if an agent is not comparing with itself and has not been invited
            if ((!this.getId().equals(trustee))&&(!invitationHolders.contains(trustee))&&(!groupFounders.contains(trustee)))
            {
                Double trustValue = this.getDataModel().getTrust(trustee);
                if (trustValue != null) trustFaction = trustValue;

                economic = getConn().getAgentById(trustee).getEconomicBelief() - getDataModel().getEconomicBelief();//change in X
                social = getConn().getAgentById(trustee).getSocialBelief() - getDataModel().getSocialBelief();//change in Y
                vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social, 2));
                esFaction = 1 - (vectorDistance / maxDistance);

                currentHeuristic = 0.5*trustFaction + 0.5*esFaction;
                if ((currentHeuristic > 0.6) && (previousHeuristic < currentHeuristic))
                {
                    bestPartner = trustee;
                    previousHeuristic = currentHeuristic;
                }
            }
        }
        
        if (bestPartner.equals(""))
            return null;
        else
        {
            GroupDataInitialiser myGroup = new GroupDataInitialiser(this.uniformRandLong(), (this.getDataModel().getEconomicBelief() + getConn().getAgentById(bestPartner).getEconomicBelief())/2);
            Class<? extends AbstractGroupAgent> gtype = getConn().getAllowedGroupTypes().get(0);
            chosenGroup = getConn().createGroup(gtype, myGroup, bestPartner);
            groupFounders.add(this.getId());
            //ONLY FOR DEBUGGING
            logger.log(Level.INFO, "I have tried the heuristic with {0}", this.getConn().getAgentById(bestPartner).getName());
            logger.log(Level.INFO, "HEURISTIC = {0}", previousHeuristic);
            logger.log(Level.INFO, "Therefore I can form a group with {0}", this.getConn().getAgentById(bestPartner).getName());
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

            //WORK IN PROGRESS
            String groupID = this.getDataModel().getGroupId();
            String advisor;
            int maxHistory = 0;
            int historySize;
            if (groupID != null)
            {
                for (String possibleAdvisor:  getConn().getGroupById(groupID).getMemberList())
                {
                    if (getConn().getAgentById(possibleAdvisor) != null)
                    {

                        historySize = getConn().getAgentById(possibleAdvisor).getHuntingHistory().size();
                        if (historySize > maxHistory)
                        {
                            advisor = possibleAdvisor;
                            maxHistory = historySize;
                        }
                    }
                }
            }
            
            //WORK IN PROGRESS END

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
                            Food opponentPreviousChoice = cooperateFood;
                            List<String> members = this.getDataModel().getHuntingTeam().getMembers();

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
    protected ProposalType makeProposal()
    {
            //Note : No need to check if agent is in a group. This is done by doMakeProposal

            String groupId = this.getDataModel().getGroupId();
            ProposalType proposal;
            //Get the economic beliefs of the agent and the group
            double groupEconomicPosition = this.getConn().getGroupById(groupId).getCurrentEconomicPoisition();
            double agentEconomicBelief = this.getDataModel().getEconomicBelief();

            //Three cases: -> Agent economic belief = Group economic belief -> agent proposes to stay there
            //             -> Agent economic belief > group economic belief -> agent prefers to move right (remember left is zero and right is one)
            //             -> Agent economic belief < group economic belief -> agent prefers to move left
            if (agentEconomicBelief > groupEconomicPosition)
            {
                    proposal = ProposalType.moveRight;
            }
            else if (agentEconomicBelief < groupEconomicPosition)
            {
                    proposal = ProposalType.moveLeft;
            }
            else
            {
                    proposal = ProposalType.staySame;
            }
            return proposal;
    }

    @Override
    protected VoteType castVote(Proposition p)
    {
            String groupId = this.getDataModel().getGroupId();
            String proposerGroup = p.getOwnerGroup();
            ProposalType agentProposal;
            VoteType vote = null;

            if (groupId != null)
            { //check if is in a group
                    if (groupId.equals(proposerGroup))
                    { //check if agent is in the same group as the proposal
                            double groupEconomicPosition = this.getConn().getGroupById(groupId).getCurrentEconomicPoisition();
                            double agentEconomicBelief = this.getDataModel().getEconomicBelief();

                            if (agentEconomicBelief > groupEconomicPosition)
                            {
                                 agentProposal = ProposalType.moveRight;
                            }
                            else if (agentEconomicBelief < groupEconomicPosition)
                            {
                                agentProposal = ProposalType.moveLeft;
                            }
                            else
                            {
                                agentProposal = ProposalType.staySame;
                            }
                            //Compare proposals
                            if (p.getType().equals(agentProposal))
                            {
                                vote = VoteType.For;
                            }
                            else
                            {
                                vote =  VoteType.Against;
                            }
                    }
                    else{ //must never happen!!
                        //throw new UnsupportedOperationException("Agent cannot vote for other Groups ");
                        vote =  VoteType.Abstain;
                    }
            }
            else //must never happen!!
            {
                vote =  VoteType.Abstain;
            }
            return vote;
    }

    @Override
    protected Food giveAdvice(String agent, HuntingTeam agentsTeam)
    {
            double MaxThreshold = 0.8;
            double MinThreshold = 0.2;
            String opponentID;

            //find opponent
            if (agentsTeam.getMembers().get(0).equals(agent))
                opponentID = agentsTeam.getMembers().get(1);
            else
                opponentID = agentsTeam.getMembers().get(0);

            //get opponent's trust value from "this" agent
            double opponentTrust = this.getDataModel().getTrust(opponentID);

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
            //'entitelment' denotes the amount of food an agent wants to get, at the least
            double entitlement = getDataModel().getEconomicBelief() * foodHunted;
            double surplus = foodReceived - entitlement;
            Double currentHappiness = getDataModel().getCurrentHappiness();

            //FOR DEBUGGING ONLY
            System.out.println("--------------------------------");
            System.out.println("My economic belief is: " + getDataModel().getEconomicBelief());
            System.out.println("I hunted : " + foodHunted + "units of food");
            System.out.println("Therefore I am entitled to receive: " + entitlement);
            System.out.println("I received: " + foodReceived);
            if (surplus == 0)
                System.out.println("I got back exactly what I expected");
            else if (surplus > 0)
                System.out.println("I got back more than what I expected");
            else
                System.out.println("I got back less than what I expected");
            //FOR DEBUGGING ONLY END
            
            if (currentHappiness == null)
                //By default we are all satisfied with the economic position
                //we start off in, unless you are always happy or just hate life
                currentHappiness = 0.5 * getDataModel().getEconomicBelief();
            System.out.println("My happiness before hunting was: " +currentHappiness);
            if (surplus > 0)
            {
                //you're overjoyed
                currentHappiness = ValueScaler.scale(currentHappiness, surplus, 0.1);
            }
            else if(surplus < 0)
            {
                //you're dissapointed
                currentHappiness = ValueScaler.scale(currentHappiness, surplus, 0.1);
            }
            else
            {   //surplus = 0
                currentHappiness = ValueScaler.scale(currentHappiness, surplus, 0.1);
            }
            System.out.println("My new happiness is: " +currentHappiness);
            return currentHappiness;
    }

    @Override
    protected double updateLoyaltyAfterHunt(double foodHunted, double foodReceived)
    {
            //Loyalty after hunting refines from how much more happy you are after the hunt
            //and from comparing your economic (sharing of food) belief with the group's belief.
            if (this.getDataModel().getGroupId() != null)
            {
                //get change in economic beleifs
                double myEconomic = getDataModel().getEconomicBelief();
                double myGroupEconomic = getConn().getGroupById(getDataModel().getGroupId()).getCurrentEconomicPoisition();
                double deltaEconomic = Math.abs(myGroupEconomic - myEconomic);//how close are you to the group's belief
                
                
                //get change in happiness
                Double currentHappiness = getDataModel().getCurrentHappiness();
                if (currentHappiness == null)
                {
                    currentHappiness = 0.5 * myEconomic;
                }
                double newHappiness = updateHappinessAfterHunt(foodHunted, foodReceived);
                double deltaHappiness = newHappiness - currentHappiness;//how much or less happy did you get               
                
                
                //get new loyalty
                Double currentLoyalty = getDataModel().getCurrentLoyalty();
                if (currentLoyalty == null || currentLoyalty == 0)
                    //As this if statement implies either entry to your first group or
                    //entry to a new (but not necessarily your first) group then you're
                    //loyal to the average sense (not too much and no too little)
                    currentLoyalty = 0.5 * (currentHappiness + deltaEconomic);
                
                //copy over the initial (the current value) loyalty to update               
                double newLoyalty = currentLoyalty;
                
                if (deltaHappiness > 0)
                {
                    //you gain loyalty to your group
                    newLoyalty += ValueScaler.scale(deltaHappiness, deltaEconomic, 0.01);
                    if (newLoyalty >= 1)
                        return 1;
                    else
                        return newLoyalty;
                }
                
                if (deltaHappiness < 0)
                {
                    //you lose loyalty to your group
                    newLoyalty -= ValueScaler.scale(Math.abs(deltaHappiness), deltaEconomic, 0.01);
                    if (newLoyalty <= 0)
                        return 0.001;//reserve 'loyalty = 0' to agents belonging to no group
                    else
                        return newLoyalty;
                }
                
                //if you get here then you got what you wanted after the hunt, so, you increase your loyalty slightly
                newLoyalty += ValueScaler.scale(0, deltaEconomic, 0.01);
                if (newLoyalty >= 1)
                    return 1;
                else
                    return newLoyalty;    
            }               
            else
                return 0;//agent doesnt belong to a group and so is not loyal to anyone
            //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Map<String, Double> updateTrustAfterHunt(double foodHunted,
                                    double foodReceived)
    {
            Food lastHunted = this.getDataModel().getLastHunted();
            List<String> members = this.getDataModel().getHuntingTeam().getMembers();
            String opponentID;
            Map<String, Double> newTrustValue = new HashMap<String, Double>();
            double trust;

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
                trust = 0;
            }

            //If agent hunted stag then check what the opponent did. If betrayed decrease trust
            // otherwise increase it. If the agent hunted rabbit no change in trust
            if (lastHunted.getName().equals("Stag"))
            {
                    if (foodHunted == 0) //Agent has been betrayed
                    {
                        trust = ValueScaler.scale(trust, -1, 0.5);
                    }
                    else //Opponent cooperated
                    {
                        trust = ValueScaler.scale(trust, 1, 0.5);
                    }
            }
            else    //Agent hunted rabbit so no trust issues
            {
                trust = ValueScaler.scale(trust, 0, 0.5);
            }
            
            newTrustValue.put(opponentID, trust);
            return  newTrustValue;
    }

    @Override
    protected double updateLoyaltyAfterVotes(Proposition proposition, int votes,
                                    double overallMovement)
    {
            //Loyalty after a vote refines from how happy you are after the vote?
            if (this.getDataModel().getGroupId() != null)
                return updateHappinessAfterVotes(proposition, votes, overallMovement);
            else
                return 0;//agent doesnt belong to a group and so is not loyal to anyone
    }

    @Override
    protected double updateHappinessAfterVotes(Proposition proposition, int votes,
                                    double overallMovement)
    {
            double newHappiness;
            Double currentHappiness = getDataModel().getCurrentHappiness();            
            
            
            if (currentHappiness == null)
            {
                //By default we are all satisfied with the economic position
                //we start off in, unless you are always happy or just hate life
                currentHappiness = 0.5 * getDataModel().getEconomicBelief();
                newHappiness = currentHappiness;
            }
            else 
                newHappiness = currentHappiness;
                
            //If this concerns you...
            if (this.getDataModel().getGroupId().equals(proposition.getOwnerGroup()))
            {
                //If I won...
                if(votes > 0)
                {
                    //your happy your proposition was passed
                    newHappiness += ValueScaler.scale(overallMovement, votes, 0.01);
                    if (newHappiness >= 1)
                        return 1;
                    else
                        return newHappiness;
                }

                //If I lost...
                if (votes < 0)
                {
                    //your dissapointed your proposition didn't pass
                    newHappiness -= ValueScaler.scale(overallMovement, votes, 0.01);
                    if (newHappiness <= 0)
                        return 0;
                    else
                        return newHappiness;
                }
            }
            
            //If this proposition doesn't concern you or if nothing happened, no decision
            //was made, then you're not affected
            return newHappiness;
           
    }

    @Override
    protected Map<String, Double> updateTrustAfterVotes(Proposition proposition,
                                    int votes, double overallMovement)
    {
            Map<String, Double> newTrustValue = new HashMap<String, Double>();
            String proposer = proposition.getProposer();
            double proposerTrust;

            //Check if proposer is not this agent. There is no point in increasing (or decreasing)
            //the trust to yourself
            if (!this.getDataModel().getId().equals(proposer)){
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
                    //increase the trust for proposer according to the number of votes
                    proposerTrust += ValueScaler.scale(overallMovement, votes, 0.1);
                    newTrustValue.put(proposer, proposerTrust);
            }
            else
            {
                    //proposerTrust = 1; //trust for himself
                    newTrustValue = null;
            }

            

            return newTrustValue;
        }
    
        @Override
	protected double updateSocialBeliefAfterVotes(Proposition proposition, int votes, double overallMovement)
        {
            //return the current value, for now
            return this.getDataModel().getSocialBelief();
        }
	
        @Override
        protected double updateEconomicBeliefAfterVotes(Proposition proposition, int votes, double overallMovement)
        {
            //return the current value, for now
            return this.getDataModel().getEconomicBelief();
        }
    
        //An agent which has been invited to a group must be tagged in order to process the invitation later
	@Override
	protected void onInvite(String group)
	{
		invitationHolders.add(this.getId());
                this.invitationToGroup = group;

	}

}