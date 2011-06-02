package ise.gameoflife.actions;

import ise.gameoflife.models.Food;
import ise.gameoflife.models.HuntingTeam;
import presage.Action;

/**
 * An Action performed by 
 * @author Benedict
 */
public class GroupOrder extends GenericAction
{
	private final Food toHunt;
	private final HuntingTeam withTeam;
	private final String agent;

	public GroupOrder(Food toHunt, HuntingTeam withTeam, String agent)
	{
		this.toHunt = toHunt;
		this.withTeam = withTeam;
		this.agent = agent;
	}

	public Food getToHunt()
	{
		return toHunt;
	}

	public HuntingTeam getTeam()
	{
		return withTeam;
	}

	public String getAgent()
	{
		return agent;
	}

}
