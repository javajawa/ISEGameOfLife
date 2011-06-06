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
import ise.gameoflife.tokens.AgentType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.simpleframework.xml.Element;
import java.util.Random;

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
    
    @Element
    private double socialBelief;

    public TestPoliticalAgent(double initialFood, double consumption, AgentType type, double social){
        super("<hunter>", 0, initialFood, consumption);
        this.type = type;
        this.socialBelief = social;
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
        String chosenGroup = "";
        PublicGroupDataModel aGroup;
        
        double currentHeuristic, previousHeuristic = 0;
        
        //used for the socio-economic faction of heuristic
        double vectorDistance;
        double maxDistance = Math.sqrt(2);
        double economic, social, esFaction;
        
        //used for the trust faction of heuristic
        double trustFaction, trustSum;
        int numKnownTrustValues;
        
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
            trustFaction = trustSum / numKnownTrustValues;

            economic = aGroup.getCurrentEconomicPoisition() - this.getDataModel().getEconomicBelief();//change in X
            social = aGroup.getEstimatedSocialLocation() - this.socialBelief;//change in Y
            vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social, 2));
            esFaction = 1 - (vectorDistance / maxDistance);
            
            currentHeuristic = 0.5*trustFaction + 0.5*esFaction;
            
            if (currentHeuristic > 0.5 && previousHeuristic < currentHeuristic) {
                chosenGroup = aGroup.getId();
                previousHeuristic = currentHeuristic;
            }
        }
        
        if (chosenGroup.equals("")) {
            GroupDataInitialiser myGroup = new GroupDataInitialiser(0, this.getDataModel().getEconomicBelief());
            chosenGroup = getConn().createGroup(TestPoliticalGroup.class, myGroup);
        }
        return chosenGroup;
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
                    else
                    {
                            return ProposalType.moveLeft;
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
            // TODO: Implement
            return VoteType.For;
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
            if (members.size() == 1) return null;

            Map<String, Double> newTrustValue = new HashMap<String, Double>();
            double trust;

            if (this.getDataModel().getLastHunted().getName().equals("Stag"))
            {
                    if (foodHunted == 0) //Agent has been betrayed
                    {
                            trust = -1;
                    }
                    else
                    {
                            trust = 1;
                    }
            }
            else    //Agent hunted rabbit so no trust issues
            {
                    trust = 0;
            }

            if (members.get(0).equals(this.getId()))
            {
                    newTrustValue.put(members.get(1), trust);
            }
            else
            {
                    newTrustValue.put(members.get(0), trust);
            }

            return newTrustValue;
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