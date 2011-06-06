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
import ise.gameoflife.tokens.AgentType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.simpleframework.xml.Element;
import java.util.Random;
/**
 *
 * @author george
 */

//Test class. I will copy strategies in Aadil's agent
public class TestPoliticalAgent extends AbstractAgent{

    private static final long serialVersionUID = 1L;

    @Deprecated
    public TestPoliticalAgent(){
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
        return null;
    }

    @Override
    protected void groupApplicationResponse(boolean accepted) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Food chooseFood() {
        //We assume there will be only two food sources (stags/rabbits)
        Food[] foodArray = new Food[2];
        Food cooperateFood, defectFood, choice;
        int i = 0;

        //Stores the two sources in an array
        for (Food noms : getConn().availableFoods()){
            foodArray[i] = noms;
            i++;
        }

        //Hunting a stag is equivalent to cooperation. Hunting rabbit is equivalent to defection
        if (foodArray[0].getNutrition() > foodArray[1].getNutrition()){
            cooperateFood = foodArray[0];
            defectFood = foodArray[1];
        }
        else{
            cooperateFood = foodArray[1];
            defectFood = foodArray[0];
        }

        switch (type){
            //The choice is always to hunt stags
            case AC:
                choice = cooperateFood;
                break;
            //The choice is always to hunt rabbits
            case AD:
                choice = defectFood;
                break;
            //If first time cooperate else imitate what your partner (opponent?) choose the previous time
            case R:
                Random random = new Random();
                if (random.nextInt(2) == 0) //if zero then cooperate
                    choice = cooperateFood;
                else                        //else if one then defect
                    choice = defectFood;
                break;
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
                if (members.get(0).equals(this.getId())){
                    if (getConn().getAgentById(members.get(1)).getHuntingHistory().size() != 1){
                        opponentPreviousChoice = getConn().getAgentById(members.get(1)).getHuntingHistory().getValue(1);
                    }
                }
                else{
                    if (getConn().getAgentById(members.get(0)).getHuntingHistory().size() != 1){
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
    protected ProposalType makeProposal() {
		// TODO: Implement
		return ProposalType.staySame;
		//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected VoteType castVote(Proposition p) {
		// TODO: Implement
		return VoteType.For;
		//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Food giveAdvice(String agent, HuntingTeam agentsTeam) {
		// TODO Implement
		return null;
    }

    @Override
    protected double updateHappinessAfterHunt(double foodHunted, double foodReceived) {
		return 0; //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double updateLoyaltyAfterHunt(double foodHunted, double foodReceived) {
		return 0; //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Map<String, Double> updateTrustAfterHunt(double foodHunted, double foodReceived) {
                List<String> members = this.getDataModel().getHuntingTeam().getMembers();
                Map<String, Double> newTrustValue = new HashMap<String, Double>();
                double trust;

                if (this.getDataModel().getLastHunted().getName().equals("Stag")){
                        if (foodHunted==0) //Agent has been betrayed
                            trust = -1;
                        else
                            trust = 1;
                }
                else    //Agent hunted rabbit so no trust issues
                    trust = 0;

                if (members.get(0).equals(this.getId())){
                    newTrustValue.put(members.get(1), trust);
                }
                else{
                    newTrustValue.put(members.get(0), trust);
                }

                return newTrustValue;
    }

    @Override
    protected double updateLoyaltyAfterVotes(Proposition proposition, int votes, double overallMovement) {
 		return 0;
		//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double updateHappinessAfterVotes(Proposition proposition, int votes, double overallMovement) {
		return 0;
		//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Map<String, Double> updateTrustAfterVotes(Proposition proposition, int votes, double overallMovement) {
		return null;
		//throw new UnsupportedOperationException("Not supported yet.");
    }
}