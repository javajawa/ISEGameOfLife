package ise.gameoflife.inputs;

import ise.gameoflife.models.Food;
import ise.gameoflife.models.HuntingTeam;

/**
 * TODO: Document this class
 * @author Benedict
 */
public class HuntOrder extends GenericInput
{
	private Food toHunt;
	private HuntingTeam team;

	public HuntOrder(long time, Food toHunt, HuntingTeam team)
	{
		super(time, "hunt_order");
		this.toHunt = toHunt;
	}

	public Food getOrder()
	{
		return toHunt;
	}

	public HuntingTeam getTeam()
	{
		return team;
	}

}
