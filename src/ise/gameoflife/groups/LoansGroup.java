/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.groups;

import ise.gameoflife.inputs.LeaveNotification.Reasons;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.models.History;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.tokens.AgentType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author george
 */
public class LoansGroup extends AbstractGroupAgent {
    private static final long serialVersionUID = 1L;
    //TODO: 1) Add an abstract inspectOtherGroups() in GroupDataModel. Concrete implementation here.
    // *What if we use public static methods for the histories to keep the framework intact?
    
    @Deprecated
    public LoansGroup() {
    	super();
    }

    public LoansGroup(GroupDataInitialiser dm) {
	super(dm);
    }

    @Override
    protected void onActivate() {
        //Do nothing!
    }

    @Override
    protected boolean respondToJoinRequest(String playerID) {
        //To keep it simple always accept agents no matter what 
        return true;
    }

    private Comparator<String> c = new Comparator<String>() {
            private Random r = new Random(0);
            @Override
            public int compare(String o1, String o2)
            {
                    return (r.nextBoolean() ? -1 : 1);
            }
    };

    @Override
    public List<HuntingTeam> selectTeams()
    {
            ArrayList<HuntingTeam> teams = new ArrayList <HuntingTeam>();
            List<String> members = new ArrayList<String>(getDataModel().getMemberList());
            Collections.sort(members, c);
            int agents = members.size();

            for(int i=0; i < agents; i += 2){
                    int ubound = (i + 2 >= agents) ? agents : i + 2;
                    teams.add(new HuntingTeam(members.subList(i, ubound)));
        }

            return teams;
    }

    @Override
    protected void onMemberLeave(String playerID, Reasons reason) {
        //TODO: Reuse code from TestPoliticalGroup but it doesn't really matter because we don't care about politics
    }

    @Override
    protected AgentType decideGroupStrategy() {
        //TODO: The panel should make a decision. This decision will determine how much food we will spend this round/
        //The amount spent is taken from the reserve pool. Two possibilities: Either groups go hunting and spend energy = food
        //or spend money for public service (build roads, schools, big pointless sculptures etc)
        return null;
    }

    @Override
    protected void beforeNewRound() {
        //TODO: Reuse code from TestPoliticalGroup
    }

    @Override
    protected double decideTaxForReservePool() {
        return 0.1;
    }

}
