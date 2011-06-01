package ise.gameoflife.groups;

import ise.gameoflife.inputs.LeaveNotification.Reasons;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.GroupDataModel;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.participants.AbstractGroupAgent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public TestGroup(GroupDataModel dm)
	{
		super(dm);
	}

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
	/**
	 * Determines the optimum hunting choice in terms of the food gained/hunters needed ratio
	 * divides the group up into teams of this size, then passes them the order to hunt that food.
	 * If the group number doesn't divide easily, the excess will be put into a smaller team.
	 * This team will still be given the same order.
	 * @return the assignment of teams and which food they are to hunt.
	 */
	@Override
	protected Map<HuntingTeam, Food> selectTeams()
	{
		//Find food with greatest nutrition / hunters required ratio
		Food bestSoFar = null;
		for (Food noms : conn.availableFoods())
		{
			if(bestSoFar == null)
			{
				bestSoFar = noms;
			}			
			if((noms.getNutrition()/noms.getHuntersRequired()) > (bestSoFar.getNutrition()/bestSoFar.getHuntersRequired()))
			{
				bestSoFar = noms;
			}
		}
		//Distribute group into teams of this size
		HashMap<HuntingTeam, Food> map = new HashMap<HuntingTeam, Food>(); 

		List<String> memberList = this.getInternalDataModel().getMemberList();
		int agentsInGroup = memberList.size();
		int huntersInTeam = bestSoFar.getHuntersRequired();

		for(int i=0; i < agentsInGroup; i += huntersInTeam){
			int ubound = (i + huntersInTeam >= agentsInGroup) ? agentsInGroup : i + huntersInTeam;
			HuntingTeam team = new HuntingTeam (memberList.subList(i, ubound));
			map.put(team, bestSoFar);
		}
		//Order them to hunt that food
		return map;
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
