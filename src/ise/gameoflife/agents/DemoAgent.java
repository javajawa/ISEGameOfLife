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
 * @author christopherfonseka
 */
public class DemoAgent extends AbstractAgent {

    @Override
    protected void onActivate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void beforeNewRound() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected String chooseGroup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void groupApplicationResponse(boolean accepted) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Food chooseFood() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected ProposalType makeProposal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected VoteType castVote(Proposition p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Food giveAdvice(String agent, HuntingTeam agentsTeam) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double updateHappinessAfterHunt(double foodHunted, double foodReceived) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double updateLoyaltyAfterHunt(double foodHunted, double foodReceived) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Map<String, Double> updateTrustAfterHunt(double foodHunted, double foodReceived) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double updateLoyaltyAfterVotes(Proposition proposition, int votes, double overallMovement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double updateHappinessAfterVotes(Proposition proposition, int votes, double overallMovement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Map<String, Double> updateTrustAfterVotes(Proposition proposition, int votes, double overallMovement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onInvite(String group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double updateSocialBeliefAfterVotes(Proposition proposition, int votes, double overallMovement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double updateEconomicBeliefAfterVotes(Proposition proposition, int votes, double overallMovement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
