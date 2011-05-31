package ise.gameoflife.actions;

import ise.gameoflife.models.Food;
import presage.Action;

/**
 * TODO: Documentation for this class
 * @author Benedict
 */
public class GroupOrder implements Action
{
	private Food toHunt;

	public GroupOrder(Food toHunt)
	{
		this.toHunt = toHunt;
	}

	public Food getToHunt()
	{
		return toHunt;
	}

}
