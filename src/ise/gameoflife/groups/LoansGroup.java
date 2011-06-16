/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.groups;

import ise.gameoflife.inputs.LeaveNotification.Reasons;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.tokens.AgentType;
import java.util.List;

/**
 *
 * @author george
 */
public class LoansGroup extends AbstractGroupAgent {

    @Override
    protected void onActivate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected boolean respondToJoinRequest(String playerID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<HuntingTeam> selectTeams() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onMemberLeave(String playerID, Reasons reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected AgentType decideGroupStrategy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void beforeNewRound() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
