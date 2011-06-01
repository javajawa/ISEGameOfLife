package ise.gameoflife.actions;

import ise.gameoflife.models.Food;
import ise.gameoflife.models.HuntingTeam;
import presage.Action;

/**
 * TODO: Documentation for this class
 * @author Benedict
 */
public class GroupOrder implements Action
{
	private final Food toHunt;
	private final HuntingTeam withTeam;

	public GroupOrder(Food toHunt, HuntingTeam withTeam)
	{
		this.toHunt = toHunt;
		this.withTeam = withTeam;
	}

	public Food getToHunt()
	{
		return toHunt;
	}

	public HuntingTeam getTeam()
	{
		return withTeam;
	}

}
