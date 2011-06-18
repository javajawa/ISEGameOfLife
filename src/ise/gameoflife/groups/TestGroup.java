package ise.gameoflife.groups;

import ise.gameoflife.inputs.LeaveNotification.Reasons;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.models.Tuple;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.tokens.AgentType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benedict
 */
public class TestGroup extends AbstractGroupAgent
{
	private static final long serialVersionUID = 1L;

	@Deprecated
	public TestGroup()
	{
		super();
	}

	public TestGroup(GroupDataInitialiser dm)
	{
		super(dm);
	}

	@Override
	protected void onActivate()
	{
		// Do nothing.
	}

	@Override
	protected boolean respondToJoinRequest(String playerID)
	{
		return true;
	}
	/**
	 * Determines the optimum hunting choice in terms of the food gained/hunters needed ratio
	 * divides the group up into teams of this size, then passes them the order to hunt that food.
	 * If the group number doesn't divide easily, the excess will be put into a smaller team.
	 * This team will still be given the same order.
	 * @return the assignment of teams and which food they are to hunt.
	 */
	@Override
	public List<HuntingTeam> selectTeams()
	{
		ArrayList<HuntingTeam> teams = new ArrayList <HuntingTeam>();
		List<String> members = getDataModel().getMemberList();
		int agents = members.size();

		for(int i=0; i < agents; i += 2){
			int ubound = (i + 2 >= agents) ? agents : i + 2;
			teams.add(new HuntingTeam(members.subList(i, ubound)));
		}

		return teams;
	}

	@Override
	protected void onMemberLeave(String playerID, Reasons reason)
	{
		// Do nothing
	}

	@Override
	protected void beforeNewRound()
	{
		// Do nothing
	}

    @Override
    protected AgentType decideGroupStrategy() {
        return null;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double decideTaxForReservePool() {
        return 0;
    }
    
    @Override
    protected Tuple<AgentType, Double> makePayments()
    {      
        return new Tuple<AgentType, Double>(this.getDataModel().getGroupStrategy(), this.getDataModel().getCurrentReservedFood());
    }    
	
}
