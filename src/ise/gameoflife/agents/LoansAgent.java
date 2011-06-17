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

    @Override
    protected Food chooseFood() {
        //TODO: Reuse the code of TestPoliticalAgent. No change here. Probably get rid of advice.
        //The less details we have for agents the better. ABSTRACTION
        return null;
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
}