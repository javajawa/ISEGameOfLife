package ise.gameoflife.groups;

import ise.gameoflife.inputs.LeaveNotification.Reasons;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.participants.AbstractGroupAgent;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Benedict
 */
public class TestGroup extends AbstractGroupAgent
{
	private static final long serialVersionUID = 1L;

	@Override
	protected void onInit()
	{
		// Do nothing
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

	@Override
	protected Map<HuntingTeam, Food> selectTeams()
	{
		// TODO: Find food with greatest nitrition / people ratio
		// TODO: Distribute in teams of this size
		// TODO: Order them to hunt that food
		return new HashMap<HuntingTeam, Food>();
	}

	@Override
	protected Map<String, Double> distributeFood(Map<String, Double> gains)
	{
		return gains;
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
	
}
