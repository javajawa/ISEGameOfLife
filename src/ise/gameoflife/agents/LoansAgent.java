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
import java.util.Map;

/**
 *
 * @author george
 */
public class LoansAgent extends AbstractAgent{

    @Override
    protected void onActivate() {
        //Do nothing!
    }

    @Override
    protected void beforeNewRound() {
        //Do nothing!
    }

    @Override
    protected String chooseGroup() {
        //TODO: Reuse the code of TestPoliticalAgent. No change here
        return null;
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
        return null;
    }

    @Override
    protected VoteType castVote(Proposition p) {
        //Do nothing. No proposal = no vote
        return null;
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
        //TODO: Reuse code from TestPoliticalAgent
    }

}
