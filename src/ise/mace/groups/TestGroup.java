package ise.mace.groups;

import ise.mace.inputs.LeaveNotification.Reasons;
import ise.mace.models.GroupDataInitialiser;
import ise.mace.models.HuntingTeam;
import ise.mace.models.Tuple;
import ise.mace.participants.AbstractGroupAgent;
import ise.mace.tokens.AgentType;
import ise.mace.tokens.InteractionResult;
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
    protected Tuple<Double, Double> updateTaxedPool(double sharedFood) {
        Tuple<Double, Double> newSharedAndReserve = new Tuple<Double,Double>();
        newSharedAndReserve.add(sharedFood, 0.0);
        return newSharedAndReserve;
    }
    
    @Override
    protected Tuple<AgentType, Double> makePayments()
    {      
        return new Tuple<AgentType, Double>(this.getDataModel().getGroupStrategy(), this.getDataModel().getCurrentReservedFood());
    }

    @Override
    protected Tuple<InteractionResult, Double> interactWithOtherGroups() {
        //throw new UnsupportedOperationException("Not supported yet.");
        Tuple<InteractionResult, Double> interactionResult = new Tuple<InteractionResult, Double>();
        interactionResult.add(InteractionResult.NothingHappened, 0.0);
        return interactionResult;
    }
	
}
