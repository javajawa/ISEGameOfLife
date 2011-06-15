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
import ise.gameoflife.participants.PublicAgentDataModel;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.ListIterator;
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
        private final static HashMap<String, String> groupFounders = new HashMap<String, String>();
        private final static TreeSet<String> membersToKickOut = new TreeSet<String>();        
        private final static TreeSet<String> freeToGroup = new TreeSet<String>();                
	private History<Double> satisfaction = new History<Double>(1);

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
        freeToGroup.add(this.getId());
    }

    @Override
    protected void beforeNewRound() {
         //Do nothing
    }
    /**
    * This method assesses an agent's satisfaction in the group. If the agent is satisfied remains in the group
    * otherwise it will request to leave the group.
    * @param none
    * @return Satisfied or not?
    */
    protected boolean SatisfiedInGroup() {
        if (satisfaction.isEmpty())
            satisfaction.newEntry(0.0);
        
        //get the previous satisfaction value
        Double previousSatisfaction = satisfaction.getValue();        

        //compute current satisfaction, based on your socio economic vector distance with the group        
        double myEconomic = getDataModel().getEconomicBelief();
        double mySocial = getDataModel().getSocialBelief();
        double groupEconomic = getConn().getGroupById(getDataModel().getGroupId()).getCurrentEconomicPoisition();
        double groupSocial = getConn().getGroupById(getDataModel().getGroupId()).getEstimatedSocialLocation();

        double deltaEconomic = groupEconomic - myEconomic;//change in X
        double deltaSocial = groupSocial - mySocial;//change in Y
        Double currentSatisfaction = Math.sqrt(Math.pow(deltaEconomic, 2) + Math.pow(deltaSocial, 2));
        
        //store it in the history
        satisfaction.newEntry(currentSatisfaction); 
        
        //compare with previous satisfaction and find out if agent should stay in the group        
        Double deltaSatisfaction = currentSatisfaction - previousSatisfaction;

        if (deltaSatisfaction >= 0)//you're growing more satisfied, so stay
        {
            return true;
        }
        else if (currentSatisfaction > 0.6)//if strictly greater than the weighting of 'esFaction' in the grouping heuristic
        {
                //you're very far apart on the political compass and you're not satisfied, so give up and leave
                return false;
        }
        else if (deltaSatisfaction < -0.2)
        {
                    //you may be quite close on the political compass, but you were very unsatisfied in the last turn,
                    //so give up and leave
                    return false;
        }
        else
        {
                    //you're close on the political compass and you were unsatisfied in the last turn, but, you're
                    //willing to give the group another shot
                    return true;
        }             
    }
    
    /**
     * This procedure is primarily used right before an agent issues the choice to leave the group it
     * is currently in. If the group it is leaving had only one other member then that member is stored
     * in a dedicated tree structure 'membersToKickOut' so that it is known that this member will have
     * to leave in the next turn. This is necessary as you cannot have a group characterised by only one member.
     * @param none
     * @return none
    */   
    protected void checkToEvict() {
        PublicGroupDataModel myGroup = getConn().getGroupById(getDataModel().getGroupId());
        
        //used when agent is about to issue the command to leave the group of only himself and another,
        //it searches for the other agent and tells it leave the group as well
        if(myGroup.getMemberList().size() == 2)
        {
             for(String member : myGroup.getMemberList())
             {
                //look for the other member of this group to kick out into free agent mode
                if(!member.equals(getDataModel().getId()))
                    membersToKickOut.add(member);                 
             }
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
        String chosenGroup = "";

        //If agent is already member of a group remove it from the founders or invitation holders lists
        //and check if it is satisfied. If not return leaveGroup request

        logger.log(Level.FINE, "---------------------------------");
        for (String groupID : getConn().availableGroups())
        {
            int size = getConn().getGroupById(groupID).getMemberList().size();
            logger.log(Level.FINE, "{0} with size: {1}", new Object[]{getConn().getGroupById(groupID).getName(), size});
            for (String a: getConn().getGroupById(groupID).getMemberList())
            {
                logger.log(Level.FINE, "    {0}", getConn().getAgentById(a).getName());
            }
        }        
        
 
        System.out.println("-------------START-GROUP---------------------------");        
        for (String groupID : getConn().availableGroups())
        {
            int size = getConn().getGroupById(groupID).getMemberList().size();
            System.out.println(getConn().getGroupById(groupID).getName() +" with size: " +size );
            for (String a: getConn().getGroupById(groupID).getMemberList())
            {
                System.out.println("    "+getConn().getAgentById(a).getName());
            }
        }
        System.out.println("--------------END-GROUP---------------------------");
        System.out.println();
        System.out.println();



        if (this.getDataModel().getGroupId() != null)
        {
            if (groupFounders.containsKey(this.getId()))
            {
                    groupFounders.remove(this.getId());
            }
            if (invitationHolders.contains(this.getId()))
            {                
                    invitationHolders.remove(this.getId());
                    invitationToGroup = null;
            }
            if (membersToKickOut.contains(this.getId()))
            {
                    membersToKickOut.remove(this.getId());
                    return leaveGroup;
            }            
            
            if (SatisfiedInGroup())
            { 
                return null;
            }
            else
            {
                checkToEvict();
                return leaveGroup;
            }
        }
        else if(this.invitationToGroup != null && invitationHolders.contains(this.getId())) //If this agent has a pending invitation to a group, return the invitation
        {
            return invitationToGroup; //founderInviteeGrouping();
        }
        else if(!getConn().availableGroups().isEmpty()) //If none of the above worked out then first try to find an optimal group to join with
        {
            chosenGroup = agentGroupGrouping();
        }

        //And if the above line didn't work then try to group with other free agents
        if (chosenGroup.equals(""))
        {
           chosenGroup = freeAgentsGrouping();
        }
        return chosenGroup;
    }

    /**
    * This method enables agents to check their compatibility to already existing groups
    * @param none
    * @return The group ID that this agent has chosen to join. If null no group is chosen.
    */
    private String agentGroupGrouping() {
        String chosenGroup = "";
        double maxDistance = Math.sqrt(2);
        List< Tuple<String, Double> > partnershipCandidates = new LinkedList< Tuple<String, Double> >();
        
        //Assess each group in turn
        for (String groupID: getConn().availableGroups())
        {
            if (getConn().getGroupById(groupID).getMemberList().size() > 1)
            {
                int numKnownTrustValues = 0;
                double trustSum = 0;

                for (String trustee : getConn().getGroupById(groupID).getMemberList())
                {
                    //Obtain how much trust there is between this agent and the members of the group
                    if (getDataModel().getTrust(trustee) != null)
                    {
                        trustSum += getDataModel().getTrust(trustee);
                        numKnownTrustValues++;
                    }
                }
                //calculates the vector distance between agent's and group's beliefs
                double economic = getConn().getGroupById(groupID).getCurrentEconomicPoisition() - getDataModel().getEconomicBelief();//change in X
                double social = getConn().getGroupById(groupID).getEstimatedSocialLocation() - getDataModel().getSocialBelief();//change in Y
                double vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social, 2));

                //The longer the distance the lower the esFaction is. Therefore, agents close to group's beliefs have
                //higher probability of joining this group
                double esFaction = 1 - (vectorDistance / maxDistance);

                Tuple<String, Double> tuple;
                double heuristicValue;
                if (numKnownTrustValues != 0)
                {
                    //The actual heuristic value is calculated. The politics is more important for compatibility than
                    //trust when a free agent tries to enter a group
                    heuristicValue = 0.3*(trustSum/numKnownTrustValues) + 0.7*esFaction;
                }
                else
                {
                    heuristicValue = 0.7*esFaction;                
                }
                tuple = new Tuple<String , Double>(groupID, heuristicValue);
                partnershipCandidates.add(tuple);                 
            }                       
        }

        //We sort candidate groups in descending order based on the heuristic value
        Collections.sort(partnershipCandidates, c);

        //Then simply check if the top candidate has a heuristic evaluation above a certain threshold
        //If top candidate has a value less than the threshold no need to check anyone else since
        //they are in descending order.
        if (!partnershipCandidates.isEmpty())
        { 
            double topCandidateHeuristicValue = partnershipCandidates.get(0).getValue();

            //If top candidate has evaluation above the threshold then choose that group
            if (topCandidateHeuristicValue > 0.6)
            { 
                chosenGroup = partnershipCandidates.get(0).getKey();
                return chosenGroup;
            }
        }
        return chosenGroup;
    }
    
    /**
    * This method enables agents to check their compatibility with other free agents
    * @param none
    * @return The group ID that this agent has chosen to join. If null no group is chosen.
    */
    private String freeAgentsGrouping() {
        String chosenGroup = null;
        double maxDistance = Math.sqrt(2);
        List< Tuple<String, Double> > partnershipCandidates = new LinkedList< Tuple<String, Double> >();
        List< Tuple<String, Double> > partnershipESFactions = new LinkedList< Tuple<String, Double> >();        

        //Iterate over the set of free agents
        for (String trustee : getConn().getUngroupedAgents())
        {
            //if an agent is not comparing with itself and has not been invited or has not formed a group already 
            if ((!this.getId().equals(trustee))&&(!invitationHolders.contains(trustee))&&(!groupFounders.containsKey(trustee)))
            {
                Double trustValue = this.getDataModel().getTrust(trustee);

                //Calculate the vector distance between these two agents socio-economic beliefs
                double economic = getConn().getAgentById(trustee).getEconomicBelief() - getDataModel().getEconomicBelief();//change in X
                double social = getConn().getAgentById(trustee).getSocialBelief() - getDataModel().getSocialBelief();//change in Y
                double vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social, 2));

                //The longer the distance the lower the esFaction is. Therefore, agents close to group's beliefs have
                //higher probability of joining this group
                double esFaction = 1 - (vectorDistance / maxDistance);                             

                Tuple<String, Double> tuple;
                double heuristicValue;
                if (trustValue != null)
                {
                    //The actual heuristic value is calculated. Trust is more important for compatibility than the politics
                    //when free agents try to group with each other
                    heuristicValue = 0.7*trustValue + 0.3*esFaction;
                }
                else
                {
                    heuristicValue = 0.3*esFaction;                
                }
                tuple = new Tuple<String , Double>(trustee, heuristicValue);
                partnershipCandidates.add(tuple);
                
                Tuple<String, Double> esTuple = new Tuple<String, Double>(trustee, esFaction);
                partnershipESFactions.add(esTuple);
            }
        }
 
        //We sort candidate groups in descending order based on the heuristic value
        Collections.sort(partnershipCandidates, c);

        //Then simply check if the top candidate has a heuristic evaluation above a certain threshold
        //If top candidate has a value less than the threshold no need to check anyone else since
        //they are in descending order.
        if (!partnershipCandidates.isEmpty())
        { 
            double topCandidateHeuristicValue = partnershipCandidates.get(0).getValue();

            //If top candidate has evaluation above the threshold then choose that group
            if (topCandidateHeuristicValue > 0.6)
            {
                String invitee = partnershipCandidates.get(0).getKey();
                
                ListIterator itr = partnershipESFactions.listIterator();
                Double topCandidateESFaction = null;
                while(itr.hasNext())
                {
                    Tuple<String, Double> listTuple = (Tuple<String, Double>) itr.next();                    
                    if(listTuple.getKey().equals(invitee))
                        topCandidateESFaction = listTuple.getValue();
                }
                
                if (inviteeAccepts(invitee, topCandidateESFaction))
                {
                    //Create a new group and invite your partner to join it
                    GroupDataInitialiser myGroup = new GroupDataInitialiser(this.uniformRandLong(), (this.getDataModel().getEconomicBelief() + getConn().getAgentById(invitee).getEconomicBelief())/2);
                    Class<? extends AbstractGroupAgent> gtype = getConn().getAllowedGroupTypes().get(0);
                    chosenGroup = getConn().createGroup(gtype, myGroup, invitee);
                    groupFounders.put(this.getId(), chosenGroup);
                }
            }
        }
        return chosenGroup;
    }

    /**
    * This method enables agents who received an invitation to check if they want to accept that invitation
    * @param none
    * @return The group ID held in the invitation. If null, the agent has rejected the invitation.
    */
    private boolean inviteeAccepts(String invitee, Double esFaction) {
        
        //Retieve the trust value between founder and invitee
        Double trustValue =  getConn().getAgentById(invitee).getTrust(getDataModel().getId());

        double heuristicValue;
        if (trustValue != null)  
        {
            //The actual heuristic value is calculated. Trust is more important for compatibility than the politics
            //when free agents try to group with each other
            heuristicValue = 0.7*trustValue + 0.3*esFaction;
        }
        else
        {
            heuristicValue = 0.3*esFaction;                                   
        }
            
        System.out.println("-------------START-INVITATION-ASSESSMENT--------------------");
        System.out.println("My name is " + getConn().getAgentById(invitee).getName());
        System.out.println("I was invited by " + getDataModel().getName());
        System.out.println("My heuristic is " + heuristicValue);
        System.out.println("--------------END-INVITATION-ASSESSMENT------------------");
        System.out.println();
        System.out.println();
        
        
        if (heuristicValue > 0.6)
        {
            //you accept the invitation
            invitationHolders.add(invitee);            
            return true;   
        }
        else
        {
            //you reject the invitation
            return false;
        }                               
    }
    
    private String getFounderOfGroup()
    {
        String founder = "";
        if(!groupFounders.isEmpty())
        {                                          
            for(String agent : groupFounders.keySet())
            {                          
                if(groupFounders.get(agent).equals(invitationToGroup))
                {
                    founder = agent;
                }
            }
        }        
        return founder;
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

    /**
    * This method allows an agent to cast its vote about a proposal. The decision depends
    * on the economic belief of this agent.
    * @param p The proposition
    * @return The type of the vote. Two types {For, Against}
    */
    @Override
    protected VoteType castVote(Proposition p)
    {
            String groupId = this.getDataModel().getGroupId();
            String proposerGroup = p.getOwnerGroup();
            ProposalType agentProposal;
            VoteType vote = null;

            if (groupId != null && getConn().getGroupById(groupId).getMemberList().size() > 1)//check if is in a group
            { 
                    if (groupId.equals(proposerGroup))  //check if agent is in the same group as the proposal
                    {
                            double groupEconomicPosition = this.getConn().getGroupById(groupId).getCurrentEconomicPoisition();
                            double agentEconomicBelief = this.getDataModel().getEconomicBelief();

                            //What this agent would propose...
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

                            //Compare agent's possible proposal to the actual proposal.
                            //If they agree obviously the agent will vote for.
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
                        vote =  VoteType.Abstain;
                    }
            }
            else //must never happen!!
            {
                vote =  VoteType.Abstain;
            }
            return vote;
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
            //Loyalty after hunting refines from how much more happy you are after the hunt
            //and from comparing your economic (sharing of food) belief with the group's belief.
            String groupId = getDataModel().getGroupId();
            if (groupId != null  && getConn().getGroupById(groupId).getMemberList().size() > 1)
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

                //As this if statement implies either entry to your first group or
                //entry to a new (but not necessarily your first) group then you're
                //loyal to the average sense (not too much and no too little)
                if (currentLoyalty == null || currentLoyalty == 0)
                {
                    currentLoyalty = 0.5 * (oneTurnAgoHappiness + deltaEconomic); 
                }

                //If deltaHappiness is < 0 you lose loyalty to the group. Otherwise if deltaHappiness is >0
                //you gain loyalty. If deltaHappiness is zero you don't change your loyalty
                currentLoyalty = scale(currentLoyalty, deltaHappiness, deltaEconomic);
                
                return currentLoyalty;
            }
            else //agent doesnt belong to a group and so is not loyal to anyone
            {
                return 0;
            }
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
            //Loyalty after voting refines from how much more happy you are after the vote
            //and from comparing your economic (decision to deviate from your belief) belief
            //with the group's belief.
            String groupId = getDataModel().getGroupId();
            if (groupId != null  && getConn().getGroupById(groupId).getMemberList().size() > 1)
            {
                //get change in economic beliefs
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
                        //If deltaHappiness <0 you lose loyalty to your group
                        //If deltaHappiness >0 you increase your loyalty to your group
                        //If deltaHappiness = 0 you don't change at all
                        currentLoyalty = scale(currentLoyalty, deltaHappiness, deltaEconomic);
                }
                return currentLoyalty;                                      
            }               
            else
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


            if (currentHappiness == null)
            {
                //By default we are all satisfied with the economic position
                //we start off in, unless you are always happy or just hate life
                currentHappiness = 0.5 * getDataModel().getEconomicBelief();
            }

            //If this concerns you...
            if (this.getDataModel().getGroupId().equals(proposition.getOwnerGroup()))
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
                       proposerTrust = 0;
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
            double currentSocial = getDataModel().getSocialBelief();
            //Your social belief refines from how much more/less trust there is in the group
            //after the vote. Whether or not your proposition passed reflects how much you
            //want to trust the group to make decisions or a single dictator to make decisions.
            String groupId = getDataModel().getGroupId();
            if ((groupId != null)  && (getConn().getGroupById(groupId).getMemberList().size() > 1))
            {
               //If this concerns you...
                if (this.getDataModel().getGroupId().equals(proposition.getOwnerGroup()))
                {
                    double groupSocial = getConn().getGroupById(getDataModel().getGroupId()).getEstimatedSocialLocation();
                    double deltaSocial = groupSocial - currentSocial;//how close are you to the group's belief
                                        

                    if (votes > 0)
                    {   //you're social belief moves towards the group's social posistion
                        currentSocial = scale(currentSocial, deltaSocial*10, Math.abs(overallMovement));
                    }
                    else if (votes < 0)

                    {
                        //you're social belief moves away from the group's social posistion
                        currentSocial = scale(currentSocial, -deltaSocial*10, Math.abs(overallMovement));

                    }
                    //otherwise your social belief remains the same
                }
                return currentSocial;
            }
            else
            {
                return currentSocial;//agent doesnt belong to a group and does not vote
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
            double currentEconomic = getDataModel().getEconomicBelief();
            //Your economic belief refines from how much more/less happy you are after the vote
            //and from how loyal you are after the group made their decision after the vote.
            String groupId = getDataModel().getGroupId();
            if ((groupId != null)  && (getConn().getGroupById(groupId).getMemberList().size() > 1))
            {
               //If this concerns you...
                if (this.getDataModel().getGroupId().equals(proposition.getOwnerGroup()))
                {
                    double groupEconomic = getConn().getGroupById(getDataModel().getGroupId()).getCurrentEconomicPoisition();
                    double deltaEconomic = groupEconomic - currentEconomic;//how close are you to the group's belief

                    if (moreLoyal() && moreHappy())

                    {
                        currentEconomic = scale(currentEconomic, deltaEconomic*10, Math.abs(overallMovement));

                    }
                    else
                    {
                        if (deltaEconomic != 0)//if your beliefs are NOT the same as the group's beliefs
                        {
                            currentEconomic = scale(currentEconomic, -deltaEconomic*10, Math.abs(overallMovement));
                        }
                        else //if your beliefs are exactly the same with the group's beliefs
                        {
                            //move in any direction, for now
                            boolean random = uniformRandBoolean();
                            if (random)
                                currentEconomic = scale(currentEconomic, 1, Math.abs(overallMovement));
                            else
                                currentEconomic = scale(currentEconomic, -1, Math.abs(overallMovement));
                        }
                    }
                }
                return currentEconomic;
            }
            else
                return currentEconomic;//agent doesnt belong to a group and so is not loyal to anyone
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
        Food suggestedFood = null;
        String opponentID = null;
        
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

    /**
    * This method checks if the agent has become more loyal since last round
    * @param none
    * @return True for becoming more loyal and false otherwise
    */
    private boolean moreLoyal() {
        String groupId = getDataModel().getGroupId();
        if (groupId != null  && getConn().getGroupById(groupId).getMemberList().size() > 1)
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
    
    /**
    * This method checks if the agent has become happier since last round
    * @param none
    * @return True for becoming happier and false otherwise
    */
    private boolean moreHappy() {
            //get change in economic beliefs
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

    /**
    * This is a helper method and distinguishes what is the food type for cooperation and defection
    * @param none
    * @return A list containing the food for cooperation and defrction
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

        private void ratePanel(){
            AgentType groupStrategy = getConn().getGroupById(getDataModel().getGroupId()).getGroupStrategy();
            System.out.println(groupStrategy);
        }
}
