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
import ise.gameoflife.models.History;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.participants.PublicAgentDataModel;
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
	private History<Double> economicSatisfaction;
        private History<Double> socialSatisfaction;

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
        
        //compute current social satisfaction, based on your level of trust in the group
        
        //compare previous social satisfaction with current and update social belief accordingly
        
        //compute current economic satisfaction, based on happiness and loyalty in the group
        
        //compare previous economic satisfaction with current and update economic belief accordingly
        
        //combine social and economic satisfaction and decide if you're satisfied to be in the group
        
        
        
        
        
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
            Food suggestedFood, cooperateFood, defectFood, choice;

            String groupID = this.getDataModel().getGroupId();
            //If the agent belongs to a group can ask for advice
            if (groupID != null)
            {   
                //suggestedFood = this.askAdvice();
                //TODO: Make use of advice when choosing food. 
            }
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
            double MaxThreshold = 0.85;
            double MinThreshold = 0.15;
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
                opponentTrust = getDataModel().getTrust(opponentID);
            else
                return null;

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
            
            if (currentHappiness == null)
                //By default we are all satisfied with the economic position
                //we start off in, unless you are always happy or just hate life
                currentHappiness = 0.5 * getDataModel().getEconomicBelief();
            
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

            return currentHappiness;
    }

    @Override
    protected double updateLoyaltyAfterHunt(double foodHunted, double foodReceived)
    {
            //Loyalty after hunting refines from how much more happy you are after the hunt
            //and from comparing your economic (sharing of food) belief with the group's belief.
            if (this.getDataModel().getGroupId() != null)
            {
                //get change in economic beliefs
                double myEconomic = getDataModel().getEconomicBelief();
                double myGroupEconomic = getConn().getGroupById(getDataModel().getGroupId()).getCurrentEconomicPoisition();                
            
                //how close are you to the group's belief
                double deltaEconomic = Math.abs(myGroupEconomic - myEconomic);

                //get change in happiness
                Double oneTurnAgoHappiness = this.getDataModel().getHappinessHistory().getValue(1);
                //if there is no entry for happiness initialise it
                if (oneTurnAgoHappiness == null)
                {
                    oneTurnAgoHappiness = 0.5 * myEconomic;
                }
                //Calculate difference in happiness between the current and the previous round
                Double currentHappiness = getDataModel().getCurrentHappiness();
                double deltaHappiness =  currentHappiness - oneTurnAgoHappiness ;//how much or less happy did you get
                //get new loyalty
                Double currentLoyalty = getDataModel().getCurrentLoyalty();
                if (currentLoyalty == null || currentLoyalty == 0)
                    //As this if statement implies either entry to your first group or
                    //entry to a new (but not necessarily your first) group then you're
                    //loyal to the average sense (not too much and no too little)
                    currentLoyalty = 0.5 * (oneTurnAgoHappiness + deltaEconomic); 
                                      
                if (deltaHappiness > 0)
                {
                    //you gain loyalty to your group
                    currentLoyalty = ValueScaler.scale(currentLoyalty, deltaHappiness, 1 - deltaEconomic);
                }
                else if(deltaHappiness < 0)
                {
                    //you lose loyalty to your group
                    currentLoyalty = ValueScaler.scale(currentLoyalty, deltaHappiness, 1 - deltaEconomic);
                } else
                    currentLoyalty = ValueScaler.scale(currentLoyalty, 0, 1 - deltaEconomic);
                
                return currentLoyalty;
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
                trust = 0.1;
            }
            
            //If agent hunted stag then check what the opponent did. If betrayed decrease trust
            // otherwise increase it. If the agent hunted rabbit no change in trust
            if (lastHunted.getName().equals("Stag"))
            {
                    if (foodHunted == 0) //Agent has been betrayed
                    {
                        trust = ValueScaler.scale(trust, -1, 0.3);
                    }
                    else //Opponent cooperated
                    {
                        trust = ValueScaler.scale(trust, 1, 0.3);
                    }
            }
            else    //Agent hunted rabbit so no trust issues
            {
                trust = ValueScaler.scale(trust, 0, 0.3);
            }
            
            newTrustValue.put(opponentID, trust);
            return  newTrustValue;
    }

    @Override
    protected double updateLoyaltyAfterVotes(Proposition proposition, int votes,
                                    double overallMovement)
    {
            //Loyalty after voting refines from how much more happy you are after the vote
            //and from comparing your economic (decision to deviate from your belief) belief
            //with the group's belief.
            if (this.getDataModel().getGroupId() != null)
            {
                //get change in economic beleifs
                double myEconomic = getDataModel().getEconomicBelief();
                double myGroupEconomic = getConn().getGroupById(getDataModel().getGroupId()).getCurrentEconomicPoisition();
                double deltaEconomic = Math.abs(myGroupEconomic - myEconomic);//how close are you to the group's belief
                                
                //get change in happiness
                Double oneTurnAgoHappiness = getDataModel().getHappinessHistory().getValue(1);
                if (oneTurnAgoHappiness == null)
                {
                    oneTurnAgoHappiness = 0.5 * myEconomic;
                }
                double currentHappiness = getDataModel().getCurrentHappiness();
                double deltaHappiness = currentHappiness - oneTurnAgoHappiness;//how much or less happy did you get               
                                
                //get new loyalty
                Double currentLoyalty = getDataModel().getCurrentLoyalty();
                if (currentLoyalty == null || currentLoyalty == 0)
                    //As this if statement implies either entry to your first group or
                    //entry to a new (but not necessarily your first) group then you're
                    //loyal to the average sense (not too much and no too little)
                    currentLoyalty = 0.5 * (oneTurnAgoHappiness + deltaEconomic);           
                
               //If this concerns you...
                if (this.getDataModel().getGroupId().equals(proposition.getOwnerGroup()))
                {
                    if (deltaHappiness > 0)
                    {
                        //you gain loyalty to your group
                        currentLoyalty = ValueScaler.scale(currentLoyalty, deltaHappiness, 1- deltaEconomic);
                    }
                    else if(deltaHappiness < 0)
                    {
                        //you lose loyalty to your group
                        currentLoyalty = ValueScaler.scale(currentLoyalty, deltaHappiness, 1- deltaEconomic);
                    } else
                        currentLoyalty = ValueScaler.scale(currentLoyalty, 0, 1 - deltaEconomic);
                }
                return currentLoyalty;                                      
            }               
            else
                return 0;//agent doesnt belong to a group and so is not loyal to anyone
    }

    @Override
    protected double updateHappinessAfterVotes(Proposition proposition, int votes,
                                    double overallMovement)
    {
            Double currentHappiness = getDataModel().getCurrentHappiness();            

            if (currentHappiness == null)
            {
                //By default we are all satisfied with the economic position
                //we start off in, unless you are always happy or just hate life
                currentHappiness = 0.5 * getDataModel().getEconomicBelief();
            }

            //If this concerns you...
            if (this.getDataModel().getGroupId().equals(proposition.getOwnerGroup()))
            {
                if (votes > 0)
                {
                    //your happy your proposition was passed
                    currentHappiness = ValueScaler.scale(currentHappiness, votes, Math.abs(overallMovement));
                }
                else if(votes < 0)
                {
                    //your dissapointed your proposition didn't pass
                    currentHappiness = ValueScaler.scale(currentHappiness, votes, Math.abs(overallMovement));
                }
                else
                    //votes = 0
                    currentHappiness = ValueScaler.scale(currentHappiness, 0, Math.abs(overallMovement));
             }

            return currentHappiness;
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
            if (!this.getDataModel().getId().equals(proposer))
            {
                if (this.getDataModel().getTrust(proposer) != null)
                {
                       proposerTrust = this.getDataModel().getTrust(proposer); //get current trust of proposer
                }
                else
                {
                       proposerTrust = 0;
                }
                //increase the trust for proposer according to the number of votes
                proposerTrust = ValueScaler.scale(proposerTrust, votes, Math.abs(overallMovement));
                newTrustValue.put(proposer, proposerTrust);
             }
             else
             {
                    newTrustValue = null;
             }
            return newTrustValue;
        }
    
        @Override
	protected double updateSocialBeliefAfterVotes(Proposition proposition, int votes, double overallMovement)
        {System.out.println(overallMovement);
            double currentSocial = getDataModel().getSocialBelief();
            //Your social belief refines from how much more/less trust there is in the group
            //after the vote. Whether or not your proposition passed reflects how much you
            //want to trust the group to make decisions or a single dictator to make decisions.
            if (this.getDataModel().getGroupId() != null)
            {                                                                                             
               //If this concerns you...
                if (this.getDataModel().getGroupId().equals(proposition.getOwnerGroup()))
                {
                    char position;
                    double groupSocial = getConn().getGroupById(getDataModel().getGroupId()).getEstimatedSocialLocation();
                    double deltaSocial = groupSocial - currentSocial;//how close are you to the group's belief
                    
                    if (currentSocial < groupSocial)                    
                        //your belief is more authoritorian
                        position = 'a';//authoritorian
                    else if (currentSocial > groupSocial)                  
                        //your belief is more libertarian
                        position = 'l';//libertarian                    
                    else
                        //your belief equates to group belief
                        position = 'c';//center
                                        
                    if (votes > 0)
                    {   //you're social belief moves towards the group social poistion
                        switch (position)
                        {
                            case 'a':
                                //move more libertarian
                                currentSocial = ValueScaler.scale(currentSocial, deltaSocial, Math.abs(overallMovement));
                                if (currentSocial > groupSocial)
                                    currentSocial = groupSocial;
                                break;
                            case 'l':
                                //move more authoritorian
                                currentSocial = ValueScaler.scale(currentSocial, deltaSocial, Math.abs(overallMovement));
                                if (currentSocial < groupSocial)
                                    currentSocial = groupSocial;
                                break;
                            case 'c':
                                //stay
                                currentSocial = groupSocial;
                                break;
                            default :
                                throw new IllegalStateException("Agent won but social belief not recognised");
                        }
                    }
                    else if (votes < 0)
                    {                     
                        //you're economic belief moves away from the group economic position
                        switch (position)
                        {
                            case 'a':
                                //move more authoriatorian
                                currentSocial = ValueScaler.scale(currentSocial, -deltaSocial, Math.abs(overallMovement));
                                break;
                            case 'l':
                                //move more libertarian
                                currentSocial = ValueScaler.scale(currentSocial, -deltaSocial, Math.abs(overallMovement));
                                break;
                            case 'c':
                                //any direction, for now
                                boolean random = uniformRandBoolean();
                                if (random)
                                    currentSocial = ValueScaler.scale(currentSocial, 0.05, Math.abs(overallMovement));
                                else
                                    currentSocial = ValueScaler.scale(currentSocial, -0.05, Math.abs(overallMovement));
                                break;
                            default :
                                throw new IllegalStateException("Agent lost but social belief not recognised");
                        }
                    }
                    //otherwise your social belief remains the same
                }
                return currentSocial;                                      
            }               
            else
                return currentSocial;//agent doesnt belong to a group and does not vote
        }
	
        @Override
        protected double updateEconomicBeliefAfterVotes(Proposition proposition, int votes, double overallMovement)
        {
            double currentEconomic = getDataModel().getEconomicBelief();
            //Your economic belief refines from how much more/less happy you are after the vote
            //and from how loyal you are after the group made their decision after the vote.
            if (this.getDataModel().getGroupId() != null)
            {
               //If this concerns you...
                if (this.getDataModel().getGroupId().equals(proposition.getOwnerGroup()))
                {
                    char position;
                    double groupEconomic = getConn().getGroupById(getDataModel().getGroupId()).getCurrentEconomicPoisition();
                    double deltaEconomic = groupEconomic - currentEconomic;//how close are you to the group's belief

                    if (currentEconomic < groupEconomic)
                        //your belief is more right
                        position = 'r';//right
                    else if (currentEconomic > groupEconomic)
                        //your belief is more left
                        position = 'l';//left
                    else
                        //your belief equates to group belief
                        position = 'c';//center

                    if (moreLoyal() && moreHappy())
                    {   //you're economic belief moves towards the group economic poistion
                        switch (position)
                        {
                            case 'r':
                                //move more left
                                currentEconomic = ValueScaler.scale(currentEconomic, deltaEconomic, Math.abs(overallMovement));
                                if (currentEconomic > groupEconomic)
                                    currentEconomic = groupEconomic;
                                break;
                            case 'l':
                                //move more right
                                currentEconomic = ValueScaler.scale(currentEconomic, deltaEconomic, Math.abs(overallMovement));
                                if (currentEconomic < groupEconomic)
                                    currentEconomic = groupEconomic;
                                break;
                            case 'c':
                                //stay
                                currentEconomic = groupEconomic;
                                break;
                            default :
                                throw new IllegalStateException("Agent happy but economic belief not recognised");
                        }
                    }
                    else
                    {
                        //you're economic belief moves away from the group economic position
                        switch (position)
                        {
                            case 'r':
                                //move more right
                                currentEconomic = ValueScaler.scale(currentEconomic, -deltaEconomic, Math.abs(overallMovement));
                                break;
                            case 'l':
                                //move mor left
                                currentEconomic = ValueScaler.scale(currentEconomic, -deltaEconomic, Math.abs(overallMovement));
                                break;
                            case 'c':
                                //any direction, for now
                                boolean random = uniformRandBoolean();
                                if (random)
                                    currentEconomic = ValueScaler.scale(currentEconomic, 0.05, Math.abs(overallMovement));
                                else
                                    currentEconomic = ValueScaler.scale(currentEconomic, -0.05, Math.abs(overallMovement));
                                break;
                            default :
                                throw new IllegalStateException("Agent unhappy but economic belief not recognised");
                        }
                    }
                }
                return currentEconomic;
            }
            else
                return currentEconomic;//agent doesnt belong to a group and so is not loyal to anyone
        }
    
        //An agent which has been invited to a group must be tagged in order to process the invitation later
	@Override
	protected void onInvite(String group)
	{
		invitationHolders.add(this.getId());
                this.invitationToGroup = group;

	}


        private Food askAdvice() {
            Food suggestedFood = null;
            String opponentID = null;

            //Get the members of the hunting team that this agent belongs to
            List<String> members = this.getDataModel().getHuntingTeam().getMembers();

            //If the agent has no pair then no advice
            if (members.size() == 1) return null;

            //Find opponent's ID
            if (members.get(0).equals(this.getId()))
            {
                    if (getConn().getAgentById(members.get(1)) != null)
                    {
                        opponentID = members.get(1);
                    }
            }
            else
            {
                    if (getConn().getAgentById(members.get(0)) != null)
                    {
                        opponentID = members.get(0);
                    }
            }

            //Get the hunting teams history of the opponent. Get the last hunting team of the opponent
            //and find out which agent was its opponent at that time. This agent has the latest information
            //about our opponent. Therefore this agent is the advisor.
            if (opponentID != null)
            {
                HuntingTeam opponentPreviousTeam = getConn().getAgentById(opponentID).getTeamHistory().getValue(1);
                if (opponentPreviousTeam != null)
                {
                    for (String agent: opponentPreviousTeam.getMembers())
                    {
                        if (!agent.equals(opponentID)&&!agent.equals(this.getId()))
                        {
                            return suggestedFood = seekAvice(agent);
                        }
                    }
                }
            }
            
            return suggestedFood;
        }   
        
        private boolean moreLoyal() {
            if (this.getDataModel().getGroupId() != null)
            {
                //get change in economic beliefs
                double myEconomic = getDataModel().getEconomicBelief();
                double myGroupEconomic = getConn().getGroupById(getDataModel().getGroupId()).getCurrentEconomicPoisition();
                double deltaEconomic = Math.abs(myGroupEconomic - myEconomic);//how close are you to the group's belief

                Double oneTurnAgoHappiness = getDataModel().getHappinessHistory().getValue(1);
                if (oneTurnAgoHappiness == null)
                {
                    oneTurnAgoHappiness = 0.5 * myEconomic;
                }

                Double curretnHappiness = getDataModel().getCurrentHappiness();
                if (curretnHappiness == null)
                {
                    curretnHappiness = 0.5 * myEconomic;
                }

                //get your loyalty and loyalty history
                Double oneTurnAgoLoyalty = getDataModel().getLoyaltyHistory().getValue(1);
                if (oneTurnAgoLoyalty == null)
                {
                    oneTurnAgoLoyalty = 0.5 * (oneTurnAgoHappiness * deltaEconomic);
                }

                Double currentLoyalty = getDataModel().getCurrentLoyalty();
                if (currentLoyalty == null)
                {
                    currentLoyalty = 0.5 * (curretnHappiness * deltaEconomic);
                }

                double deltaLoyalty = currentLoyalty - oneTurnAgoLoyalty;//how much or less loyal did you get

                if (deltaLoyalty > 0)
                {
                    //you became more loyal to the group
                    return true;
                }
                else if(deltaLoyalty < 0)
                {
                    //you became less loyal to the group
                    return false;
                } else
                    //you just got in the group and for that you must be loyal to them, at the least
                    return true;
            }
            else
                //not loyal to anyone
                return false;
        }

        private boolean moreHappy() {
                //get change in economic beleifs
                double myEconomic = getDataModel().getEconomicBelief();

                //get your loyalty and loyalty history
                Double oneTurnAgoHappiness = getDataModel().getHappinessHistory().getValue(1);
                if (oneTurnAgoHappiness == null)
                {
                    oneTurnAgoHappiness = 0.5 * myEconomic;
                }

                Double currentHappiness = getDataModel().getCurrentHappiness();
                if (currentHappiness == null)
                {
                    currentHappiness = 0.5 * myEconomic;
                }

                double deltaHappiness = currentHappiness - oneTurnAgoHappiness;//how much or less loyal did you get

                if (deltaHappiness > 0)
                {
                    //you became more loyal to the group
                    return true;
                }
                else if(deltaHappiness < 0)
                {
                    //you became less loyal to the group
                    return false;
                } else
                    //you're not overjoyed but you're satisfied
                    return true;                
        }
}
