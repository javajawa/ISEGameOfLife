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
import ise.gameoflife.groups.TestPoliticalGroup;
import ise.gameoflife.models.ValueScaler;
import ise.gameoflife.tokens.AgentType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.simpleframework.xml.Element;
import java.util.Random;
import ise.gameoflife.participants.AbstractGroupAgent;

/**
 *
 * @author george
 */
//Test class. I will copy strategies in Aadil's agent
public class TestPoliticalAgent extends AbstractAgent
{

	private static final long serialVersionUID = 1L;

	@Deprecated
	public TestPoliticalAgent()
	{
		super();
        }

    @Element
    private AgentType type;
    

    public TestPoliticalAgent(double initialFood, double consumption, AgentType type){
        super("<hunter>", 0, initialFood, consumption);
        this.type = type;

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
//        if (this.getDataModel().getGroupId() != null) return this.getDataModel().getGroupId();
//
//        String chosenGroup = "";
//
//        double currentHeuristic, previousHeuristic = 0;
//
//        //used for the socio-economic faction of heuristic
//        double vectorDistance;
//        double maxDistance = Math.sqrt(2);
//        double economic, social, esFaction;
//
//        //used for the trust faction of heuristic
//        double trustFaction, trustSum;
//        int numKnownTrustValues;
//
//        if (!getConn().availableGroups().isEmpty()) {
//            PublicGroupDataModel aGroup;
//
//            //Assess each group in turn
//            for (String groupID : getConn().availableGroups()) {
//                aGroup = getConn().getGroupById(groupID);
//
//                //Obtain how much trust there is between this agent and the members of the group
//                numKnownTrustValues = 0;
//                trustSum = 0;
//                for (String trustee : aGroup.getMemberList()) {
//                        Double trustValue = this.getDataModel().getTrust(trustee);
//
//                        if (trustValue != null) {
//                                trustSum += trustValue;
//                                numKnownTrustValues++;
//                        }
//                }
//                trustFaction = trustSum / numKnownTrustValues;
//
//                economic = aGroup.getCurrentEconomicPoisition() - this.getDataModel().getEconomicBelief();//change in X
//                social = aGroup.getEstimatedSocialLocation() - getDataModel().getSocialBelief();//change in Y
//                vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social, 2));
//                esFaction = 1 - (vectorDistance / maxDistance);
//
//                currentHeuristic = 0.5*trustFaction + 0.5*esFaction;
//
//                if (currentHeuristic > 0.5 && previousHeuristic < currentHeuristic) {
//                    chosenGroup = aGroup.getId();
//                    previousHeuristic = currentHeuristic;
//                }
//            }
//        }
//            if (chosenGroup.equals("")) {
//                String optimalGrouping = "";
//                //Obtain how much trust there is between this agent and the members of the group
//                for (String trustee : getConn().getUngroupedAgents()) {
//                        Double trustValue = this.getDataModel().getTrust(trustee);
//                        if (trustValue != null) {
//                            trustFaction = trustValue;
//
//                            //economic = getConn().getAgentById(trustee).getEconomicBelief() - getDataModel().getEconomicBelief();//change in X
//                            //social = getConn().getAgentById(trustee).getSocialBelief() - getDataModel().getSocialBelief();//change in Y
//
//                            //vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social, 2));
//                            //esFaction = 1 - (vectorDistance / maxDistance);
//                            esFaction = 0.5;
//                            currentHeuristic = 0.5*trustFaction + 0.5*esFaction;
//                            if (currentHeuristic > 0.5 && previousHeuristic < currentHeuristic) {
//                                optimalGrouping = trustee;
//                                previousHeuristic = currentHeuristic;
//                            }
//                        }
//                }
//                if (optimalGrouping.equals("")){
//                    return null;
//                }
//                else {
//                    GroupDataInitialiser myGroup = new GroupDataInitialiser(this.uniformRandLong(), (this.getDataModel().getEconomicBelief() + getConn().getAgentById(optimalGrouping).getEconomicBelief())/2);
//                    Class<? extends AbstractGroupAgent> gtype = getConn().getAllowedGroupTypes().get(0);
//                    chosenGroup = getConn().createGroup(gtype, myGroup);
//                }
//
//            }
//         return chosenGroup;
        return null;
    }
        



    @Override
    protected void groupApplicationResponse(boolean accepted) {
        throw new UnsupportedOperationException("Not supported yet.");
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

            switch (type)
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
            String groupId = this.getDataModel().getGroupId();
            String proposerGroup = p.getOwnerGroup();
            ProposalType agentProposal;

            if (groupId != null){ //check if is in a group
                    if (groupId.equals(proposerGroup)){ //check if agent is in the same group as the proposal
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
                                return VoteType.For;
                            }
                            else
                            {
                                return VoteType.Against;
                            }
                    }
                    else{ //must never happen!!
                        throw new UnsupportedOperationException("Agent cannot vote for other Groups ");
                    }
            }
            else //must never happen!!
            {
                return VoteType.Abstain;
            }
            //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Food giveAdvice(String agent, HuntingTeam agentsTeam)
    {
            // TODO Implement
            return null;
    }

    @Override
    protected double updateHappinessAfterHunt(double foodHunted,
                                    double foodReceived)
    {
            return 0; //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double updateLoyaltyAfterHunt(double foodHunted, double foodReceived)
    {
            return 0; //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Map<String, Double> updateTrustAfterHunt(double foodHunted,
                                    double foodReceived)
    {

            List<String> members = this.getDataModel().getHuntingTeam().getMembers();
            if (members.size() < 2) return null;

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
            
            if (this.getDataModel().getLastHunted().getName().equals("Stag"))
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
            return 0;
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
            return null;
            //throw new UnsupportedOperationException("Not supported yet.");
    }

}